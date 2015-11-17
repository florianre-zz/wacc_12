package wacc;

import antlr.WACCParser;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import wacc.error.*;

import java.util.ArrayDeque;
import java.util.Deque;

// TODO: do we check if a function has a return function on every branch?

public class WACCTypeChecker extends WACCVisitor<Type> {
  
  private Function currentFunction;
  private Deque<SymbolTable<String, Type>> variableSymbolTableStack;
  
  public WACCTypeChecker(SymbolTable<String, Binding> top,
                         WACCErrorHandler errorHandler) {
    super(top, errorHandler);
    variableSymbolTableStack = new ArrayDeque<>();
  }

  /***************************** Helper Method *******************************/

  private boolean isReadable(Type lhsType) {
    return Type.isInt(lhsType) || Type.isChar(lhsType);
  }

  private boolean isFreeable(Type exprType) {
    return ArrayType.isArray(exprType) || PairType.isPair(exprType);
  }

  private boolean checkTypes(ParserRuleContext ctx, Type lhsType, Type rhsType) {
    if (lhsType != null) {
      if (!lhsType.equals(rhsType)) {
        IncorrectType(ctx, rhsType, lhsType.toString());
        return false;
      }
      return true;
    } else {
      errorHandler.complain(new TypeError(ctx, "Null Type"));
    }
    return false;
  }

  private void IncorrectType(ParserRuleContext ctx,
                             Type exprType,
                             String expectedType) {
    String actual = exprType != null ? exprType.toString() : "'null'";
    errorHandler.complain(
        new TypeAssignmentError(ctx, expectedType, actual)
    );
  }

  private void changeWorkingSymbolTableTo(String scopeName) {
    NewScope b = (NewScope) workingSymbolTable.lookupAll(scopeName);
    if (b != null) {
      workingSymbolTable = (SymbolTable<String, Binding>) b.getSymbolTable();
    }
  }

  private void pushEmptyVariableSymbolTable() {
    SymbolTable<String, Type> scope = new SymbolTable<>();
    variableSymbolTableStack.push(scope);
  }

  private void popCurrentScopeVariableSymbolTable() {
    variableSymbolTableStack.pop();
  }

  private void addVariableToCurrentScope(String name, Type type) {
    SymbolTable<String, Type> current = variableSymbolTableStack.peek();
    current.put(name, type);
  }

  private Type getMostRecentBindingForVariable(String varname) {
    // keep looking up the variable down the stack, then return null if not found
    for (SymbolTable<String, Type> symbolTable : variableSymbolTableStack) {
      if (symbolTable.containsKey(varname)) {
        return symbolTable.get(varname);
      }
    }
    return null;
  }

  public Type lookupTypeInWorkingSymbolTable(String key) {
    Binding b = workingSymbolTable.lookupAll(key);
    if (b instanceof Variable) {
      return ((Variable) b).getType();
    }
    if (b instanceof Function) {
      return ((Function) b).getType();
    }
    return null;
  }

  /************************** Visit Functions ****************************/

  /**
  * prog: BEGIN func* main END EOF;
  * change scope
  * visit children, to type check children
  */
  @Override
  public Type visitProg(@NotNull WACCParser.ProgContext ctx) {
    String scopeName = Scope.PROG.toString();
    changeWorkingSymbolTableTo(scopeName);
    visitChildren(ctx);
    workingSymbolTable = workingSymbolTable.getEnclosingST();
    return null;
  }

  /**
   * main: statList;
   * change scope to 0main
   * push new variable scope
   * type check children
   * revert to enclosing scope
   * pop variable scope
   */
  @Override
  public Type visitMain(@NotNull WACCParser.MainContext ctx) {
    String scopeName = Scope.MAIN.toString();
    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSymbolTable();

    Type type = visitStatList(ctx.statList());

    workingSymbolTable = workingSymbolTable.getEnclosingST();
    popCurrentScopeVariableSymbolTable();

    return type;
  }

  /************************** Functions ****************************/

  /**
  * func: type funcName ( (paramList)? ) IS body END;
  * get return type of function
  * change scope to function
  * push new variable scope
  * visit Params, to type check
  * visit body to type check
  * return type check is deferred
  * revert to enclosing scope
  * pop variable scope
  */
  @Override
  public Type visitFunc(@NotNull WACCParser.FuncContext ctx) {
    String funcName = ctx.funcName.getText();
    currentFunction = (Function) workingSymbolTable.lookupAll(funcName);
    Type expectedReturnType = currentFunction.getType();

    changeWorkingSymbolTableTo(ctx.funcName.getText());
    pushEmptyVariableSymbolTable();

    // TODO: check if this is needed
    if (expectedReturnType == null) {
      TypeError error = new TypeError(ctx);
      errorHandler.complain(error);
    }

    if (ctx.paramList() != null) {
      visitParamList(ctx.paramList());
    }
    visitStatList(ctx.statList());

    goUpWorkingSymbolTable();
    popCurrentScopeVariableSymbolTable();

    return expectedReturnType;
  }

  /**
  * param: type name;
  * look up the type in the symbol table
  * add to current variable scope
  * return the type
  */
  @Override
  public Type visitParam(@NotNull WACCParser.ParamContext ctx) {
    Type type = lookupTypeInWorkingSymbolTable(ctx.ident().getText());

    // Add the param to the current variable scope symbol table
    addVariableToCurrentScope(ctx.ident().getText(), type);

    return type;
  }

  /************************** Statements ****************************/

  /**
  * type varName EQUALS assignRHS
  * get lhs & rhs types
  * check that they are equal
  *  - if it is a pair, check the inner types */
  @Override
  public Type visitInitStat(@NotNull WACCParser.InitStatContext ctx) {
    Type lhsType = lookupTypeInWorkingSymbolTable(ctx.ident().getText());
    Type rhsType = visitAssignRHS(ctx.assignRHS());

    addVariableToCurrentScope(ctx.ident().getText(), lhsType);
    checkTypes(ctx, lhsType, rhsType);

    return lhsType;
  }

  /**
   * assignLHS EQUALS assignRHS
   * get lhs & rhs types
   * check that they are equal
   *  - if it is a pair, check the inner types
   */
  @Override
  public Type visitAssignStat(WACCParser.AssignStatContext ctx) {
    Type lhsType = visitAssignLHS(ctx.assignLHS());
    Type rhsType = visitAssignRHS(ctx.assignRHS());

    checkTypes(ctx, lhsType, rhsType);

    return lhsType;
  }

  /**
  * READ assignLHS
  * type check isReadable */
  @Override
  public Type visitReadStat(@NotNull WACCParser.ReadStatContext ctx) {
    Type lhsType = visitAssignLHS(ctx.assignLHS());

    if (!isReadable(lhsType)) {
      errorHandler.complain(
          new ReadTypeAssignmentError(ctx, lhsType.toString()));
    }

    return lhsType;
  }

  /**
  * FREE expr
  * type check isFreeable */
  @Override
  public Type visitFreeStat(@NotNull WACCParser.FreeStatContext ctx) {
    Type exprType = visitExpr(ctx.expr());

    if (!isFreeable(exprType)) {
      errorHandler.complain(
          new FreeTypeAssignmentError(ctx, exprType.toString()));
    }

    return exprType;
  }

  /**
  * EXIT expr
  * type check: int */
  @Override
  public Type visitExitStat(@NotNull WACCParser.ExitStatContext ctx) {
    Type exprType = visitExpr(ctx.expr());

    if (!Type.isInt(exprType)) { // exit codes are Integers
      errorHandler.complain(
          new ExitTypeAssignmentError(ctx, exprType.toString()));
    }

    return exprType;
  }

  /**
  * RETURN expr
  * get type of expr
  * get the type of the current (function) scope
  * check both are equal */
  @Override
  public Type visitReturnStat(@NotNull WACCParser.ReturnStatContext ctx) {

    Type actualReturnType = visitExpr(ctx.expr());
    Type expectedReturnType = currentFunction.getType();
    checkTypes(ctx, expectedReturnType, actualReturnType);

    return expectedReturnType;
  }

  /**
  * IF predicate THEN thenStat ELSE elseStat FI
  * type check predicate is bool
  * change scope to thenStat
  * visit thenStat
  * reset scope to enclosing table
  * change scope to elseStat
  * visit elseStat
  * reset scope to enclosing table */
  @Override
  public Type visitIfStat(@NotNull WACCParser.IfStatContext ctx) {
    Type predicateType = visitExpr(ctx.expr());

    if (!Type.isBool(predicateType)) {
      errorHandler.complain(
          new TypeAssignmentError(ctx, "'bool'", predicateType.getName()));
    }

    String scopeName = Scope.THEN.toString() + ++ifCount;
    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSymbolTable();
    visitStatList(ctx.thenStat);
    workingSymbolTable = workingSymbolTable.getEnclosingST();
    popCurrentScopeVariableSymbolTable();

    scopeName = Scope.ELSE.toString() + ifCount;
    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSymbolTable();
    visitStatList(ctx.elseStat);
    workingSymbolTable = workingSymbolTable.getEnclosingST();
    popCurrentScopeVariableSymbolTable();


    return null;
  }

  /** WHILE expr DO body DONE
  * type check predicate is bool
  * change scope to body
  * visit body
  * reset scope to enclosing table */
  @Override
  public Type visitWhileStat(@NotNull WACCParser.WhileStatContext ctx) {

    Type predicateType = visitExpr(ctx.expr());

    if (!Type.isBool(predicateType)) {
      IncorrectType(ctx, predicateType, "'bool'");
    }

    String scopeName = Scope.WHILE.toString() + ++whileCount;
    changeWorkingSymbolTableTo(scopeName);

    pushEmptyVariableSymbolTable();

    visitStatList(ctx.statList());

    workingSymbolTable = workingSymbolTable.getEnclosingST();

    popCurrentScopeVariableSymbolTable();


    return null;
  }

  /**
   * BEGIN body END
   * change scope to body
   * visit body
   * reset scope to enclosing table
   */
  @Override
  public Type visitBeginStat(@NotNull WACCParser.BeginStatContext ctx) {

    String scopeName = Scope.BEGIN.toString() + ++beginCount;
    changeWorkingSymbolTableTo(scopeName);

    pushEmptyVariableSymbolTable();

    Type statListType = visitStatList(ctx.statList());
    workingSymbolTable = workingSymbolTable.getEnclosingST();

    popCurrentScopeVariableSymbolTable();

    return statListType;
  }

  // Statement Helpers

  /**
   * NEW_PAIR (first , second)
   * return pair type that has two subTypes where
   *  - fst = first.type
   *  - snd = second.type
   */
  @Override
  public Type visitNewPair(@NotNull WACCParser.NewPairContext ctx) {
    Type fstType = visitExpr(ctx.first);
    Type sndType = visitExpr(ctx.second);
    return new PairType(fstType, sndType);
  }

  /**
   * CALL funcName ( (argList)? )
   * type check each argument
   * return the functions return type
   */
  @Override
  public Type visitCall(@NotNull WACCParser.CallContext ctx) {
    Function calledFunction = getCalledFunction(ctx);
    WACCParser.ArgListContext argListContext = ctx.argList();
    if (argListContext != null) {
      if (argListContext.expr().size() == calledFunction.getParams().size()) {
        for (int i = 0; i < argListContext.expr().size(); i++) {
          WACCParser.ExprContext exprCtx = argListContext.expr(i);
          Type actualType = visitExpr(exprCtx);
          Type expectedType = calledFunction.getParams().get(i).getType();
          checkTypes(ctx, actualType, expectedType);
        }
      } else {
        StringBuilder sb = new StringBuilder();
        sb.append("The number of arguments doesn't match function declaration: ");

        sb.append(ctx.getText()).append("\n");
        sb.append("There are currently ").append(argListContext.expr().size());
        sb.append(" params, there should be ");
        sb.append(calledFunction.getParams().size());

        String errorMsg = sb.toString();
        errorHandler.complain(new DeclarationError(ctx, errorMsg));
      }

    } else if (calledFunction.getParams().size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("The number of arguments doesn't match function declaration: ");

      sb.append(ctx.getText()).append("\n");
      sb.append("There are currently no");
      sb.append(" params, there should be ");
      sb.append(calledFunction.getParams().size());

      String errorMsg = sb.toString();
      errorHandler.complain(new DeclarationError(ctx, errorMsg));
    }

    return visitIdent(ctx.ident());
  }

  /**
   * arrayLitr: [(T, T, T, ...)?];
   * check all types are the same
   * else return null
   */
  @Override
  public Type visitArrayLitr(@NotNull WACCParser.ArrayLitrContext ctx) {

    if (ctx.expr() != null && ctx.expr().size() != 0) {
      Type firstType = visitExpr(ctx.expr(0));
      for (WACCParser.ExprContext exprCtx : ctx.expr()) {
        Type currentType = visitExpr(exprCtx);
        if (!currentType.equals(firstType)) {
          return null;
        }
      }
      return new ArrayType(firstType);
    }

    return new ArrayType();
  }

  // Expressions

  /**
   * intExpr: (CHR)? (sign)? INTEGER
   * check if it is a char (if CHR is set)
   * otherwise it should be an int
   */
  @Override
  public Type visitInteger(@NotNull WACCParser.IntegerContext ctx) {
    if (ctx.CHR() != null) {
      return getType(Types.CHAR_T);
    }

    // TODO: Refactor into function
    Type type = getType(Types.INT_T);

    long intValue = Long.valueOf(ctx.INTEGER().getText());
    if (ctx.sign() != null) {
      intValue *= -1;
    }

    if (!(type.getMin() <= intValue && intValue <= type.getMax())) {
      String errorMsg = "Integer Overflow";
      errorHandler.complain(new SyntaxError(ctx, errorMsg));
    }

    return type;
  }

  /**
   * boolExpr: (NOT)? boolLitr
   * check if literal is bool
   */
  @Override
  public Type visitBool(@NotNull WACCParser.BoolContext ctx) {
    return getType(Types.BOOL_T);
  }

  /**
   * (ORD)? CHARACTER
   * check if it is an int (if ORD is set)
   * otherwise it should be a char
   */
  @Override
  public Type visitCharacter(WACCParser.CharacterContext ctx) {
    if (ctx.ORD() != null) {
      return getType(Types.INT_T);
    }
    return getType(Types.CHAR_T);
  }

  /**
   * stringExpr: STRING
   * it is of type string (parser asserts this)
   */
  @Override
  public Type visitString(@NotNull WACCParser.StringContext ctx) {
    return getType(Types.STRING_T);
  }

  /**
   * arrayExpr: (LEN)? arrayElem
   * if we are checking the length of the array
   *  - we return an int
   * otherwise
   * check that ident is of type array
   *  - return the inner type
   */
  @Override
  public Type visitArray(@NotNull WACCParser.ArrayContext ctx) {
    if (ctx.LEN() != null) {
      return getType(Types.INT_T);
    }
    return visitArrayElem(ctx.arrayElem());
  }

  /**
   * pairLitr: NULL;
   * return null
   */
  @Override
  public Type visitPairLitr(@NotNull WACCParser.PairLitrContext ctx) {
    return new PairType();
  }

  // Expression Helpers

  /**
   * arrayElem: varName[expr];
   *
   *  int[][][] a;
   *  b = a[]
   *  :t b = int[][]
   *
   * check that each expr evaluates to an int
   */
  @Override
	public Type visitArrayElem(@NotNull WACCParser.ArrayElemContext ctx) {
    for (WACCParser.ExprContext expr : ctx.expr()) {
      Type index = visitExpr(expr);
      if (index != null && !Type.isInt(index)) {
        errorHandler.complain(new TypeError(ctx));
      }
    }

    Type type = visitIdent(ctx.ident());
    // TODO: clean up
    if (ArrayType.isArray(type)) {
      int wantedDimensionality = ctx.OPEN_BRACKET().size();
      if (Type.isString(type)) {
        if (wantedDimensionality != 1) {
          errorHandler.complain(new TypeError(ctx, "String is 1D"));
        } else {
          return new Type(Types.CHAR_T);
        }
      } else {
        ArrayType arrayType = (ArrayType) type;
        int totalDimensionality = arrayType.getDimensionality();
        if (wantedDimensionality > totalDimensionality) {
          errorHandler.complain(new TypeError(ctx));
        } else {
          int returnDimensionality = totalDimensionality-wantedDimensionality;
          if (returnDimensionality == 0) {
            return arrayType.getBase();
          } else {
            return new ArrayType(arrayType.getBase(), returnDimensionality);
          }
        }
      }
    }

    IncorrectType(ctx, type, "'T[]'");

		return null;
	}

  /**
   * pairElem: (FST | SND) IDENT;
   * check that ident is a pair
   * if fst is set
   *   return type of the first element
   * otherwise (snd)
   *   return type of the second element
   */
  @Override
  public Type visitPairElem(@NotNull WACCParser.PairElemContext ctx) {
    Type varType = visitIdent(ctx.ident());

    Type returnType = null;

    if (checkTypes(ctx, new PairType(), varType)) {
      PairType pairType = (PairType) varType;

      if (ctx.FST() != null) {
        returnType = pairType.getFst();
      } else if (ctx.SND() != null) {
        returnType = pairType.getSnd();
      }
    }

    return returnType;
  }

  // Operations

  /**
   * unaryOper: (! | - | len | ord | chr)? ( IDENT | (expr) );
   * if ident
   *   check ident is a variable
   *   get its type
   * otherwise (expr)
   *   visit to get type
   * for each unary operator check
   *   return the result type of the unary operator
   * otherwise
   *   return type of expression (as no unary operator)
   */
  @Override
  // TODO: shorten! (with a sense of urgency) - but last!!!
	public Type visitUnaryOper(@NotNull WACCParser.UnaryOperContext ctx) {
    Type exprType = null;

    if (ctx.ident() != null) {
      exprType = visitIdent(ctx.ident());
    } else if (ctx.expr() != null) {
      exprType = visitExpr(ctx.expr());
    }

    if (ctx.NOT() != null) {
      if (!Type.isBool(exprType)) {
        IncorrectType(ctx, exprType, "'bool'");
      }
      return getType(Types.BOOL_T);
    }
    else if (ctx.MINUS() != null) {
      if (!Type.isInt(exprType)) {
        IncorrectType(ctx, exprType, "'int'");
      }
      return getType(Types.INT_T);
    }
    else if (ctx.LEN() != null) {
      if (!ArrayType.isArray(exprType)) {
        IncorrectType(ctx, exprType, "'T[]'");
      }
      return getType(Types.INT_T);
    }
    else if (ctx.ORD() != null) {
      if (!Type.isChar(exprType)) {
        IncorrectType(ctx, exprType, "'char'");
      }
      return getType(Types.INT_T);
    }
    else if (ctx.CHR() != null) {
      if (!Type.isInt(exprType)) {
        IncorrectType(ctx, exprType, "'int'");
      }
      return getType(Types.CHAR_T);
    }

		return exprType;
	}

  /**
   * logicalOper: first ((AND | OR) otherExprs)*
   * if there are no otherExprs
   *  - return type of first
   * otherwise
   *  - check all exprs are bool(s)
   */
  @Override
  public Type visitLogicalOper(@NotNull WACCParser.LogicalOperContext ctx) {
    if (ctx.otherExprs.size() > 0) {
      for (WACCParser.ComparisonOperContext operCtx : ctx.comparisonOper()) {
        Type type = visitComparisonOper(operCtx);
        if (!Type.isBool(type)) {
          IncorrectType(operCtx, type, "'bool'");
        }
      }
      return getType(Types.BOOL_T);
    } else {
      return visitComparisonOper(ctx.first);
    }
  }

  @Override
  public Type visitComparisonOper(@NotNull WACCParser.ComparisonOperContext ctx) {
    if (ctx.orderingOper() != null) {
      return visitOrderingOper(ctx.orderingOper());
    } else if (ctx.equalityOper() != null) {
      return visitEqualityOper(ctx.equalityOper());
    }
    return null;
  }

  /**
   * orderingOper: first ((GT | GTE | LT | LTE) second)?
   * if there is no second
   *  - return type of first
   * otherwise
   *  - check both are int(s) or both are char(s)
   */
  @Override
  public Type visitOrderingOper(@NotNull WACCParser.OrderingOperContext ctx) {
    if (ctx.second != null) {
      Type fstType = visitArithmeticOper(ctx.first);
      Type sndType = visitArithmeticOper(ctx.second);

      if (!fstType.equals(sndType)) {
        errorHandler.complain(new TypeError(ctx.first));
        errorHandler.complain(new TypeError(ctx.second));
      } else if (!(Type.isInt(fstType) || Type.isChar(fstType))) {
        IncorrectType(ctx.first, fstType, "'int' or 'char'");
        IncorrectType(ctx.second, sndType, "'int' or 'char'");
      }
      return getType(Types.BOOL_T);
    } else {
      return visitArithmeticOper(ctx.first);
    }
  }

  /**
   * equalityOper: first ((EQ | NE) second)?
   * if there is no second
   *  - return type of first
   * otherwise
   *  - check both are the same type
   */
  @Override
  public Type visitEqualityOper(@NotNull WACCParser.EqualityOperContext ctx) {
    if (ctx.second != null) {
      Type fstType = visitArithmeticOper(ctx.first);
      Type sndType = visitArithmeticOper(ctx.second);

      if (!fstType.equals(sndType)) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Type of ");
        stringBuilder.append(ctx.first.getText());
        stringBuilder.append("(").append(fstType).append(")");
        stringBuilder.append(" and ");
        stringBuilder.append(ctx.second.getText());
        stringBuilder.append("(").append(sndType).append(")");
        stringBuilder.append(" do not match");
        String errorMsg = stringBuilder.toString();
        errorHandler.complain(new TypeError(ctx.first, errorMsg));
      }

      return getType(Types.BOOL_T);
    } else {
      return visitArithmeticOper(ctx.first);
    }
  }

  /**
   * arithmeticOper: first ((MUL | DIV | MOD | PLUS | MINUS) otherExprs)*;
   * if there are no otherExprs
   *  - return type of first
   * otherwise
   *  - check all exprs are int(s)
   */
  @Override
  public Type visitArithmeticOper(
      @NotNull WACCParser.ArithmeticOperContext ctx) {
    if (ctx.otherExprs.size() > 0) {
      for (WACCParser.AtomContext atomCtx : ctx.atom()) {
        Type type = visitAtom(atomCtx);
        if (!Type.isInt(type)) {
          IncorrectType(atomCtx, type, "'int'");
        }
      }
      return getType(Types.INT_T);
    } else {
      return visitChildren(ctx);
    }
  }

  @Override
  public Type visitAtom(@NotNull WACCParser.AtomContext ctx) {

    if (ctx.integer() != null) {
      return visitInteger(ctx.integer());
    } else if (ctx.bool() != null) {
      return visitBool(ctx.bool());
    } else if (ctx.character() != null) {
      return visitCharacter(ctx.character());
    } else if (ctx.string() != null) {
      return visitString(ctx.string());
    } else if (ctx.array() != null) {
      return visitArray(ctx.array());
    } else if (ctx.pairLitr() != null) {
      return visitPairLitr(ctx.pairLitr());
    } else if (ctx.unaryOper() != null) {
      return visitUnaryOper(ctx.unaryOper());
    }

    return null;
  }

  //Other
  /**
   * IDENT
   * lookup and return the type
   */
  @Override
  public Type visitIdent(WACCParser.IdentContext ctx) {

    // Assume ident is a variable and get its most local type
    Type local = getMostRecentBindingForVariable(ctx.getText());
    if (local != null) {
      return local;
    }
    // Not a variable (it's a function), so look up in working symbol table
    return lookupTypeInWorkingSymbolTable(ctx.getText());
  }

}

package wacc;

import antlr.WACCParser;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import wacc.error.*;

// TODO: do we check if a function has a return function on every branch? *
// TODO: who checks if an int assignment is too large? *
// * (run: /src/test/test)

public class WACCTypeChecker extends WACCVisitor<Type> {
  
  private Function currentFunction;
  
  public WACCTypeChecker(SymbolTable<String, Binding> top,
                         WACCErrorHandler errorHandler) {
    super(top, errorHandler);
  }

  // Helper Methods

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

  // Visit Methods

  /**
  * prog: BEGIN func* main END EOF;
  * change Scope
  * visit children, to type check children */
  @Override
  public Type visitProg(@NotNull WACCParser.ProgContext ctx) {
    String scopeName =Scope.PROG.toString();
    changeWorkingSymbolTableTo(scopeName);
    visitChildren(ctx);
    workingSymbolTable = workingSymbolTable.getEnclosingST();
    return null;
  }

  //  Functions

  /**
   * main: statList;
   * change scope to 0main
   * type check children */
  @Override
  public Type visitMain(@NotNull WACCParser.MainContext ctx) {
    String scopeName = Scope.MAIN.toString();
    changeWorkingSymbolTableTo(scopeName);
    Type type = visitStatList(ctx.statList());
    workingSymbolTable = workingSymbolTable.getEnclosingST();
    return type;
  }

  /**
  * func: type funcName ( (paramList)? ) IS body END;
  * get return type of function
  * change scope to function
  * visit Params, to type check
  * visit body to type check
  * return type check is deferred */
  @Override
  public Type visitFunc(@NotNull WACCParser.FuncContext ctx) {
    String funcName = ctx.funcName.getText();
    currentFunction = (Function) workingSymbolTable.lookupAll(funcName);
    Type expectedReturnType = currentFunction.getType();
    changeWorkingSymbolTableTo(ctx.funcName.getText());

    // TODO: check if this is needed
    if (expectedReturnType == null) {
      TypeError error = new TypeError(ctx);
      errorHandler.complain(error);
    }

    if (ctx.paramList() != null) {
      visitParamList(ctx.paramList());
    }
    visitStatList(ctx.statList());

    //TODO: there is a function to do this
    workingSymbolTable = workingSymbolTable.getEnclosingST();

    return expectedReturnType;
  }

  /**
  * param: type name;
  * check type is a valid Type
  * returns null if not valid */
  @Override
  public Type visitParam(@NotNull WACCParser.ParamContext ctx) {
    Type type = visitIdent(ctx.ident());
    // TODO: check if this is needed
    if (type == null) {
      TypeError error = new TypeError(ctx);
      errorHandler.complain(error);
    }

    return type;
  }

  // Statements

  /**
  * type varName EQUALS assignRHS
  * get lhs & rhs types
  * check that they are equal
  *  - if it is a pair, check the inner types */
  @Override
    public Type visitInitStat(@NotNull WACCParser.InitStatContext ctx) {
      Type lhsType = visitIdent(ctx.ident());
      Type rhsType = visitAssignRHS(ctx.assignRHS());

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
    visitStatList(ctx.thenStat);
    workingSymbolTable = workingSymbolTable.getEnclosingST();

    scopeName = Scope.ELSE.toString() + ifCount;
    changeWorkingSymbolTableTo(scopeName);
    visitStatList(ctx.elseStat);
    workingSymbolTable = workingSymbolTable.getEnclosingST();

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
    visitStatList(ctx.statList());
    workingSymbolTable = workingSymbolTable.getEnclosingST();

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
    Type statListType = visitStatList(ctx.statList());
    workingSymbolTable = workingSymbolTable.getEnclosingST();
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
      return (Type) top.lookupAll(Types.CHAR_T.toString());
    }

    // TODO: Refactor into function
    Type type = (Type) top.lookupAll(Types.INT_T.toString());

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
    return (Type) top.lookupAll(Types.BOOL_T.toString());
  }

  /**
   * (ORD)? CHARACTER
   * check if it is an int (if ORD is set)
   * otherwise it should be a char
   */
  @Override
  public Type visitCharacter(WACCParser.CharacterContext ctx) {
    if (ctx.ORD() != null) {
      return (Type) top.lookupAll(Types.INT_T.toString());
    }
    return (Type) top.lookupAll(Types.CHAR_T.toString());
  }

  /**
   * stringExpr: STRING
   * it is of type string (parser asserts this)
   */
  @Override
  public Type visitString(@NotNull WACCParser.StringContext ctx) {
    return (Type) top.lookupAll(Types.STRING_T.toString());
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
      return (Type) top.lookupAll(Types.INT_T.toString());
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
      return (Type) top.lookupAll(Types.BOOL_T.toString());
    }
    else if (ctx.MINUS() != null) {
      if (!Type.isInt(exprType)) {
        IncorrectType(ctx, exprType, "'int'");
      }
      return (Type) top.lookupAll(Types.INT_T.toString());
    }
    else if (ctx.LEN() != null) {
      if (!ArrayType.isArray(exprType)) {
        IncorrectType(ctx, exprType, "'T[]'");
      }
      return (Type) top.lookupAll(Types.INT_T.toString());
    }
    else if (ctx.ORD() != null) {
      if (!Type.isChar(exprType)) {
        IncorrectType(ctx, exprType, "'char'");
      }
      return (Type) top.lookupAll(Types.INT_T.toString());
    }
    else if (ctx.CHR() != null) {
      if (!Type.isInt(exprType)) {
        IncorrectType(ctx, exprType, "'int'");
      }
      return (Type) top.lookupAll(Types.CHAR_T.toString());
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
      return (Type) top.lookupAll(Types.BOOL_T.toString());
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
      return (Type) top.lookupAll(Types.BOOL_T.toString());
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

      return (Type) top.lookupAll(Types.BOOL_T.toString());
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
      return (Type) top.lookupAll(Types.INT_T.toString());
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
    Binding b = workingSymbolTable.lookupAll(ctx.getText());
    if (b instanceof Variable) {
      return ((Variable) b).getType();
    }
    if (b instanceof Function) {
      return ((Function) b).getType();
    }
    return null;
  }

}

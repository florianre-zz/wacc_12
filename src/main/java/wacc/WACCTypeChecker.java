package wacc;

import antlr.WACCParser;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import wacc.error.*;

import java.util.List;

public class WACCTypeChecker extends WACCVisitor<Type> {

  private Function currentFunction;

  public WACCTypeChecker(SymbolTable<String, Binding> top,
                         WACCErrorHandler errorHandler) {
    super(top, errorHandler);
  }

  /***************************** Helper Method *******************************/

  private boolean isReadable(Type lhsType) {
    return Type.isInt(lhsType) || Type.isChar(lhsType);
  }

  private boolean isFreeable(Type exprType) {
    return ArrayType.isArray(exprType) || PairType.isPair(exprType);
  }

  private boolean checkTypesEqual(ParserRuleContext ctx,
                                  Type lhsType, Type rhsType) {
    if (lhsType != null) {
      if (!lhsType.equals(rhsType)) {
        incorrectType(ctx, rhsType, lhsType.toString());
        return false;
      }
      return true;
    } else {
      errorHandler.complain(new TypeError(ctx, "Null Type"));
    }
    return false;
  }

  private void incorrectType(ParserRuleContext ctx,
                             Type exprType,
                             String expectedType) {
    String actual = exprType != null ? exprType.toString() : "'null'";
    errorHandler.complain(
        new TypeAssignmentError(ctx, expectedType, actual)
    );
  }

  private Type checkAllTypesEqual(List<? extends WACCParser.ExprContext> ctxs) {
    Type firstType = visitExpr(ctxs.get(0));
    for (WACCParser.ExprContext ctx : ctxs) {
      Type currentType = visitExpr(ctx);
      if (!currentType.equals(firstType)) {
        errorHandler.complain(new TypeError(ctx, "Inconsistent array types"
                + "of array values"));
        return firstType;
      }
    }
    return firstType;
  }

  private void inconsistentParamCountError(WACCParser.CallContext ctx,
                                           int expectedSize, int actualSize) {
    StringBuilder sb = new StringBuilder();
    sb.append("The number of arguments doesn't match function declaration: ");

    sb.append(ctx.getText()).append("\n");
    sb.append("There are currently ").append(actualSize);
    sb.append(" params, there should be ");
    sb.append(expectedSize);

    String errorMsg = sb.toString();
    errorHandler.complain(new DeclarationError(ctx, errorMsg));
  }

  private void checkArrayElemExpressions(
      List<? extends WACCParser.ExprContext> exprs) {
    for (WACCParser.ExprContext expr : exprs) {
      Type index = visitExpr(expr);
      if (!Type.isInt(index)) {
        errorHandler.complain(new TypeError(expr));
      }
    }
  }

  private Type lookupTypeInWorkingSymbolTable(String key) {
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
  public Type visitProg(WACCParser.ProgContext ctx) {
    String scopeName = Scope.PROG.toString();
    changeWorkingSymbolTableTo(scopeName);
    visitChildren(ctx);
    goUpWorkingSymbolTable();
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
  public Type visitMain(WACCParser.MainContext ctx) {
    String scopeName = Scope.MAIN.toString();
    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSet();
    currentFunction = null;

    Type type = visitStatList(ctx.statList());
    
    goUpWorkingSymbolTable();
    popCurrentScopeVariableSet();

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
  public Type visitFunc(WACCParser.FuncContext ctx) {
    String funcName = ScopeType.FUNCTION_SCOPE + ctx.funcName.getText();
    currentFunction = (Function) workingSymbolTable.lookupAll(funcName);
    Type expectedReturnType = currentFunction.getType();

    changeWorkingSymbolTableTo(funcName);
    pushEmptyVariableSet();

    if (ctx.paramList() != null) {
      visitParamList(ctx.paramList());
    }
    visitStatList(ctx.statList());

    goUpWorkingSymbolTable();
    popCurrentScopeVariableSet();

    return expectedReturnType;
  }

  /**
  * param: type name;
  * look up the type in the symbol table
  * add to current variable scope
  * return the type
  */
  @Override
  public Type visitParam(WACCParser.ParamContext ctx) {
    Type type = lookupTypeInWorkingSymbolTable(ctx.ident().getText());

    // Add the param to the current variable scope symbol table
    addVariableToCurrentScope(ctx.ident().getText());

    return type;
  }

  /************************** Statements ****************************/

  /**
  * type varName EQUALS assignRHS
  * get lhs & rhs types
  * check that they are equal
  *  - if it is a pair, check the inner types
  */
  @Override
  public Type visitInitStat(WACCParser.InitStatContext ctx) {
    Type lhsType = lookupTypeInWorkingSymbolTable(ctx.ident().getText());
    Type rhsType = visitAssignRHS(ctx.assignRHS());

    addVariableToCurrentScope(ctx.ident().getText());
    checkTypesEqual(ctx, lhsType, rhsType);

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

    checkTypesEqual(ctx, lhsType, rhsType);

    return lhsType;
  }

  @Override
  public Type visitAssignLHS(WACCParser.AssignLHSContext ctx) {

    return super.visitAssignLHS(ctx);
  }

  /**
  * READ assignLHS
  * type check isReadable */
  @Override
  public Type visitReadStat(WACCParser.ReadStatContext ctx) {
    Type lhsType = visitAssignLHS(ctx.assignLHS());

    if (!isReadable(lhsType)) {
      errorHandler.complain(
          new ReadTypeAssignmentError(ctx, lhsType.toString()));
    }

    ctx.assignLHS().returnType = visitAssignLHS(ctx.assignLHS());

    return lhsType;
  }

  /**
  * FREE expr
  * type check isFreeable */
  @Override
  public Type visitFreeStat(WACCParser.FreeStatContext ctx) {
    Type exprType = visitExpr(ctx.expr());

    if (!isFreeable(exprType)) {
      errorHandler.complain(
          new FreeTypeAssignmentError(ctx, exprType.toString()));
    }

    return exprType;
  }

  @Override
  public Type visitPrintStat(WACCParser.PrintStatContext ctx) {
    ctx.expr().returnType = visitExpr(ctx.expr());
    return super.visitPrintStat(ctx);
  }

  /**
  * EXIT expr
  * type check: int */
  @Override
  public Type visitExitStat(WACCParser.ExitStatContext ctx) {
    Type exprType = visitExpr(ctx.expr());

    if (!Type.isInt(exprType)) {// exit codes are Integers
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
  public Type visitReturnStat(WACCParser.ReturnStatContext ctx) {
    Type actualReturnType = visitExpr(ctx.expr());
    if (currentFunction != null) {
      Type expectedReturnType = currentFunction.getType();
      checkTypesEqual(ctx, expectedReturnType, actualReturnType);
      return expectedReturnType;
    }

    return null;
  }

  /**
  * IF predicate THEN thenStat ELSE elseStat FI
  * type check predicate is bool
  * change scope to thenStat
  * push new variable scope
  * visit thenStat
  * reset scope to enclosing table
  * pop variable scope
  * change scope to elseStat
  * push new variable scope
  * visit elseStat
  * reset scope to enclosing table
  * pop variable scope */
  @Override
  public Type visitIfStat(WACCParser.IfStatContext ctx) {
    Type predicateType = visitExpr(ctx.expr());

    if (!Type.isBool(predicateType)) {
      errorHandler.complain(
          new TypeAssignmentError(ctx, "'bool'", predicateType.getName()));
    }

    String scopeName = Scope.THEN.toString() + ++ifCount;
    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSet();
    visitStatList(ctx.thenStat);
    goUpWorkingSymbolTable();
    popCurrentScopeVariableSet();

    scopeName = Scope.ELSE.toString() + ifCount;
    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSet();
    visitStatList(ctx.elseStat);
    goUpWorkingSymbolTable();
    popCurrentScopeVariableSet();

    return null;
  }

  /** WHILE expr DO body DONE
  * type check predicate is bool (or evaluates to it)
  * change scope to body
  * push new variable scope
  * visit body
  * reset scope to enclosing table
  * pop variable scope */
  @Override
  public Type visitWhileStat(WACCParser.WhileStatContext ctx) {
    Type predicateType = visitExpr(ctx.expr());

    if (!Type.isBool(predicateType)) {
      incorrectType(ctx, predicateType, "'bool'");
    }

    String scopeName = Scope.WHILE.toString() + ++whileCount;
    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSet();

    visitStatList(ctx.statList());

    goUpWorkingSymbolTable();
    popCurrentScopeVariableSet();

    return null;
  }

  /**
   * BEGIN body END
   * change scope to body
   * push new variable scope
   * visit body
   * reset scope to enclosing table
   * pop variable scope
   */
  @Override
  public Type visitBeginStat(WACCParser.BeginStatContext ctx) {

    String scopeName = Scope.BEGIN.toString() + ++beginCount;
    changeWorkingSymbolTableTo(scopeName);

    pushEmptyVariableSet();

    Type statListType = visitStatList(ctx.statList());
    goUpWorkingSymbolTable();

    popCurrentScopeVariableSet();

    return statListType;
  }

  /**
   * NEW_PAIR (first , second)
   * return pair type that has two subTypes where
   *  - fst = first.type
   *  - snd = second.type
   */
  @Override
  public Type visitNewPair(WACCParser.NewPairContext ctx) {
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
  public Type visitCall(WACCParser.CallContext ctx) {
    Function calledFunction = getCalledFunction(ctx);
    WACCParser.ArgListContext argListContext = ctx.argList();
    int expectedSize = calledFunction.getParams().size();
    if (argListContext != null) {
      int actualSize = argListContext.expr().size();
      if (actualSize == expectedSize) {
        for (int i = 0; i < actualSize; i++) {
          WACCParser.ExprContext exprCtx = argListContext.expr(i);
          Type actualType = visitExpr(exprCtx);
          Type expectedType = calledFunction.getParams().get(i).getType();
          checkTypesEqual(ctx, actualType, expectedType);
        }
      } else {
        inconsistentParamCountError(ctx, expectedSize, actualSize);
      }
    } else if (expectedSize > 0) {
      inconsistentParamCountError(ctx, expectedSize, 0);
    }

    return calledFunction.getType();
  }

  /**
   * if array non empty
   *   - arrayLitr: [(T, T, T, ...)?];
   *   - check all types are the same
   *   - return the type of the first element
   * otherwise
   *   - return generic array type
   */
  @Override
  public Type visitArrayLitr(WACCParser.ArrayLitrContext ctx) {

    if (ctx.expr() != null && ctx.expr().size() != 0) {
      Type type = checkAllTypesEqual(ctx.expr());
      return new ArrayType(type);
    }
    return new ArrayType();
  }

  /************************** Expressions ****************************/

  /**
   * intExpr: (CHR)? (sign)? INTEGER
   * check if it is a char (if CHR is set)
   * otherwise it is an int
   */
  @Override
  public Type visitInteger(WACCParser.IntegerContext ctx) {
    if (ctx.CHR() != null) {
      return getType(Types.CHAR_T);
    }

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
  public Type visitBool(WACCParser.BoolContext ctx) {
    return getType(Types.BOOL_T);
  }

  /**
   * (ORD)? CHARACTER
   * check if it is an int (if ORD is set)
   * otherwise it is a char
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
  public Type visitString(WACCParser.StringContext ctx) {
    if (ctx.LEN() != null) {
      return getType(Types.INT_T);
    }
    return getType(Types.STRING_T);
  }

  /**
   * arrayExpr: (LEN)? arrayElem
   * check the type of the array
   * if preceded by LEN, we are checking the length of the array
   *  - return an int
   * otherwise
   *  - return the type of the array
   */
  @Override
  public Type visitArray(WACCParser.ArrayContext ctx) {

    Type arrayType = visitArrayElem(ctx.arrayElem());

    if (ctx.LEN() != null) {
      return getType(Types.INT_T);
    }

    return arrayType;
  }

  /**
   * pairLitr: NULL;
   * return generic pair type
   */
  @Override
  public Type visitPairLitr(WACCParser.PairLitrContext ctx) {
    return new PairType();
  }

  /************************** Expression Helpers ****************************/

  /**
   * arrayElem: varName[expr];
   *
   *  int[][][] a;
   *  let b = a[]
   *  b :: int[][]
   *
   * check that each expr evaluates to an int
   */
  @Override
	public Type visitArrayElem(WACCParser.ArrayElemContext ctx) {
    checkArrayElemExpressions(ctx.expr());

    Type type = visitIdent(ctx.ident());
    int wantedDim = ctx.OPEN_BRACKET().size();
    if (Type.isString(type)) {
      if (wantedDim != 1) {
        String errorMsg = "String is one dimensional";
        errorHandler.complain(new TypeError(ctx, errorMsg));
      } else {
        return new Type(Types.CHAR_T);
      }
    } else if (ArrayType.isArray(type)) {
      ArrayType arrayType = (ArrayType) type;
      int totalDim = arrayType.getDimensionality();
      if (wantedDim <= totalDim) {
        int returnDim = totalDim - wantedDim;
        return ArrayType.createArray(arrayType.getBase(), returnDim);
      } else {
        errorHandler.complain(new TypeError(ctx));
      }
    }

    incorrectType(ctx, type, "'T[]' or 'string");

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
  public Type visitPairElem(WACCParser.PairElemContext ctx) {
    Type varType = visitIdent(ctx.ident());

    Type returnType = null;

    if (checkTypesEqual(ctx, new PairType(), varType)) {
      PairType pairType = (PairType) varType;

      if (ctx.FST() != null) {
        returnType = pairType.getFst();
      } else if (ctx.SND() != null) {
        returnType = pairType.getSnd();
      }
    }

    return returnType;
  }

  /************************** Operations ****************************/

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
	public Type visitUnaryOper(WACCParser.UnaryOperContext ctx) {
    Type exprType = null;
    if (ctx.ident() != null) {
      exprType = visitIdent(ctx.ident());
    } else if (ctx.expr() != null) {
      exprType = visitExpr(ctx.expr());
    }
    if (ctx.NOT() != null) {
      if (!Type.isBool(exprType)) {
        incorrectType(ctx, exprType, Types.BOOL_T.toString());
      }
      return getType(Types.BOOL_T);
    } else if (ctx.MINUS() != null || ctx.CHR() != null) {
      if (!Type.isInt(exprType)) {
        incorrectType(ctx, exprType, Types.INT_T.toString());
      }
      return ctx.CHR() != null ? getType(Types.CHAR_T) : getType(Types.INT_T);
    } else if (ctx.LEN() != null) {
      if (!ArrayType.isArray(exprType)) {
        incorrectType(ctx, exprType, Types.GENERIC_ARRAY_T.toString());
      }
      return getType(Types.INT_T);
    } else if (ctx.ORD() != null) {
      if (!Type.isChar(exprType)) {
        incorrectType(ctx, exprType, Types.CHAR_T.toString());
      }
      return getType(Types.INT_T);
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
  public Type visitLogicalOper(WACCParser.LogicalOperContext ctx) {
    if (ctx.otherExprs.size() > 0) {
      for (WACCParser.ComparisonOperContext operCtx : ctx.comparisonOper()) {
        Type type = visitComparisonOper(operCtx);
        if (!Type.isBool(type)) {
          incorrectType(operCtx, type, Types.BOOL_T.toString());
        }
      }
      return getType(Types.BOOL_T);
    } else {
      return visitComparisonOper(ctx.first);
    }
  }

  @Override
  public Type visitComparisonOper(WACCParser.ComparisonOperContext ctx) {
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
  public Type visitOrderingOper(WACCParser.OrderingOperContext ctx) {
    if (ctx.second != null) {
      Type fstType = visitAddOper(ctx.first);
      Type sndType = visitAddOper(ctx.second);

      if (!fstType.equals(sndType)) {
        errorHandler.complain(new TypeError(ctx.first));
        errorHandler.complain(new TypeError(ctx.second));
      } else if (!(Type.isInt(fstType) || Type.isChar(fstType))) {
        incorrectType(ctx.first, fstType, "'int' or 'char'");
        incorrectType(ctx.second, sndType, "'int' or 'char'");
      }
      return getType(Types.BOOL_T);
    } else {
      return visitAddOper(ctx.first);
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
  public Type visitEqualityOper(WACCParser.EqualityOperContext ctx) {
    if (ctx.second != null) {
      Type fstType = visitAddOper(ctx.first);
      Type sndType = visitAddOper(ctx.second);

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
      return visitAddOper(ctx.first);
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
  public Type visitMultOper(
      WACCParser.MultOperContext ctx) {
    if (!ctx.otherExprs.isEmpty()) {
      for (WACCParser.AtomContext atomContext : ctx.atom()) {
        Type type = visitAtom(atomContext);
        if (!Type.isInt(type)) {
          incorrectType(atomContext, type, Types.INT_T.toString());
        }
      }
      return getType(Types.INT_T);
    } else {
      return visitChildren(ctx);
    }
  }

  // TODO: Invalid may fail here

  @Override
  public Type visitAddOper(@NotNull WACCParser.AddOperContext ctx) {
    if (!ctx.otherExprs.isEmpty()) {
      for (WACCParser.MultOperContext multOperContext : ctx.multOper()) {
        Type type = visitMultOper(multOperContext);
        if (!Type.isInt(type)) {
          incorrectType(multOperContext, type, Types.INT_T.toString());
        }
      }
      return getType(Types.INT_T);
    } else {
      return visitChildren(ctx);
    }
  }

  @Override
  public Type visitAtom(WACCParser.AtomContext ctx) {

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

  /************************** Other ****************************/

  /**
   * IDENT
   * lookup and return the type
   */
  @Override
  public Type visitIdent(WACCParser.IdentContext ctx) {
    return getMostRecentBindingForVariable(ctx.getText()).getType();
  }

}

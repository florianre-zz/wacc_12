package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import wacc.error.*;
import wacc.error.Error;

public class WACCTypeChecker extends WACCParserBaseVisitor<Type> {

  private final SymbolTable<String, Binding> top;
  private SymbolTable<String, Binding> workingSymbTable;
  private Function currentFunction;
  private ErrorHandler errorHandler;

  public WACCTypeChecker(SymbolTable<String, Binding> top,
                         ErrorHandler errorHandler) {
    this.top = this.workingSymbTable = top;
    this.errorHandler = errorHandler;
  }

  // Helper Methods

  // TODO: this is visitIdent()
  private Type getVariableType(String name) {
    Variable var = (Variable) workingSymbTable.lookupAll(name);

    return var.getType();
  }

  private boolean isReadable(Type lhsType) {
    return Type.isInt(lhsType) || Type.isChar(lhsType);
  }

  private boolean isFreeable(Type exprType) {
    return ArrayType.isArray(exprType) || PairType.isPair(exprType);
  }

  // TODO: check all complain(s) to see if this helper method could be used
  private void IncorrectType(ParserRuleContext ctx,
                             Type exprType,
                             String expectedType) {
    String actual = exprType != null ? exprType.getName() : "'null'";
    errorHandler.complain(
        new TypeAssignmentError(ctx, expectedType, actual)
    );
  }

  private void changeWorkingSymbolTableTo(ParserRuleContext ctx) {
    NewScope b = (NewScope) workingSymbTable.lookupAll(ctx.getText());
    if (b != null) {
      workingSymbTable = (SymbolTable<String, Binding>) b.getSymbolTable();
    }
  }

  // Visit Methods

  /**
  * prog: BEGIN func* main END EOF;
  * change Scope
  * visit children, to type check children */
  @Override
  public Type visitProg(@NotNull WACCParser.ProgContext ctx) {
    changeWorkingSymbolTableTo(ctx);
    visitChildren(ctx);
    return null;
  }

  //  Functions

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
    currentFunction = (Function) workingSymbTable.lookupAll(funcName);
    Type expectedReturnType = currentFunction.getType();
    changeWorkingSymbolTableTo(ctx);

    if (expectedReturnType == null) {
      TypeError error = new TypeError(ctx);
      errorHandler.complain(error);
    }

    visitParamList(ctx.paramList());
    visitStatList(ctx.statList());

    return expectedReturnType;
  }

  /**
  * paramList: param (COMMA param)*;
  * type check each param in the list */
  @Override
  public Type visitParamList(@NotNull WACCParser.ParamListContext ctx) {
    for (WACCParser.ParamContext param : ctx.param()) {
      visitParam(param);
    }
    return null;
  }

  /**
  * param: type name;
  * check type is a valid Type
  * returns null if not valid */
  @Override
  public Type visitParam(@NotNull WACCParser.ParamContext ctx) {
    Type type = getVariableType(ctx.name.getText());
    if (type == null) {
      TypeError error = new TypeError(ctx);
      errorHandler.complain(error);
    }

    return type;
  }

  /**
  * main: statList;
  * change scope to 0main
  * type check children */
  @Override
  public Type visitMain(@NotNull WACCParser.MainContext ctx) {
    //TODO: change scope
    return visitStatList(ctx.statList());
  }

  // Statements

  /**
  * statList: stat (SEMICOLON stat)*;
  * visit each stat in the list */
  @Override
  public Type visitStatList(@NotNull WACCParser.StatListContext ctx) {
    for (WACCParser.StatContext stat : ctx.stat()) {
      visitStat(stat);
    }
    return null;
  }

  /**
  * dummy function that defers to child, may be deleted
  * TODO: delete later */
  @Override
  public Type visitStat(@NotNull WACCParser.StatContext ctx) {
    return visitChildren(ctx);
  }

  /**
  * SKIP
  * return null
  * TODO: delete later*/
  @Override
  public Type visitSkipStat(@NotNull WACCParser.SkipStatContext ctx) {
    return null;
  }

  /**
  * type varName EQUALS assignRHS
  * get lhs & rhs types
  * check that they are equal
  *  - if it is a pair, check the inner types */
  @Override
  public Type visitInitStat(@NotNull WACCParser.InitStatContext ctx) {
    Type lhsType = visitType(ctx.type());
    Type rhsType = visitAssignRHS(ctx.assignRHS());

    if (!lhsType.equals(rhsType)) {
      errorHandler.complain(
          new TypeAssignmentError(ctx, lhsType.getName(), rhsType.getName()));
    } else if (PairType.isPair(lhsType)) {
      Type lhsFstType = ((PairType) lhsType).getFst();
      Type lhsSndType = ((PairType) lhsType).getSnd();

      Type rhsFstType = ((PairType) rhsType).getFst();
      Type rhsSndType = ((PairType) rhsType).getSnd();

      if (!lhsFstType.equals(rhsFstType)) {
        errorHandler.complain(
            new TypeAssignmentError(ctx, lhsType.getName(), rhsType.getName()));
      }

      if (!lhsSndType.equals(rhsSndType)) {
        errorHandler.complain(
            new TypeAssignmentError(ctx, lhsType.getName(), rhsType.getName()));
      }

    }

    return lhsType;
  }

  /**
  * assignLHS EQUALS assignRHS
  * get lhs & rhs types
  * check that they are equal
  *  - if it is a pair, check the inner types
  *  TODO: refactor with Init stat */
  @Override
  public Type visitAssignStat(@NotNull WACCParser.AssignStatContext ctx) {
    Type lhsType = visitAssignLHS(ctx.assignLHS());
    Type rhsType = visitAssignRHS(ctx.assignRHS());

    if (!lhsType.equals(rhsType)) {
      errorHandler.complain(
          new TypeAssignmentError(ctx, lhsType.getName(), rhsType.getName()));
    }

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
          new ReadTypeAssignmentError(ctx, lhsType.getName()));
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
          new ReadTypeAssignmentError(ctx, exprType.getName()));
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
          new ReadTypeAssignmentError(ctx, exprType.getName()));
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

    if (actualReturnType != expectedReturnType) {
      TypeAssignmentError error
          = new TypeAssignmentError(ctx,
          actualReturnType.getName(), expectedReturnType.getName());
      errorHandler.complain(error);
    }

    return expectedReturnType;
  }

  /**
  * (PRINT | PRINTLN) expr
  * no checking to be done
  * return null
  * TODO: delete later */
  @Override
  public Type visitPrintStat(@NotNull WACCParser.PrintStatContext ctx) {
    return null;
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

    //TODO: changes scopes
    visitStatList(ctx.thenStat);
    visitStatList(ctx.elseStat);

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

    // TODO: make 'bool' an enum not string
    if (Type.isBool(predicateType)) {
      errorHandler.complain(
          new TypeAssignmentError(ctx, "'bool'", predicateType.getName()));
    }

    //TODO: changes scopes
    visitStatList(ctx.statList());

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
    //TODO: changes scopes
    return visitStatList(ctx.statList());
  }

  // Statement Helpers

  /**
   * assignLHS: IDENT | arrayElem | pairElem;
   * if IDENT
   *  - lookup binding, check is Variable
   *  - return type
   * if arrayElem
   *  - visit arrayElem to get Type
   * if pairElem
   *  - visit pairElem
   */
  @Override
  public Type visitAssignLHS(@NotNull WACCParser.AssignLHSContext ctx) {

    if (ctx.IDENT() != null) {

      Binding b = workingSymbTable.lookupAll(ctx.getText());
      if (b instanceof Variable) {
        return ((Variable) b).getType();
      }

      errorHandler.complain(
          new Error(ctx)
      );

      return null;
    } else if (ctx.arrayElem() != null) {
      return visitArrayElem(ctx.arrayElem());
    } else {
      return visitPairElem(ctx.pairElem());
    }

  }

  // TODO: Implement AssignRHS children

  /**
   * arrayLitr: [(T, T, T, ...)?];
   * check all types are the same
   * else return null
   */
  @Override
  public Type visitArrayLitr(@NotNull WACCParser.ArrayLitrContext ctx) {
    return null;
  }

  /**
   * argList: expr (COMMA expr)*;
   * TODO: delete? not sure we will use this...
   */
  @Override
	public Type visitArgList(@NotNull WACCParser.ArgListContext ctx) {

		return null;
	}

  // Expressions

  /**
   * expr: binaryOper;
   * */
  @Override
  //TODO: delete later
  public Type visitExpr(@NotNull WACCParser.ExprContext ctx) {
    return visitChildren(ctx);
  }

  /**
   * intExpr: (CHR)? (sign)? INTEGER
   * check if it is a char (if CHR is set)
   * otherwise it should be an int
   */
  @Override
  public Type visitIntExpr(@NotNull WACCParser.IntExprContext ctx) {
    if (ctx.CHR() != null) {
      return (Type) top.lookupAll(Types.CHAR_T.toString());
    }

    return (Type) top.lookupAll(Types.INT_T.toString());
  }

  /**
   * boolExpr: (NOT)? boolLitr
   * check if literal is bool
   */
  @Override
  public Type visitBoolExpr(@NotNull WACCParser.BoolExprContext ctx) {
    return visitBoolLitr(ctx.boolLitr());
  }

  /**
   * (ORD)? CHARACTER
   * check if it is an int (if ORD is set)
   * otherwise it should be a char
   */
  @Override
  public Type visitCharExpr(@NotNull WACCParser.CharExprContext ctx) {
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
  public Type visitStringExpr(@NotNull WACCParser.StringExprContext ctx) {
    return (Type) top.lookupAll(Types.STRING_T.toString());
  }

  /**
   * pairExpr: pairLitr
   * TODO: delete later
   */
  @Override
  public Type visitPairExpr(@NotNull WACCParser.PairExprContext ctx) {
    return visitPairLitr(ctx.pairLitr());
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
  public Type visitArrayExpr(@NotNull WACCParser.ArrayExprContext ctx) {
    if (ctx.LEN() != null) {
      return (Type) top.lookupAll(Types.INT_T.toString());
    }
    return visitArrayElem(ctx.arrayElem());
  }

  /**
   * boolLitr: TRUE | FALSE;
   * return bool type
   */
  @Override
  public Type visitBoolLitr(@NotNull WACCParser.BoolLitrContext ctx) {
    return (Type) top.lookupAll(Types.BOOL_T.toString());
  }

  /**
   * pairLitr: NULL;
   * return null
   */
  @Override
  public Type visitPairLitr(@NotNull WACCParser.PairLitrContext ctx) {
    return null;
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
    // TODO: implement this method
    for (WACCParser.ExprContext expr : ctx.expr()) {
      Type index = visitExpr(expr);
      if (!Type.isInt(index)) {
        errorHandler.complain(new TypeError(ctx));
      }
    }

    // lookup varName, get dimensionality
    // check that brackets <= dimensionality

    // call array type lookup function

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
    // TODO: revisit parser rule
    Type varType = getVariableType(ctx.IDENT().getText());

    if (PairType.isPair(varType)) {
      PairType pairType = (PairType) varType;

      Type innerPairType = null;
      if (ctx.FST() != null) {
        innerPairType = pairType.getFst();
      } else if (ctx.SND() != null) {
        innerPairType = pairType.getSnd();
      }

      return innerPairType;
    } else {
      errorHandler.complain(
          new TypeAssignmentError(ctx, "'pair'", varType.getName()));
    }

    return null;
  }

  /**
   * TODO: delete later
   */
  @Override
  public Type visitUnaryExpr(@NotNull WACCParser.UnaryExprContext ctx) {
    return visitUnaryOper(ctx.unaryOper());
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
  // TODO: when visitIdent is implemented, it should be used to get the type
  // TODO: shorten! (with a sense of urgency)
	public Type visitUnaryOper(@NotNull WACCParser.UnaryOperContext ctx) {
    Type exprType = null;

    if (ctx.IDENT() != null) {
      Binding b = workingSymbTable.lookupAll(ctx.IDENT().getText());
      if (b instanceof Variable) {
        return ((Variable) b).getType();
      }
      errorHandler.complain(
          new Error(ctx)
      );
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
   * TODO: delete later
   */
  @Override
  public Type visitBinaryOper(@NotNull WACCParser.BinaryOperContext ctx) {
    return null;
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
        errorHandler.complain(new TypeError(ctx.first));
        errorHandler.complain(new TypeError(ctx.second));
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
  public Type visitArithmeticOper(@NotNull WACCParser.ArithmeticOperContext ctx) {
    if (ctx.otherExprs.size() > 0) {
      for (WACCParser.AtomContext atomCtx : ctx.atom()) {
        Type type = visitAtom(atomCtx);
        if (!Type.isBool(type)) {
          IncorrectType(atomCtx, type, "'bool'");
        }
      }
      return (Type) top.lookupAll(Types.INT_T.toString());
    } else {
      return visitAtom(ctx.first);
    }
  }

  //Types

  @Override
  public Type visitType(@NotNull WACCParser.TypeContext ctx) {
    //TODO: revisit, should we look at the children?

    return (Type) top.lookupAll(ctx.getText());
  }


}

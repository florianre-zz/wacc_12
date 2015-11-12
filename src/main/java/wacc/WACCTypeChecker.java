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

  private boolean isReadable(Type lhsType) {
    return Type.isInt(lhsType) || Type.isChar(lhsType);
  }

  private boolean isFreeable(Type exprType) {
    return ArrayType.isArray(exprType) || PairType.isPair(exprType);
  }

  private void IncorrectType(WACCParser.UnaryOperContext ctx,
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
    //TODO: revisit how we get the type
    Type type = visitType(ctx.type());
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

  /**
   * visit children
   * TODO: delete later
   */
  @Override
  public Type visitAssignRHS(@NotNull WACCParser.AssignRHSContext ctx) {

    if (ctx.expr() != null) {
      return visitExpr(ctx.expr(0));
    } else if (ctx.arrayLitr() != null) {
      return visitArrayLitr(ctx.arrayLitr());
    }

    return null;
  }

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
   * */
  @Override
  public Type visitExpr(@NotNull WACCParser.ExprContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Type visitIntExpr(@NotNull WACCParser.IntExprContext ctx) {
    if (ctx.CHR() != null) {
      return (Type) top.lookupAll(ctx.CHR().getText());
    }

    return (Type) top.lookupAll(Types.INT_T.toString());
  }

  @Override
  public Type visitBoolExpr(@NotNull WACCParser.BoolExprContext ctx) {
    return visitBoolLitr(ctx.boolLitr());
  }

  @Override
  public Type visitCharExpr(@NotNull WACCParser.CharExprContext ctx) {
    if (ctx.ORD() != null) {
      return (Type) top.lookupAll(Types.INT_T.toString());
    }

    return (Type) top.lookupAll(Types.CHAR_T.toString());
  }

  @Override
  public Type visitStringExpr(@NotNull WACCParser.StringExprContext ctx) {
    return (Type) top.lookupAll(Types.STRING_T.toString());
  }

  @Override
  public Type visitPairExpr(@NotNull WACCParser.PairExprContext ctx) {
    return visitPairLitr(ctx.pairLitr());
  }

  @Override
  public Type visitArrayExpr(@NotNull WACCParser.ArrayExprContext ctx) {
    if (ctx.LEN() != null) {
      return (Type) top.lookupAll(Types.INT_T.toString());
    }
    return visitArrayElem(ctx.arrayElem());
  }

  @Override
  public Type visitBoolLitr(@NotNull WACCParser.BoolLitrContext ctx) {
    return (Type) top.lookupAll(Types.BOOL_T.toString());
  }

  @Override
  public Type visitPairLitr(@NotNull WACCParser.PairLitrContext ctx) {
    return null;
  }

  // Expression Helpers

  @Override
	public Type visitArrayElem(@NotNull WACCParser.ArrayElemContext ctx) {
		return null;
	}

  @Override
  public Type visitPairElem(@NotNull WACCParser.PairElemContext ctx) {
    // TODO: revisit parser rule
    Variable var = (Variable) workingSymbTable.lookupAll(ctx.IDENT().getText());

    Type varType = var.getType();

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

  @Override
  public Type visitUnaryExpr(@NotNull WACCParser.UnaryExprContext ctx) {
    return visitUnaryOper(ctx.unaryOper());
  }

  // Operations

  @Override
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

    if (ctx.NOT() != null && !Type.isBool(exprType)) {
      IncorrectType(ctx, exprType, "'bool'");
    } else if (ctx.MINUS() != null && !Type.isInt(exprType)) {
      IncorrectType(ctx, exprType, "'int'");
    } else if (ctx.LEN() != null && !ArrayType.isArray(exprType)) {
      IncorrectType(ctx, exprType, "'T[]'");
      // TODO: be more explicit about array type
      return (Type) top.lookupAll("INT_T");
    } else if (ctx.ORD() != null && !Type.isChar(exprType)) {
      IncorrectType(ctx, exprType, "'char'");
      return (Type) top.lookupAll(Types.INT_T.toString());
    } else if (ctx.CHR() != null && !Type.isInt(exprType)) {
      IncorrectType(ctx, exprType, "'int'");
      return (Type) top.lookupAll(Types.CHAR_T.toString());
    }

		return exprType;
	}

  @Override
  public Type visitBinaryOper(@NotNull WACCParser.BinaryOperContext ctx) {
    return null;
  }

  @Override
  public Type visitArithmeticOper(@NotNull WACCParser.ArithmeticOperContext ctx) {
    return null;
  }

  @Override
  public Type visitComparisonOper(@NotNull WACCParser.ComparisonOperContext ctx) {
    return null;
  }

  @Override
  public Type visitLogicalOper(@NotNull WACCParser.LogicalOperContext ctx) {
    return null;
  }

  //Types

  @Override
  public Type visitType(@NotNull WACCParser.TypeContext ctx) {
    //TODO: revisit, should we look at the children?

    return (Type) top.lookupAll(ctx.getText());
  }


}

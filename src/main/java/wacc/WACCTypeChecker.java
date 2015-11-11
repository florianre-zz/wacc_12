package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.Binding;
import bindings.Function;
import bindings.Type;
import bindings.Variable;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import wacc.error.*;
import wacc.error.Error;

public class WACCTypeChecker extends WACCParserBaseVisitor<Type> {

  private final SymbolTable<String, Binding> top;
  private SymbolTable<String, Binding> workingSymbTable;
  private Function currentScope;
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
    return Type.isArray(exprType) || Type.isPair(exprType);
  }

  private void IncorrectType(WACCParser.UnaryOperContext ctx,
                             Type exprType,
                             String expectedType) {
    String actual = exprType != null ? exprType.getName() : "'null'";
    errorHandler.complain(
        new TypeAssignmentError(ctx, expectedType, actual)
    );
  }

  // Visit Methods

  @Override
  public Type visitProg(@NotNull WACCParser.ProgContext ctx) {
    TypeError error = new TypeError(ctx);
    errorHandler.complain(error);
    changeWorkingSymbolTableTo(ctx);
    visitChildren(ctx);
    return null;
  }

  private void changeWorkingSymbolTableTo(ParserRuleContext ctx) {
    Function b = (Function) workingSymbTable.lookupAll(ctx.getText());
    if (b != null) {
      workingSymbTable = (SymbolTable<String, Binding>) b.getSymbolTable();
    }
  }

  //  Functions

  @Override
  public Type visitFunc(@NotNull WACCParser.FuncContext ctx) {

    changeWorkingSymbolTableTo(ctx);

    Type expectedReturnType = currentScope.getType();

    if (expectedReturnType == null) {
      TypeError error = new TypeError(ctx);
      errorHandler.complain(error);
    }

    visitParamList(ctx.paramList());

    visitStatList(ctx.statList());

    return expectedReturnType;
  }

  @Override
  public Type visitParamList(@NotNull WACCParser.ParamListContext ctx) {
    for (WACCParser.ParamContext param : ctx.param()) {
      visitParam(param);
    }
    return null;
  }

  @Override
  public Type visitParam(@NotNull WACCParser.ParamContext ctx) {

    Type type = visitType(ctx.type());
    if (type == null) {
      TypeError error = new TypeError(ctx);
      errorHandler.complain(error);
    }

    return type;
  }

  @Override
  public Type visitMain(@NotNull WACCParser.MainContext ctx) {
    return visitStatList(ctx.statList());
  }

  // Statements

  @Override
  public Type visitStatList(@NotNull WACCParser.StatListContext ctx) {
    Type returnType = null;
    for (WACCParser.StatContext stat : ctx.stat()) {
      Type tmp = visitStat(stat);
      if (visitStat(stat) != null) {
        returnType = tmp;
      }
    }
    return returnType;
  }

  @Override
  public Type visitStat(@NotNull WACCParser.StatContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Type visitSkipStat(@NotNull WACCParser.SkipStatContext ctx) {
    return null;
  }

  @Override
  public Type visitInitStat(@NotNull WACCParser.InitStatContext ctx) {
    return visitType(ctx.type());
  }

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

  @Override
  public Type visitReadStat(@NotNull WACCParser.ReadStatContext ctx) {
    Type lhsType = visitAssignLHS(ctx.assignLHS());

    if (!isReadable(lhsType)) {
      errorHandler.complain(
          new ReadTypeAssignmentError(ctx, lhsType.getName()));
    }

    return lhsType;
  }

  @Override
  public Type visitFreeStat(@NotNull WACCParser.FreeStatContext ctx) {
    Type exprType = visitExpr(ctx.expr());

    if (!isFreeable(exprType)) {
      errorHandler.complain(
          new ReadTypeAssignmentError(ctx, exprType.getName()));
    }

    return exprType;
  }

  @Override
  public Type visitExitStat(@NotNull WACCParser.ExitStatContext ctx) {
    Type exprType = visitExpr(ctx.expr());

    if (!Type.isInt(exprType)) { // exit codes are Integers
      errorHandler.complain(
          new ReadTypeAssignmentError(ctx, exprType.getName()));
    }

    return exprType;
  }

  @Override
  public Type visitReturnStat(@NotNull WACCParser.ReturnStatContext ctx) {

    Type actualReturnType = visitExpr(ctx.expr());

    Type expectedReturnType = currentScope.getType();

    if (actualReturnType != expectedReturnType) {
      TypeAssignmentError error
          = new TypeAssignmentError(ctx,
          actualReturnType.getName(), expectedReturnType.getName());
      errorHandler.complain(error);
    }

    return expectedReturnType;
  }

  @Override
  public Type visitPrintStat(@NotNull WACCParser.PrintStatContext ctx) {
    return null;
  }

  @Override
  public Type visitIfStat(@NotNull WACCParser.IfStatContext ctx) {

    Type predicateType = visitExpr(ctx.expr());

    if (Type.isBool(predicateType)) {
      errorHandler.complain(
          new TypeAssignmentError(ctx, "'bool'", predicateType.getName()));
    }

    visitStatList(ctx.thenStat);
    visitStatList(ctx.elseStat);

    return null;
  }

  @Override
  public Type visitWhileStat(@NotNull WACCParser.WhileStatContext ctx) {
    Type predicateType = visitExpr(ctx.expr());

    if (Type.isBool(predicateType)) {
      errorHandler.complain(
          new TypeAssignmentError(ctx, "'bool'", predicateType.getName()));
    }

    visitStatList(ctx.statList());

    return null;
  }

  @Override
  public Type visitBeginStat(@NotNull WACCParser.BeginStatContext ctx) {
    return visitStatList(ctx.statList());
  }

  // Statement Helpers

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

  @Override
  public Type visitAssignRHS(@NotNull WACCParser.AssignRHSContext ctx) {

    if (ctx.expr() != null) {
      return visitExpr(ctx.expr(0));
    } else if (ctx.arrayLitr() != null) {
      return visitArrayLitr(ctx.arrayLitr());
    }

    return null;
  }

  @Override
  public Type visitArrayLitr(@NotNull WACCParser.ArrayLitrContext ctx) {

  /* TODO:| Come back to this when Florian has worked out how to deal with
     TODO:| array types */

    return null;
  }

  @Override
	public Type visitArgList(@NotNull WACCParser.ArgListContext ctx) {

		return null;
	}

  // Expressions

  @Override
  public Type visitExpr(@NotNull WACCParser.ExprContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Type visitIntExpr(@NotNull WACCParser.IntExprContext ctx) {
    if (ctx.CHR() != null) {
      return (Type) top.lookupAll(ctx.CHR().getText());
    }

    return (Type) top.lookupAll("INT_T");
  }

  @Override
  public Type visitBoolExpr(@NotNull WACCParser.BoolExprContext ctx) {
    return visitBoolLitr(ctx.boolLitr());
  }

  @Override
  public Type visitCharExpr(@NotNull WACCParser.CharExprContext ctx) {
    if (ctx.ORD() != null) {
      return (Type) top.lookupAll(ctx.ORD().getText());
    }

    return (Type) top.lookupAll("CHAR_T");
  }

  @Override
  public Type visitStringExpr(@NotNull WACCParser.StringExprContext ctx) {
    return (Type) top.lookupAll(ctx.STRING().getText());
  }

  @Override
  public Type visitPairExpr(@NotNull WACCParser.PairExprContext ctx) {
    return visitPairLitr(ctx.pairLitr());
  }

  @Override
  public Type visitArrayExpr(@NotNull WACCParser.ArrayExprContext ctx) {
    if (ctx.LEN() != null) {
      return (Type) top.lookupAll("INT_T");
    }
    return visitArrayElem(ctx.arrayElem());
  }

  @Override
  public Type visitBoolLitr(@NotNull WACCParser.BoolLitrContext ctx) {
    return (Type) top.lookupAll("BOOL_T");
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
    } else if (ctx.LEN() != null && !Type.isArray(exprType)) {
      IncorrectType(ctx, exprType, "'T[]'");
      // TODO: be more explicit about array type
      return (Type) top.lookupAll("INT_T");
    } else if (ctx.ORD() != null && !Type.isChar(exprType)) {
      IncorrectType(ctx, exprType, "'char'");
      return (Type) top.lookupAll("INT_T");
    } else if (ctx.CHR() != null && !Type.isInt(exprType)) {
      IncorrectType(ctx, exprType, "'int'");
      return (Type) top.lookupAll("CHAR_T");
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
    return (Type) top.lookupAll(ctx.getText());
  }


}

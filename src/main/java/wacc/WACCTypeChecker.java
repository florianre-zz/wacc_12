package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.Binding;
import bindings.Function;
import bindings.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import wacc.error.ErrorHandler;
import wacc.error.TypeAssignmentError;
import wacc.error.TypeError;

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

  private Type getType(WACCParser.TypeContext ctx) {
    return (Type) top.lookupAll(ctx.getText());
  }

  // Visit Methods

  @Override
  public Type visitProg(@NotNull WACCParser.ProgContext ctx) {
    TypeError error = new TypeError(ctx);
    errorHandler.encounteredError(error);
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
      errorHandler.encounteredError(error);
    }

    visitParamList(ctx.paramList());

    Type actualReturnType = visitStatList(ctx.statList());

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
      errorHandler.encounteredError(error);
    }

    return type;
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
    return null;
  }

  @Override
  public Type visitAssignStat(@NotNull WACCParser.AssignStatContext ctx) {
    return null;
  }

  @Override
  public Type visitReadStat(@NotNull WACCParser.ReadStatContext ctx) {
    return null;
  }

  @Override
  public Type visitFreeStat(@NotNull WACCParser.FreeStatContext ctx) {
    return null;
  }

  @Override
  public Type visitExitStat(@NotNull WACCParser.ExitStatContext ctx) {
    return null;
  }

  @Override
  public Type visitReturnStat(@NotNull WACCParser.ReturnStatContext ctx) {

    Type actualReturnType = visitExpr(ctx.expr());

    Type expectedReturnType = currentScope.getType();

    if (actualReturnType != expectedReturnType) {
      TypeAssignmentError error
          = new TypeAssignmentError(ctx,
          actualReturnType.getName(), expectedReturnType.getName());
      errorHandler.encounteredError(error);
    }

    return expectedReturnType;
  }

  @Override
  public Type visitPrintStat(@NotNull WACCParser.PrintStatContext ctx) {
    return null;
  }

  @Override
  public Type visitIfStat(@NotNull WACCParser.IfStatContext ctx) {
    return null;
  }

  @Override
  public Type visitWhileStat(@NotNull WACCParser.WhileStatContext ctx) {
    return null;
  }

  @Override
  public Type visitBeginStat(@NotNull WACCParser.BeginStatContext ctx) {
    return null;
  }

  // Statement Helpers

  @Override
  public Type visitAssignLHS(@NotNull WACCParser.AssignLHSContext ctx) {
    return null;
  }

  @Override
  public Type visitAssignRHS(@NotNull WACCParser.AssignRHSContext ctx) {
    return null;
  }

  @Override
  public Type visitArrayLitr(@NotNull WACCParser.ArrayLitrContext ctx) {
    return null;
  }

  @Override
	public Type visitArgList(@NotNull WACCParser.ArgListContext ctx) {
		return null;
	}

  // Expressions

  @Override
  public Type visitExpr(@NotNull WACCParser.ExprContext ctx) {
    return null;
  }

  @Override
  public Type visitBoolLitr(@NotNull WACCParser.BoolLitrContext ctx) {
    return null;
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

  // Operations

  @Override
	public Type visitUnaryOper(@NotNull WACCParser.UnaryOperContext ctx) {
		return null;
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

  @Override
  public Type visitArrayType(@NotNull WACCParser.ArrayTypeContext ctx) {
    return null;
  }

  @Override
  public Type visitNonArrayType(@NotNull WACCParser.NonArrayTypeContext ctx) {
    return null;
  }

  @Override
  public Type visitBaseType(@NotNull WACCParser.BaseTypeContext ctx) {
    return null;
  }

  @Override
  public Type visitPairType(@NotNull WACCParser.PairTypeContext ctx) {
    return null;
  }

  @Override
  public Type visitPairElemType(@NotNull WACCParser.PairElemTypeContext ctx) {
    return null;
  }

  // Other

  @Override
	public Type visitSign(@NotNull WACCParser.SignContext ctx) {
		return null;
	}

}

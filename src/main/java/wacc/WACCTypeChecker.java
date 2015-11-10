package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.Binding;
import bindings.Type;
import org.antlr.v4.runtime.misc.NotNull;
import wacc.error.ErrorHandler;
import wacc.error.TypeError;

public class WACCTypeChecker extends WACCParserBaseVisitor<Type> {

  private final SymbolTable<String, Binding> top;
  private SymbolTable<String, Binding> workingSymbTable;
  private ErrorHandler errorHandler;

  public WACCTypeChecker(SymbolTable<String, Binding> top, ErrorHandler errorHandler) {
    this.top = top;
    this.workingSymbTable = top;
    this.errorHandler = errorHandler;
  }

  // Helper Methods

  private Type getType(WACCParser.TypeContext ctx) {
    return (Type) top.lookupAll(ctx.getText());
  }
  
  @Override
	public Type visitArgList(@NotNull WACCParser.ArgListContext ctx) {
		return null;
	}

  @Override
	public Type visitReturnStat(@NotNull WACCParser.ReturnStatContext ctx) {
		return null;
	}

  @Override
	public Type visitBeginStat(@NotNull WACCParser.BeginStatContext ctx) {
		return null;
	}
  
  @Override
	public Type visitArrayElem(@NotNull WACCParser.ArrayElemContext ctx) {
		return null;
	}
  
  @Override
	public Type visitAssignRHS(@NotNull WACCParser.AssignRHSContext ctx) {
		return null;
	}
  
  @Override
	public Type visitBoolLitr(@NotNull WACCParser.BoolLitrContext ctx) {
		return null;
	}
  
  @Override
	public Type visitAssignLHS(@NotNull WACCParser.AssignLHSContext ctx) {
		return null;
	}
  
  @Override
	public Type visitUnaryOper(@NotNull WACCParser.UnaryOperContext ctx) {
		return null;
	}
  
  @Override
	public Type visitSign(@NotNull WACCParser.SignContext ctx) {
		return null;
	}
  
  @Override
	public Type visitType(@NotNull WACCParser.TypeContext ctx) {
    return (Type) workingSymbTable.lookupAll(ctx.getText());
	}
  
  @Override
	public Type visitReadStat(@NotNull WACCParser.ReadStatContext ctx) {
		return null;
	}
  
  @Override
	public Type visitComparisonOper(@NotNull WACCParser.ComparisonOperContext ctx) {
		return null;
	}

  @Override
	public Type visitBaseType(@NotNull WACCParser.BaseTypeContext ctx) {
		return null;
	}
  
  @Override
	public Type visitArithmeticOper(@NotNull WACCParser.ArithmeticOperContext ctx) {
		return null;
	}

  @Override
	public Type visitStatList(@NotNull WACCParser.StatListContext ctx) {
    Type returnType = null;
    for (WACCParser.StatContext stat : ctx.stat()) {
      returnType = visitStat(stat);
    }
    return returnType;
	}

  @Override
	public Type visitParam(@NotNull WACCParser.ParamContext ctx) {

    Type type = visitType(ctx.type());
    if (type == null) {
      TypeError error = new TypeError();
      errorHandler.encounteredError(error);
    }

    return type;
	}
  
  @Override
	public Type visitPrintStat(@NotNull WACCParser.PrintStatContext ctx) {
		return null;
	}
  
  @Override
	public Type visitExpr(@NotNull WACCParser.ExprContext ctx) {
		return null;
	}

  @Override
	public Type visitPairElem(@NotNull WACCParser.PairElemContext ctx) {
		return null;
	}
  
  @Override
	public Type visitExitStat(@NotNull WACCParser.ExitStatContext ctx) {
		return null;
	}

  @Override
	public Type visitStat(@NotNull WACCParser.StatContext ctx) {
    return visitChildren(ctx);
	}

  @Override
	public Type visitArrayType(@NotNull WACCParser.ArrayTypeContext ctx) {
		return null;
	}
  
  @Override
	public Type visitPairLitr(@NotNull WACCParser.PairLitrContext ctx) {
		return null;
	}

  @Override
	public Type visitIfStat(@NotNull WACCParser.IfStatContext ctx) {
		return null;
	}

  @Override
	public Type visitFreeStat(@NotNull WACCParser.FreeStatContext ctx) {
		return null;
	}
  
  @Override
	public Type visitSkipStat(@NotNull WACCParser.SkipStatContext ctx) {
		return null;
	}
  
  @Override
	public Type visitWhileStat(@NotNull WACCParser.WhileStatContext ctx) {
		return null;
	}
  
  @Override
	public Type visitLogicalOper(@NotNull WACCParser.LogicalOperContext ctx) {
		return null;
	}
  
  @Override
	public Type visitBinaryOper(@NotNull WACCParser.BinaryOperContext ctx) {
		return null;
	}
  
  @Override
	public Type visitProg(@NotNull WACCParser.ProgContext ctx) {
    visitChildren(ctx);
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

  @Override
	public Type visitAssignStat(@NotNull WACCParser.AssignStatContext ctx) {
		return null;
	}

  @Override
	public Type visitFunc(@NotNull WACCParser.FuncContext ctx) {

    Type expectedReturnType = getType(ctx.type());

    if (expectedReturnType == null) {
      TypeError error = new TypeError();
      errorHandler.encounteredError(error);
    }

    visitParamList(ctx.paramList());

    Type actualReturnType = visitStatList(ctx.statList());

    if (actualReturnType != expectedReturnType) {
      TypeError error = new TypeError();
      errorHandler.encounteredError(error);
    }

    return expectedReturnType;
	}

  @Override
	public Type visitNonArrayType(@NotNull WACCParser.NonArrayTypeContext ctx) {
		return null;
	}
  
  @Override
	public Type visitParamList(@NotNull WACCParser.ParamListContext ctx) {
    for (WACCParser.ParamContext param : ctx.param()) {
      visitParam(param);
    }
    return null;
	}
  
  @Override
	public Type visitArrayLitr(@NotNull WACCParser.ArrayLitrContext ctx) {
		return null;
	}
  
  @Override
	public Type visitInitStat(@NotNull WACCParser.InitStatContext ctx) {
		return null;
	}
}

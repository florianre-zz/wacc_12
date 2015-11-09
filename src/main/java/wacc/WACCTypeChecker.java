package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.Type;
import org.antlr.v4.runtime.misc.NotNull;

public class WACCTypeChecker extends WACCParserBaseVisitor<Type> {
  
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
		return null;
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
		return null;
	}

  @Override
	public Type visitParam(@NotNull WACCParser.ParamContext ctx) {
		return null;
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
		return null;
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
		return null;
	}

  @Override
	public Type visitNonArrayType(@NotNull WACCParser.NonArrayTypeContext ctx) {
		return null;
	}
  
  @Override
	public Type visitParamList(@NotNull WACCParser.ParamListContext ctx) {
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
package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.*;
import org.antlr.v4.runtime.misc.NotNull;

public class WACCTypeCreator extends WACCParserBaseVisitor<Type> {

  private SymbolTable<String, Binding> top;

  public WACCTypeCreator(SymbolTable<String, Binding> top) {
    this.top = top;
  }

  @Override
  public Type visitInitStat(@NotNull WACCParser.InitStatContext ctx) {
    return visitType(ctx.type());
  }

  @Override
  public Type visitParam(@NotNull WACCParser.ParamContext ctx) {
    return visitType(ctx.type());
  }

  @Override
  public Type visitType(@NotNull WACCParser.TypeContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Type visitArrayType(@NotNull WACCParser.ArrayTypeContext ctx) {
    Type base = visitNonArrayType(ctx.nonArrayType());
    int dimensionality = ctx.OPEN_BRACKET().size();
    return new ArrayType(base, dimensionality);
  }

  @Override
  public Type visitNonArrayType(@NotNull WACCParser.NonArrayTypeContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Type visitPairType(@NotNull WACCParser.PairTypeContext ctx) {
    Type fstType = visitPairElemType(ctx.firstType);
    Type sndType = visitPairElemType(ctx.secondType);
    return new PairType(fstType, sndType);
  }

  @Override
  public Type visitPairElemType(@NotNull WACCParser.PairElemTypeContext ctx) {
    if (ctx.baseType() != null) {
      return visitBaseType(ctx.baseType());
    } else if (ctx.arrayType() != null) {
      return visitArrayType(ctx.arrayType());
    } else {
      return new Type(Types.PAIR_T);
    }
  }

  @Override
  public Type visitBaseType(@NotNull WACCParser.BaseTypeContext ctx) {
    if (ctx.INT_T() != null) {
      return (Type) top.get(Types.INT_T.toString());
    } else if (ctx.BOOL_T() != null) {
      return (Type) top.get(Types.BOOL_T.toString());
    } else if(ctx.CHAR_T() != null) {
      return (Type) top.get(Types.CHAR_T.toString());
    } else {
      return (Type) top.get(Types.STRING_T.toString());
    }
  }
}

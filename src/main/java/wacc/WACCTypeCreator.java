package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.ArrayType;
import bindings.Binding;
import bindings.Type;
import bindings.Types;
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

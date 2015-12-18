package wacc;

import antlr.WACCParser;
import bindings.*;

public class WACCTypeCreator extends WACCVisitor<Type> {

  public WACCTypeCreator(SymbolTable<String, Binding> top) {
    super(top);
  }

  /**
   * Get type of parameter by visiting its type context
   */
  @Override
  public Type visitParam(WACCParser.ParamContext ctx) {
    return visitType(ctx.type());
  }

  /**
   * Get base type and dimensionality of an ArrayTypeContext using an
   * arrayType
   */
  @Override
  public Type visitArrayType(WACCParser.ArrayTypeContext ctx) {
    Type base = visitNonArrayType(ctx.nonArrayType());
    int dimensionality = ctx.OPEN_BRACKET().size();
    return new ArrayType(base, dimensionality);
  }

  /**
   * Get first element type and second element type of an PairTypeContext using
   * a pairType
   */
  @Override
  public Type visitPairType(WACCParser.PairTypeContext ctx) {
    Type fstType = visitPairElemType(ctx.firstType);
    Type sndType = visitPairElemType(ctx.secondType);

    Type base = new PairType(fstType, sndType);

    if (!ctx.MUL().isEmpty()) {
      base = new PointerType(base, ctx.MUL().size());
    }

    return base;
  }

  /**
   * Get type of an element of a pair
   */
  @Override
  public Type visitPairElemType(WACCParser.PairElemTypeContext ctx) {
    if (ctx.baseType() != null) {
      return visitBaseType(ctx.baseType());
    } else if (ctx.arrayType() != null) {
      return visitArrayType(ctx.arrayType());
    } else if (ctx.pairType() != null) {
      return visitPairType(ctx.pairType());
    } else {
      return new PairType();
    }
  }

  /**
   * Get the type of the BaseTypeContext
   */
  @Override
  public Type visitBaseType(WACCParser.BaseTypeContext ctx) {
    Type base;
    if (ctx.INT_T() != null) {
      base = getType(Types.INT_T);
    } else if (ctx.BOOL_T() != null) {
      base =  getType(Types.BOOL_T);
    } else if(ctx.CHAR_T() != null) {
      base =  getType(Types.CHAR_T);
    } else {
      base =  getType(Types.STRING_T);
    }

    if (!ctx.MUL().isEmpty()) {
      base = new PointerType(base, ctx.MUL().size());
    }

    return base;
  }
}

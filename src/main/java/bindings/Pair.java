package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

public class Pair extends Variable {

  private Type fstType;
  private Type sndType;

  public Pair(String name, ParserRuleContext ctx, Type type, Type fstType,
              Type sndType) {
    super(name, ctx, type);
    this.fstType = fstType;
    this.sndType = sndType;
  }

  public Type getFstType() {
    return fstType;
  }

  public Type getSndType() {
    return sndType;
  }
}

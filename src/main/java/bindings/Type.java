package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

public class Type extends Bindings{

  private int min, max;

  public Type(String name, ParserRuleContext ctx) {
    super(name, ctx);
  }

  public Type(String name, ParserRuleContext ctx, int min, int max) {
    super(name, ctx);
    this.min = min;
    this.max = max;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }
}

package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

public class Binding {

  private String name;
  private ParserRuleContext ctx;

  public Binding(String name, ParserRuleContext ctx) {
    this.name = name;
    this.ctx = ctx;
  }

  public ParserRuleContext getContext() {
    return ctx;
  }

  public String getName() {
    return name;
  }
}

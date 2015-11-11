package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

public class Error {

  private final ParserRuleContext ctx;

  public Error(ParserRuleContext ctx) {
    this.ctx = ctx;
  }

  public ParserRuleContext getCtx() {
    return ctx;
  }

  @Override
  public String toString() {
    return "Error: ";
  }

}

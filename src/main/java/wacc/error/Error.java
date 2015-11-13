package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

public class Error {

  // TODO: maybe extend this class where necessary, messages perhaps

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

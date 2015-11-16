package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class Error implements IError<ParserRuleContext> {

  // TODO: maybe extend this class where necessary, messages perhaps

  private final ParserRuleContext ctx;
  protected String message;

  public Error(ParserRuleContext ctx) {
    this.ctx = ctx;
  }

  public Error(ParserRuleContext ctx, String message) {
    this.ctx = ctx;
    this.message = message;
  }

  public ParserRuleContext getCtx() {
    return ctx;
  }

  @Override
  public String toString() {
    return "Error: ";
  }

}

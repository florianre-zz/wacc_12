package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class AbstractError implements Error<ParserRuleContext> {

  // TODO: maybe extend this class where necessary, messages perhaps

  private final ParserRuleContext ctx;

  public AbstractError(ParserRuleContext ctx) {
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

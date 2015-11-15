package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;


public class TypeError extends Error {

  public TypeError(ParserRuleContext ctx) {
    super(ctx);
  }

  @Override
  public String toString() {
    return "Type " + super.toString();
  }

}

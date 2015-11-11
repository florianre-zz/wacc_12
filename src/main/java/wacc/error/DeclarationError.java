package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

public class DeclarationError extends Error {

  public DeclarationError(ParserRuleContext ctx) {
    super(ctx);
  }

  @Override
  public  String toString() {
    return "Declaration " + super.toString();
  }

}

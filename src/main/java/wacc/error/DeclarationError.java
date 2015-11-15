package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

public class DeclarationError extends AbstractError {

  private String message;

  public DeclarationError(ParserRuleContext ctx, String message) {
    super(ctx);
    this.message = message;
  }

  @Override
  public  String toString() {
    return "Declaration " + super.toString() + message;
  }

}

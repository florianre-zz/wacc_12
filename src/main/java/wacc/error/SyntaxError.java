package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

public class SyntaxError extends Error {

  public SyntaxError(ParserRuleContext ctx,
                     String message) {
    super(ctx, message);
  }

  @Override
  public String toString() {
    String message = "";
    if (this.message != null) {
      message = this.message + ". ";
    }
    return "Syntax " + super.toString() + message;
  }
}

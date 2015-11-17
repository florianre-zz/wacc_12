package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;


public class TypeError extends SemanticError {

  public TypeError(ParserRuleContext ctx) {
    super(ctx);
  }

  public TypeError(ParserRuleContext ctx, String message) {
    super(ctx, message);
  }

  @Override
  public String toString() {
    String message = "";
    if (this.message != null) {
      message = this.message + ". ";
    }
    return "Type " + super.toString() + message;
  }

}

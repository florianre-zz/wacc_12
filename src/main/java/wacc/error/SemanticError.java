package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

public class SemanticError extends Error {

  public SemanticError(ParserRuleContext ctx) {
    super(ctx);
  }
  public SemanticError(ParserRuleContext ctx, String errorMsg) {
   super(ctx, errorMsg);
  }

}

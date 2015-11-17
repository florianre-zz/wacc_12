package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public class FreeTypeAssignmentError extends TypeAssignmentError {

  private static final String EXPECTED = "'array' or 'pair'";

  public FreeTypeAssignmentError(ParserRuleContext ctx, String actual) {
    super(ctx, EXPECTED, actual);
  }

}

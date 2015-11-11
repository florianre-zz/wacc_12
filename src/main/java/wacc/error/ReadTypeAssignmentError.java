package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public class ReadTypeAssignmentError extends TypeAssignmentError {

  private static final String EXPECTED = "'int' or 'char'";

  public ReadTypeAssignmentError(ParserRuleContext ctx, String actual) {
    super(ctx, EXPECTED, actual);
  }

}

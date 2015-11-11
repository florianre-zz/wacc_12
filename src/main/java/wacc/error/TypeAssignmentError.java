package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;

public class TypeAssignmentError extends TypeError {

  private String expected;
  private String actual;

  public TypeAssignmentError(ParserRuleContext ctx,
                             String expected, String actual) {
    super(ctx);
    this.expected = expected;
    this.actual = actual;
  }

  @Override
  public String toString() {
    return super.toString() + "expected: " + expected
        + ", but actual was: " + actual;
  }
}

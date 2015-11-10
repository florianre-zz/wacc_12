package wacc.error;

public class TypeAssignmentError extends TypeError {

  private String expected;
  private String actual;

  public TypeAssignmentError(String expected, String actual) {
    this.expected = expected;
    this.actual = actual;
  }

  @Override
  public String toString() {
    return super.toString() + "expected: " + expected
        + ", but actual was: " + actual;
  }
}

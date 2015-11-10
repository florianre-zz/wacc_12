package wacc.error;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public class ExitTypeAssignmentError extends TypeAssignmentError {
  private static final String EXPECTED = "'int'";

  public ExitTypeAssignmentError(String expected, String actual) {
    super(EXPECTED, actual);
  }

}

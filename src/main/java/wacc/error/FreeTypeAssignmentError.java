package wacc.error;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public class FreeTypeAssignmentError extends TypeAssignmentError {

  private static final String EXPECTED = "'array' or 'pair'";

  public FreeTypeAssignmentError(String actual) {
    super(EXPECTED, actual);
  }

}

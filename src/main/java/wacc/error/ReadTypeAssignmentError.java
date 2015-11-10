package wacc.error;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public class ReadTypeAssignmentError extends TypeAssignmentError {

  private static final String EXPECTED = "'int' or 'char'";

  public ReadTypeAssignmentError(String actual) {
    super(EXPECTED, actual);
  }

}

package wacc.error;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public class TypeError extends Error {

  protected String message() {
    return super.message() + "Type Error: ";
  }

}

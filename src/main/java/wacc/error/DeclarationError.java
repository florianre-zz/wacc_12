package wacc.error;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public class DeclarationError extends Error {

  protected  String message() {
    return super.message() + "Declaration Error: ";
  }

}

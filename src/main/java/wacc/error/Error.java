package wacc.error;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public abstract class Error {

  protected String message() {
    return "Error: ";
  }

  public void print() {
    System.out.println(message());
  }

}

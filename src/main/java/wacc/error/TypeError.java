package wacc.error;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public class TypeError extends Error {

  @Override
  public String toString() {
    return "Type " + super.toString();
  }

}

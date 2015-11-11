package wacc.error;

import java.util.ArrayList;

/**
 * Created by elliotgreenwood on 11.10.15.
 */
public class ErrorHandler {
  ArrayList<Error> errors;

  public ErrorHandler() {
    this.errors = new ArrayList<>();
  }

  public void encounteredError(Error e) {
    errors.add(e);
  }

  @Override
  public String toString() {

    final StringBuilder sb = new StringBuilder(errors.size() + " Errors:\n");

    for (Error e : errors) {
      sb.append("\t").append(e).append("\n");
    }

    return sb.toString();
  }
}

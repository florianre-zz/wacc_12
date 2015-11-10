package wacc.error;

public abstract class Error {

  protected String message() {
    return "Error: ";
  }

  public void print() {
    System.out.println(message());
  }

}

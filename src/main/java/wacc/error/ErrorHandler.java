package wacc.error;

public interface ErrorHandler<T> {

  public void complain(IError<T> e);
  public String toString();
  public int getErrorCount();

}

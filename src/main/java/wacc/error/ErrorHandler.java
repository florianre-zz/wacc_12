package wacc.error;

public interface ErrorHandler<T> {

  void complain(IError<T> e);
  String toString();
  int getSemanticErrorCount();
  int getSyntacticErrorCount();

}

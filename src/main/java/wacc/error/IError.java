package wacc.error;


public interface IError<T> {

    T getCtx();
    String toString();

}

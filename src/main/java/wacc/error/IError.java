package wacc.error;


public interface IError<T> {

    public T getCtx();
    String toString();

}

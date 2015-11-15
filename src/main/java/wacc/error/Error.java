package wacc.error;

/**
 * Created by Elyas on 15/11/2015.
 */
public interface Error<T> {

    public T getCtx();
    String toString();

}

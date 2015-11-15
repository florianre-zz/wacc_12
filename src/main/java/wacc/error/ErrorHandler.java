package wacc.error;

/**
 * Created by Elyas on 15/11/2015.
 */
public interface ErrorHandler<T> {

    public void complain(Error<T> e);
    public String toString();

}

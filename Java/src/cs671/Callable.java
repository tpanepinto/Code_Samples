package cs671;

/** Callables.  These are similar to runnables, except that the {@code
 * call} method returns a value and can throw checked exceptions.
 * Callables are a convenient way to represent result-bearing tasks.
 *
 * @author  Michel Charpentier
 * @version 1.0, 2/10/14
 */
public interface Callable<T> {

  /** Runs the task. */
  public T call () throws Exception;
}

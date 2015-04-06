package cs671;

/** Callbacks.  Instances of this type can be called back after a
 * result-bearing task completes, normally or abruptly.
 *
 * @author  Michel Charpentier
 * @version 1.0, 2/10/14
 */
public interface Callback<T> {

  /** Called after a task completes normally.
   * @param value the result produced from running the task
   */
  public void call (T value);

  /** Called after a task completes abruptly.
   * @param t the error or exception thrown by the task
   */
  public void failure (Throwable t);

}

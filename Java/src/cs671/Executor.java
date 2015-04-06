package cs671;

/** Task executor.  Executors are used to start tasks asynchronously
 * (that is, the tasks run in parallel with the thread that schedules
 * them).  Executors accept tasks as {@code Runnable} or as {@code
 * Callable}.  A task can be submitted for execution alongside a
 * {@code Callback} object that with be called with the task's result
 * (if any) after the task completes (or with the cause of failure if
 * a task terminates abruptly).  Alternatively, a task can be
 * submitted without a callback, in which case a {@code Future} is
 * returned by the executor.  This future can be used to obtain the
 * result of the task after it completes, to attach one or more
 * callbacks to the task or to specify continuations of the task with
 * further computation.
 *
 * @author  Michel Charpentier
 * @version 1.0, 2/10/14
 * @see Runnable
 * @see Callable
 * @see Callback
 * @see Future
 */
public interface Executor {

  /** Submits a callable task for execution.
   * @return a future with the given task as its underlying computation
   */
  public <T> Future<T> execute (Callable<T> task);

  /** Submits a runnable task for execution.  Note that the result
   * returned by {@code Future.get} will be {@code null}, whether the
   * task terminates normally or abruptly.
   * @return a future with the given task as its underlying computation
   * @see Future#get
   */
  public <T> Future<T> execute (Runnable task);

  /** Submits a callable task for execution, alongside a callback.
   * After the task terminates, the callback's {@code call} method
   * will be invoked with the value produced by the task, if the task
   * was successful.  If the task fails abruptly, the callback's
   * {@code failure} method will be called instead, with the cause of
   * the failure.
   */
  public <T> void execute (Callable<T> task, Callback<? super T> callback);

  /** Submits a runnable task for execution, alongside a callback.
   * After the task terminates, the callback's {@code call} method
   * will be invoked with {@code null}, if the task
   * was successful.  If the task fails abruptly, the callback's
   * {@code failure} method will be called instead, with the cause of
   * the failure.
   */
  public <T> void execute (Runnable task, Callback<T> callback);
}

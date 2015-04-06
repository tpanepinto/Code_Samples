package cs671;

/** Futures.  A future represents a computation that may or may not
 * have terminated.  Futures can be created by starting asynchronous
 * tasks or by continuing other futures.
 *
 * @author  Michel Charpentier
 * @version 1.0, 2/10/14
 * @see Executor
 * @see CompletedFuture
 * @see FailedFuture
 */
public interface Future<T> {

  /** Checks the future for completion.
   * @return true iff the underlying computation has terminated,
   * normaly or abruptly.
   */
  public boolean isDone ();

  /** Computation result.  This method blocks the calling thread if
   * the underlying computation has not terminated yet.
   * @return the value produced by the computation under normal
   * termination, or {@code null} if the computation terminated
   * abruptly.
   */
  public T get () throws InterruptedException ;

  /** Computation failure.  This method blocks the calling thread if
   * the underlying computation has not terminated yet.
   * @return the error or exception that caused the computation to
   * terminate abrupty, or {@code null} if the computation terminated
   * normally.  Note that {@code getFailure() == null} is true exactly
   * when a computation terminates normally, while {@code get() ==
   * null} can be true of a successful null-producing computation.
   */
  public Throwable getFailure () throws InterruptedException;

  /** Attaches a callback.  After the underlying computation
   * terminates, the callback object's {@code call} will be invoked
   * with the result of the computation, if it terminated
   * successfully.  Upon failure of the underlying computation, the
   * callback's {@code failure} method will be invoked instead, with
   * the error or exception that caused the failure.  Only one of the
   * callback's method will be invoked.  The invocation will take
   * place <em>after</em> the computation finishes and <em>before</em>
   * {@code isDone} returns {@code true} and blocked threads are
   * released from {@code get} or {@code getFailure}, if any.
   */
  public void whenComplete (Callback<? super T> callback);

  /** Attaches a callback.  After the underlying computation
   * terminates successfully, the callback object's {@code run} will
   * be invoked.  No call is made if the underlying computation fails.
   * The invocation will take place <em>after</em> the computation
   * finishes and <em>before</em> {@code isDone} returns {@code true}
   * and blocked threads are released from {@code get} or {@code
   * getFailure}, if any.
   */
  public void whenComplete (Runnable callback);

  /** Attaches a continuation.  After the underlying computation
   * terminates successfully, the continuation object's {@code call}
   * will be invoked with the result of the computation.  No call is
   * made if the underlying computation fails.  The invocation will
   * take place <em>after</em> the computation finishes and
   * <em>before</em> {@code isDone} returns {@code true} and blocked
   * threads are released from {@code get} or {@code getFailure}, if
   * any.
   *
   * <p> If the underlyong computation fails, the continuation is not
   * executed.  The future's {@code get} method retuns {@code null}
   * and its {@code getFailure} method retuns an instance of {@code
   * NotExecutedException} with the failure of the underlying
   * computation as its cause.
   * @return a future on the seconday computation.  This future
   * completes when the secondary computation terminates or when the
   * first computation fails.
   * @see NotExecutedException
   * @see NotExecutedException#getCause
   */
  public <U> Future<U> whenComplete (Continue<? super T, ? extends U> cont);

}

package cs671;

/** An exception used for continuations that are not run.  A
 * continuation to a task that fails is never run.  Instead, it fails
 * fith a {@code NotExecutedException} that contains the cause of the
 * primary failure.
 * @author  Michel Charpentier
 * @version 1.0, 2/10/14
 * @see Future#whenComplete(Continue)
 */
public class NotExecutedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  NotExecutedException (Throwable t) {
    super("continuation not executed", t);
  }
}

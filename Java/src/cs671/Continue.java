package cs671;

/** Continuations.  A {@code Continue<A,B>} is basically a function
 * from A to B.  Such a continuation is used to continue an
 * A-producing computation into a B-producing computation.
 *
 * @author  Michel Charpentier
 * @version 1.0, 2/10/14
 * @see Future#whenComplete(Continue)
 */
public interface Continue<A,B> {

  /** Produces a B value from an A value. */
  public B call (A a) throws Exception;

}

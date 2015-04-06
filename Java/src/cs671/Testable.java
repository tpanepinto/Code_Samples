// $Id: Testable.java 232 2014-01-27 02:40:06Z cs671a $

package cs671;

import java.lang.reflect.Method;

/** Testable classes.  Test methods are annotated with {@code TestMethod}.
 *
 * @author  Michel Charpentier
 * @version 2.0, 01/26/14
 * @see TestMethod
 */
public interface Testable {

  /** Executed before each test.  If this method fails or return
   * false, the corresponding test is not run.
   *
   * @param m the test method soon to be run
   */
  public boolean beforeMethod (Method m) throws Exception;

  /** Executed after each test.  This method runs after each test,
   * whether the test was successful or not.  It does not run if the
   * corresponding test was not run.
   *
   * @param m the test method that was just run
   */
  public void afterMethod (Method m) throws Exception;
}

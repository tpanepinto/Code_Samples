// $Id: TestResult.java 232 2014-01-27 02:40:06Z cs671a $

package cs671;

/** Test result.  A {@code TestResult} object is created for each test
 * that is run and contains information on the test, as decribed
 * below.  Instances of this class are immutable.
 *
 * @author  Michel Charpentier
 * @version 2.0, 01/26/14
 */
public interface TestResult {

  /** Test info.  If the info argument of the {@code TestMethod} annotation
   * of the corresponding test method is not empty, then this is
   * {@code full-method-name + ": " + info}; otherwise, it is {@code
   * full-method-name}.
   *
   * @see TestMethod#info
   */
  public String getInfo ();

  /** Test weight.  This is the weight associated with the {@code TestMethod}
   * annotation of the corresponding test method.
   *
   * @see TestMethod#weight
   */
  public double getWeight ();

  /** Test duration.  This is the duration of the test, in seconds,
   * till completion or up to the point of failure.  It does not
   * include time spent inside {@code beforeMethod} and {@code
   * afterMethod}.
   *
   * @see Testable#beforeMethod
   * @see Testable#afterMethod
   */
  public double getDuration ();

  /** Test status.
   * @return true if the test was successful, false otherwise
   */
  public boolean success ();

  /** Test failure.
   * @return the cause of test failure, or {@code null} if the test
   * was successful
   */
  public Throwable error ();
}

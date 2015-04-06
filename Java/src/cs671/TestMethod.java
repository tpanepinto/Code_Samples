// $Id: TestMethod.java 232 2014-01-27 02:40:06Z cs671a $

package cs671;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/** Annotation for test methods.  This is the only annotation used in
 * the testing system.
 *
 * @author  Michel Charpentier
 * @version 2.0, 01/26/14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TestMethod {

  /** The weight of this test.  Tests with negative weight are silently
   * ignored. */
  double weight () default 0;

  /** A description of the test. */
  String info () default "";
}

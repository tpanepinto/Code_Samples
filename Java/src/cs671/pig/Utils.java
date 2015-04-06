// $Id: Utils.java 264 2014-03-18 00:33:48Z cs671a $

package cs671.pig;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.lang.reflect.Constructor;

/** Utility methods.  This class cannot be instantiated.
 *
 * @author Michel Charpentier
 * @version 1.2, 03/07/12
 */
public class Utils {

  private Utils () {
    throw new AssertionError("This class cannot be instantiated");
  }

  private static final Pattern STRATEGY;
  static {
    String id = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
    String longId = id+"(?:\\."+id+")*";
    STRATEGY = Pattern.compile("("+longId+")(?:\\((.*)\\))?");
  }

  /** Creates a new strategy.  This method uses reflection to load and
   * instantiate a strategy class.  It relies on the default class loader.
   *
   * @return a new instance of the specified class, or {@code null} if it failed
   * @param spec a string that represents the class to instantiate and
   * the parameter to instantiate it with, if any.  It has one of two
   * forms: {@code <classname>} or {@code <classname>(<parameter>)}.
   * The first form will seek a public no argument constructor; the second
   * form will use a public constructor that takes a single string.
   */
  public static Strategy createStrategy (String spec) {
    String strategyName, strategyParam;
    Matcher m = STRATEGY.matcher(spec);
    if (m.matches()) {
      strategyName = m.group(1);
      strategyParam = m.group(2);
    } else {
      System.err.printf("Invalid strategy name: %s%n", spec);
      return null;
    }
    try {
      Class<? extends Strategy> clazz =
        Class.forName(strategyName).asSubclass(Strategy.class);
      Constructor<? extends Strategy> c;
      if (strategyParam == null) {
        c = clazz.getConstructor();
        return c.newInstance();
      } else {
        c = clazz.getConstructor(String.class);
        return c.newInstance(strategyParam);
      }
    } catch (ClassNotFoundException e) {
      System.err.printf("Cannot find class '%s'%n", strategyName);
    } catch (NoSuchMethodException e) {
      System.err.printf("Strategy %s does not have a string constructor%n",
                        strategyName);
    } catch (InstantiationException e) {
      System.err.printf("Strategy %s could not be instantiated: %s%n",
                        strategyName, e.getMessage());
    } catch (IllegalAccessException e) {
      System.err.printf("Strategy %s could not be instantiated: %s%n",
                        strategyName, e.getMessage());
    } catch (java.lang.reflect.InvocationTargetException e) {
      Throwable t = e.getCause();
      System.err.printf("Strategy %s could not be instantiated: %s%n",
                        strategyName, (t==null?e:t).getMessage());
    }
    return null;
  }
}
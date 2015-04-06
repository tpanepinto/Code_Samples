// $Id: Die.java 264 2014-03-18 00:33:48Z cs671a $

package cs671.pig;

import java.util.Random;

/** A fair six-face die.
 *
 * @author Michel Charpentier
 * @version 1.2, 03/07/12
 */
public class Die {

  private Random rand;

  /** Creates a new die with given seed.
   * @param seed the seed, for deterministic playback
   */
  public Die (long seed) {
    this();
    rand.setSeed(seed);
  }

  /** Creates a new die. */
  public Die () {
    rand = new Random();
  }

  /** Rolls the die.
   * @return a randomly chosen number between 1 and 6
   */
  public int roll () {
    return rand.nextInt(6) + 1;
  }
}


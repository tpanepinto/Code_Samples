// $Id: Coin.java 264 2014-03-18 00:33:48Z cs671a $

package cs671.pig;

import java.util.Random;

/** A random coin.  This can be used to select the player who starts a
 * game, or to randomize playing strategies.
 *
 * @author Michel Charpentier
 * @version 1.2, 03/07/12
 */
public class Coin {

  private final double p;
  private final Random rand;

  /** Flips the coin.  Returns true or false, randomly. */
  public boolean flip () {
    return rand.nextDouble() < p;
  }

  /** Creates a fair coin.  */
  public Coin () {
    this(.5);
  }

  /** Creates a fair coin with the given seed.
   * @param seed the seed, for deterministic playback
   */
  public Coin (long seed) {
    this();
    rand.setSeed(seed);
  }

  /** Creates a biased coin with the given seed.
   * @param probForTrue the probability that {@code this.flip()} returns true
   * @param seed the seed, for deterministic playback
   */
  public Coin (long seed, double probForTrue) {
    this(probForTrue);
    rand.setSeed(seed);
  }

  /** Creates a biased coin.  
   * @param probForTrue the probability that {@code this.flip()} returns true
   */
  public Coin (double probForTrue) {
    if (probForTrue < 0 || probForTrue > 1)
      throw new IllegalArgumentException("probability must be 0 <= p <= 1");
    p = probForTrue;
    rand = new Random();
  }

  /** A stateless string representation of the coin that includes its
   * flip probability. */
  public String toString () {
    return String.format("Coin(%.2f)", p);
  }
}

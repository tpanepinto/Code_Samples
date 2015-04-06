// $Id: Strategy.java 264 2014-03-18 00:33:48Z cs671a $

package cs671.pig;

/** A strategy that plays the game of Pig.  This interface can be
 * implemented as automatic players, as user-interfaces or as
 * socket-based protocols, among other things.
 *
 * <p>Implementations are expected to make {@code roll(1)} return
 * false, although the value should never be used by applications.  By
 * convention, implementations should offer at least a public, no
 * argument constructor or a public constructor that takes a single
 * string.  Such implementations can then benefit from the
 * reflection-based loading mechanism of {@code Utils.createStrategy}.
 *
 * @author Michel Charpentier
 * @version 1.2, 02/07/12
 *
 * @see Utils#createStrategy
 */
public interface Strategy {

  /** Decides on a roll.
   * @param value the value of the die
   * @return true to roll, false to hold
   * @throws IllegalStateException if called at a time that is
   * inconsistent with the rules of the game
   */
  public boolean roll (int value);

  /** Analyses the oponent play.
   * @param values an array of dice values, only the first {@code
   * count} of which are used
   * @param count the number of times the opponent rolled the die
   * @throws IllegalStateException if called at a time that is
   * inconsistent with the rules of the game
   * @throws IllegalArgumentException if {@code count < 1}, {@code
   * values.length < count}, values contains numbers smaller than 1 or
   * larger than 6, or values contain 1 in a position other than the
   * last.
   */
  public void opponentPlay (int[] values, int count);

  /** Starts a new game.
   * @param iStart is true iff this player plays first (i.e., it is
   * false for the other player).
   * @return true if this strategy accepts to play another game, false otherwise
   */
  public boolean startGame (boolean iStart);

  /** Ends a game.
   * @param info comments on the game from the the authority that ran it
   */
  public void endGame (String info);

  /** The name of this strategy. */
  public String getName ();
}
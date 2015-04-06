// $Id: Competition.java 264 2014-03-18 00:33:48Z cs671a $

package cs671.pig;

import cs671.pig.*;

/** Competitions for Pig game strategies.  Instances of this class can
* be used to evaluate automatic strategies over large numbers of
* games, or to play few games among human-based players.  Note that,
* although the game does not have the notion of a tie, a draw is
* declared if no player has won after 1000 rounds.
*
* @author Michel Charpentier
* @version 1.2, 02/07/12
*/
public class Competition {

  /** The starting policies.*/
  public enum StartPolicy {
    /** First player always starts first.*/
    FIRST     {void start (Competition c) {c.zeroStarts = true;}},
    /** Second player always starts first.*/
    SECOND    {void start (Competition c) {c.zeroStarts = false;}},
    /** Players alternate. */
    ALTERNATE {void start (Competition c) {c.zeroStarts = !c.zeroStarts;}},
    /** The winner of the previous game starts the next game.*/
    WINNER    {void start (Competition c) {c.zeroStarts = c.zeroWins;}},
    /** The player to start is chosen randomly (and fairly).*/
    RANDOM    {void start (Competition c) {c.zeroStarts = c.coin.flip();}};

    abstract void start (Competition c);
  }

  /** The number of rounds before a game is declared a tie. */
  public static final int LIMIT = 1000;

  private final Strategy[] strategies;
  private final String[] names;
  private StartPolicy policy = StartPolicy.ALTERNATE;
  private boolean zeroWins, zeroStarts;
  private final Coin coin;
  private final Die die;
  private int[] dice, scores;
  private boolean verbose;

  /** Creates a competition between the two strategies. The player who
   * starts the first game is chosen randomly.
   */
  public Competition (Strategy s1, Strategy s2) {
    strategies = new Strategy[] {s1, s2};
    names = new String[] {s1.getName(), s2.getName()};
    scores = new int[2];
    coin = new Coin();
    die = new Die();
    zeroStarts = coin.flip();
    dice = new int[16];
  }

  /** Sets the start policy. The default policy is to alternate. */
  public void setStartPolicy (StartPolicy policy) {
    this.policy = policy;
  }

  /** Sets verbosity.  If true, all the details of each game are
   * displayed on the terminal; if false, the competition is
   * completely silent.
   */
  public void setVerbose (boolean set) {
    verbose = set;
  }

  /** Plays one game.
   * @return the winning strategy, or {@code null} in case of a tie.
   */
  public Strategy play () {
    int i = playi();
    if (i < 0)
      return null;
    assert i == 0 || i == 1;
    return strategies[i];
  }

  private int playi () {
    try {
      policy.start(this);
      if (!(strategies[0].startGame(zeroStarts) &
            strategies[1].startGame(!zeroStarts)))
        return -1;
      scores[0] = scores[1] = 0;
      int t = zeroStarts? 0 : 1;
      for (int r=0; r<1000; r++) {
        int score = scores[t];
        assert score < 100;
        int d, n = 0, turn = 0;
        do {
          if (n >= dice.length)
            extendDice();
          dice[n++] = d = die.roll();
          if (d > 1)
            turn += d;
          else
            turn = 0;
        } while (d > 1 & strategies[t].roll(d));
        strategies[1-t].opponentPlay(dice, n);
        scores[t] = score += turn;
        if (verbose) {
          System.out.printf("%s: ", names[t]);
          for (int i=0; i<n-1; i++)
            System.out.printf("%d, ", dice[i]);
          int last = dice[n-1];
          System.out.println(last);
          if (last > 1)
            System.out.printf("  score is now %d%n", score);
        }
        if (score >= 100) {
          endGame();
          if (verbose)
            System.out.printf("%s wins.%n%n", names[t]);
          return t;
        }
        t = 1 - t;
      }
    } catch (Throwable t) {
      System.err.printf("Game interrupted: %s%n", t.getMessage());
    }
    try { // one of them may already be gone
      endGame();
    } catch (Throwable t) {}
    return -1; // no winner
  }

  private void endGame () {
    int s0 = scores[0], s1 = scores[1];
    strategies[0].endGame(s0+"/"+s1);
    strategies[1].endGame(s1+"/"+s0);
  }

  /** Plays many games.
   * @return the percentage of games won by the first player, or
   * {@link Double#NaN} if all games were ties.
   */
  @SuppressWarnings("fallthrough")
  public double play (int count) {
    if (count < 1)
      throw new IllegalArgumentException("count must be > 0");
    int w = 0, t = 0;
    for (int i=0; i<count; i++) {
      switch (playi()) {
      case 0: w++; // fall through
      case 1: t++; // fall through
      case -1: break;
      default: throw new AssertionError("incorrect return value from playi()");
      }
    }
    if (t < count) {
      System.err.printf("WARNING: %d games were not decided.%n", count - t);
      if (t == 0)
        return Double.NaN;
    }
    return (double)w / t;
  }

  private void extendDice () {
    dice = java.util.Arrays.copyOf(dice, dice.length * 2);
  }

  private static void help () {
    System.out.println
      ("Usage: Competition [-quiet] [-count <nb games>] "+
       "[-policy <start policy>] strategy1[(param)] strategy2[(param)]\n"+
       "  -quiet: only displays stats at the end, defaults to false\n"+
       "  -count: max number of games played, defaults to 1\n"+
       "  -policy: available start policies are: "+
       "FIRST, SECOND, ALTERNATE (default), WINNER and RANDOM");
  }

  /** Command-line program.  This program uses reflection to create
   * the strategies specified on the command-line.
   */
  public static void main (String[] args) {
    int count = 1;
    boolean quiet = false;
    StartPolicy policy = StartPolicy.ALTERNATE;
    int i;
    for (i=0; i<args.length; i++) {
      String arg = args[i];
      try {
        if (arg.equals("-quiet")) {
          quiet = true;
          continue;
        }
        if (arg.equals("-count")) {
          count = Integer.parseInt(args[++i]);
          continue;
        }
        if (arg.equals("-policy")) {
          policy = StartPolicy.valueOf(args[++i].toUpperCase());
          continue;
        }
      } catch (IndexOutOfBoundsException e) {
        System.err.printf("Incomplete option: %s%n", arg);
        return;
      } catch (NumberFormatException e) {
        System.err.printf("Cannot parse number: '%s'%n", args[i]);
        return;
      }
      break;
    }
    if (i != args.length - 2) {
      help();
      return;
    }
    Strategy s1 = Utils.createStrategy(args[i++]);
    Strategy s2 = Utils.createStrategy(args[i++]);
    if (s1 == null || s2 == null)
      return;
    Competition c = new Competition(s1, s2);
    c.setStartPolicy(policy);
    c.setVerbose(!quiet);
    System.out.printf("%.3f%n", c.play(count));
  }
}
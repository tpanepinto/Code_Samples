// $Id: TerminalClient.java 264 2014-03-18 00:33:48Z cs671a $

package cs671.pig.server;

import cs671.pig.Strategy;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.net.Socket;

/** Terminal-based client for the game of Pig.  This implements the
* client side of the protocol described in {@link cs671.pig.server}.  Note
* that, although the initial negotiation with the server is
* text-based, game play could be GUI-based or even automated,
* depending on the nature of the strategy used at construction time.
*
* @author Michel Charpentier
* @version 1.2, 03/07/12
* @see <a href="TerminalClient.java">TerminalClient.java</a>
*/
public class TerminalClient implements Runnable {

  private Strategy strategy;
  private int games, gameCount, victories, port;
  private String name, host;

  /** A strategy that stops after a specified number of games (useful
   * for automatic strategies that never stop).
   */
  private class CountingStrategy implements Strategy {

    public boolean roll (int value) {
      return strategy.roll(value);
    }
    public void opponentPlay (int[] values, int count) {
      strategy.opponentPlay(values, count);
    }
    public boolean startGame (boolean iStart) {
      return games++ < gameCount && strategy.startGame(iStart);
    }
    public void endGame (String info) {
      info = "Game "+games+"/"+gameCount+": "+info;
      System.out.printf("%s%n", info);
      strategy.endGame(info);
    }
    public String getName () {
      return name;
    }
  }

  /** Create a terminal-based client for the specified strategy.
   * Initial negotiations with the server are terminal-based, but how
   * the game is played depends on strategy {@code g}.  If it is the
   * terminal-based {@code pig.UserTerminalStrategy}, this program
   * becomes a text-based interface to the game as well.
   *
   * @param g the strategy that gives its "brains" to this client
   * @param name the name under which the client will play
   * @param host server host
   * @param port server port
   */
  public TerminalClient (Strategy g, String name, String host, int port) {
    strategy = g;
    gameCount = 1;
    this.name = name;
    this.host = host;
    this.port = port;
    if (port < 0 || port > 65535)
      throw new IllegalArgumentException("Invalid port number [0..65535].");
  }

  /** Sets the maximum number of games played. By default, it is 1.*/
  public void setGameCount (int count) {
    if (count < 1)
      throw new IllegalArgumentException("count must be at least 1");
    gameCount = count;
  }

  /** Runs the client side of the protocol.  First, the client runs
   * the initialization stages, using thde terminal.  Then, it uses a
   * {@code StrategyToProtocol} object, along with the given strategy,
   * to play one or more games.  The method terminates when the
   * specified number of games have been played, or {@code
   * StrategyToProtocol#run} stops for some other reason.
   * @see StrategyToProtocol#run
   */
  public void run () {
    BufferedReader terminal =
      new BufferedReader(new InputStreamReader(System.in));
    BufferedReader in;
    BufferedWriter out;
    String opponent = null;
    try {
      Socket socket = new Socket(host, port);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      Message m;
      while (true) {
        m = Message.read(in);
        if (m.header == Message.Header.ERROR) {
          System.err.printf("Server error: %s%n", m.content);
          return;
        }
        if (m.header != Message.Header.INIT) {
          System.err.printf
            ("Server is crazy; instead of INIT, it said: '%s'%n", m);
          return;
        }
        if (opponent == null) {
          opponent = ",";
          System.out.println(m.content);
          Message.makeINIT(name).send(out);
        } else if (opponent.isEmpty() || m.content.equals(opponent)) {
          break;
        } else {
          System.out.printf("The following players are in the lobby: %s%n",
                            m.content);
          System.out.print("Pick one, or press return to join them:  ");
          opponent = terminal.readLine();
          Message.makeINIT(opponent).send(out);
        }
      }
      System.out.printf("You are playing against %s.%n", m.content);
    } catch (java.io.IOException e) {
      System.err.printf("IO error: %s%n", e.getMessage());
      return;
    } catch (Message.Exception e) {
      System.err.printf
        ("Server is crazy; instead of a message, it said: '%s'%n",
         e.getMessage());
      return;
    }
    Strategy st = new CountingStrategy();
    StrategyToProtocol runner = new StrategyToProtocol(st, in, out);
    runner.run();
  }

  private static void help () {
    System.out.println
      ("Usage: TerminalClient [-name <name>] [-count <nb games>] "+
       "[-strategy <classname>[(param)]] <host name> <port>\n"+
       "  -name: player name, defaults to username\n"+
       "  -count: max number of games played, defaults to 1\n"+
       "  -strategy: full name of a strategy class, with optional parameter, "+
       "defaults to pig.UserTerminalStrategy(name)");
  }

  /** Command-line program.  This program uses reflection to create
   * the strategies specified on the command-line.
   */
  public static void main (String[] args) {
    String strategySpec = null;
    String name = System.getenv("USER");
    String host;
    int port;
    int count = 1;
    int i;
    for (i=0; i<args.length; i++) {
      String arg = args[i];
      try {
        if (arg.equals("-help")) {
          help();
          return;
        }
        if (arg.equals("-count")) {
          count = Integer.parseInt(args[++i]);
          continue;
        }
        if (arg.equals("-name")) {
          name = args[++i];
          continue;
        }
        if (arg.equals("-strategy")) {
          strategySpec = args[++i];
          continue;
        }
        break;
      } catch (IndexOutOfBoundsException e) {
        System.err.printf("Incomplete option: %s%n", arg);
        return;
      } catch (NumberFormatException e) {
        System.err.printf("Cannot parse number: '%s'%n", args[i]);
        return;
      }
    }
    if (i >= args.length) {
      System.err.println("Missing host name");
      help();
      return;
    }
    host = args[i++];
    if (host.startsWith("-")) {
      System.err.printf("Unknown option: %s%n", host);
      help();
      return;
    }
    if (i >= args.length) {
      System.err.println("Missing host name or port number");
      help();
      return;
    }
    try {
      port = Integer.parseInt(args[i]);
    } catch (NumberFormatException e) {
      System.err.printf("Cannot parse port number: '%s'%n", args[i]);
      return;
    }
    Strategy strategy;
    if (strategySpec == null) {
      strategy = new cs671.pig.UserTerminalStrategy(name);
    } else {
      strategy = cs671.pig.Utils.createStrategy(strategySpec);
      if (strategy == null)
        return;
    }
    TerminalClient client = new TerminalClient(strategy, name, host, port);
    client.setGameCount(count);
    client.run();
  }
}

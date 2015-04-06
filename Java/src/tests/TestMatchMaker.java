// $Id: TestMatchMaker.java 696 2012-06-04 18:09:33Z charpov $

package tests.pig;

import cs671.pig.server.MatchMaker;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import charpov.grader.*;
import static org.testng.Assert.*;

@Test(val=30)
class TestMatchMaker {

  Random rand = new Random();

  MatchMaker server;
  int port;
  String host = "localhost";
  ExecutorService exec;
  String ghost1, ghost2;

  void BEFORE () {
    exec = java.util.concurrent.Executors.newFixedThreadPool(32);
    int tries = 0;
    boolean started = false;
    do {
      try {
        port = rand.nextInt(5000) + 55000;
        server = new MatchMaker(port);
        server.start();
        started = true;
        System.out.printf("server started on port %d%n", port);
      } catch (Exception e) {
        if (++tries > 10)
          fail("server not started for unknown reasons ("+e.getMessage()+"); contact instructor");
      }
    } while (!started);
  }

  void AFTER () {
    exec.shutdown();
    if (server != null)
      server.stop();
  }

  static void checkResults (List<? extends Future<?>> list) throws Throwable {
    Throwable toThrow = null;
    for (Future<?> f : list) {
      try {
        f.get();
      } catch (ExecutionException e) {
        Throwable t = e.getCause();
        if (t instanceof AssertionError)
          throw t;
        if (toThrow == null) {
          if (t instanceof NoSuchElementException)
            toThrow = new AssertionError("server disconnected");
          else
            toThrow = t;
        }
      }
    }
    if (toThrow != null)
      throw toThrow;
  }

  abstract class ServerHitter implements Callable<Void> {

    final Socket socket;
    final PrintWriter out;
    final Scanner in;

    ServerHitter () {
      try {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());
      } catch (IOException e) {
        throw new AssertionError("I/O exception: "+e.getMessage());
      }
    }
    public final Void call () throws IOException {
      try {
        doCall();
        return null;
      } finally {
        socket.close();
      }
    }
    abstract void doCall () throws IOException;
  }

  class Garbage1 extends ServerHitter {
    void doCall () {
      out.printf("%s%n%s%n", "INIT:MC", "START:YES");
    }
  }

  class Garbage2 extends ServerHitter {
    void doCall () {
      out.printf("%s%n%s%n", "INIT:MC", "blahblahblah");
    }
  }

  class Garbage3 extends ServerHitter {
    void doCall () {
    }
  }

  class LobbyPlayer extends ServerHitter {

    final String name;

    LobbyPlayer (String s) {
      name = s;
    }

    void doCall () {
      String line = in.nextLine();
      assertTrue(line.startsWith("INIT:"));
      out.printf("INIT:%s%nINIT:%n", name);
      assertTrue(in.nextLine().startsWith("INIT:"));
      assertTrue(in.nextLine().startsWith("INIT:"));
      assertTrue(in.nextLine().startsWith("START:"));
      for (int i=0; i<5; i++) {
        out.printf("START:YES%n");
        while (true) {
          line = in.nextLine();
          if (line.startsWith("END:"))
            break;
          if (line.equals("DIE:1") || line.startsWith("DICE:"))
            continue;
          assertTrue(line.startsWith("DIE:"));
          if (rand.nextDouble() < .8)
            out.printf("DECIDE:ROLL%n");
          else
            out.printf("DECIDE:HOLD%n");
        }
        assertTrue(in.nextLine().startsWith("START:"));
      }
      out.printf("START:NO%n");
    }
  }

  class OtherPlayer extends ServerHitter {

    String name, opName;

    OtherPlayer (String me, String him) {
      name = me;
      opName = him;
    }

    void doCall () {
      String line = in.nextLine();
      assertTrue(line.startsWith("INIT:"));
      out.printf("INIT:%s%n", name);
      if (opName != null) {
        line = in.nextLine();
        while (true) {
          assertTrue(line.startsWith("INIT:"));
          if (line.contains(opName))
            out.printf("INIT:%s%n", opName);
          else
            out.printf("INIT:someonewhoisnotthere%n");
          line = in.nextLine();
          assertTrue(line.startsWith("INIT:"));
          if (line.equals("INIT:"+opName))
            break;
        }
      } else { // look for ghosts
        line = in.nextLine();
        while (true) {
          assertTrue(line.startsWith("INIT:"));
          String[] ghosts = line.substring(5).split("\\s*,\\s*");
          if (ghosts.length == 0)
            fail("no ghosts found");
          opName = ghosts[0].trim();
          out.printf("INIT:%s%n", opName);
          line = in.nextLine();
          assertTrue(line.startsWith("INIT:"));
          if (line.equals("INIT:"+opName))
            break;
        }
      }
      line = in.nextLine();
      for (int i=0; i<5; i++) {
        out.printf("START:YES%n");
        boolean skip;
        if (line.equals("START:NO")) {
          skip = true;
        } else {
          assertEquals(line, "START:YES");
          skip = false;
        }
        while (true) {
          line = in.nextLine();
          if (line.startsWith("END:"))
            break;
          if (skip) {
            assertTrue(line.startsWith("DICE:"));
            skip = false;
            continue;
          }
          skip = true;
          if (!line.equals("DIE:1")) {
            assertTrue(line.startsWith("DIE:"));
            out.printf("DECIDE:HOLD%n");
          }
        }
        line = in.nextLine();
        assertTrue(line.startsWith("START:"));
      }
      out.printf("START:NO%n");
    }
  }
  
 

  @Test(val=-1, timeout=10000) void bonus1 () throws Throwable {
    checkResults(exec.invokeAll(Arrays.asList
                                (new OtherPlayer("MC", null))));
  }

  @Test(val=-1, timeout=10000) void bonus2 () throws Throwable {
    OtherPlayer p1 = new OtherPlayer("MC1", null);
    OtherPlayer p2 = new OtherPlayer("MC2", null);
    checkResults(exec.invokeAll(Arrays.asList(p1, p2)));
    ghost1 = p1.opName;
    ghost2 = p2.opName;
  }

  @Test(val=-1, timeout=10000) void bonus3 () throws Throwable {
    Socket socket = new Socket(host, port);
    try {
      String line = new Scanner(socket.getInputStream()).nextLine();
      assertTrue(line.startsWith("INIT:"));
      assertTrue(line.contains(ghost1));
      assertTrue(line.contains(ghost2));
    } finally {
      if (socket != null)
        socket.close();
    }
  } 

  @Test(val=2) void restart () throws Throwable {
    try {
      server.start();
    } catch (IllegalStateException e) {
      System.out.println("Test restart would pass.");
      return; // ok
    }
    fail("server cannot be restarted");
  }

  @Test(val=5, timeout=10000) void twoPlayers () throws Throwable {
    checkResults(exec.invokeAll(Arrays.asList
                                (new LobbyPlayer("MrLobby"),
                                 new OtherPlayer("MC", "MrLobby"))));
    System.out.println("Test twoPlayers would pass.");
  }

  @Test(val=4, timeout=10000) void twoPlayersSomeGarbage () throws Throwable {
    checkResults(exec.invokeAll(Arrays.asList
                                (new Garbage1(),
                                 new Garbage2(),
                                 new LobbyPlayer("MrLobby"),
                                 new OtherPlayer("MC", "MrLobby"),
                                 new Garbage1(),
                                 new Garbage2())));
    System.out.println("Test twoPlayersSomeGarbage would pass.");
  }

  @Test(val=4, timeout=10000) void twoPlayersMoreGarbage () throws Throwable {
    List<Callable<Void>> list = new ArrayList<Callable<Void>>();
    for (int i=0; i<10; i++) {
      list.add(new Garbage1());
      list.add(new Garbage2());
      list.add(new Garbage3());
    }
    list.add(new LobbyPlayer("MrLobby"));
    list.add(new OtherPlayer("MC", "MrLobby"));
    for (int i=0; i<10; i++) {
      list.add(new Garbage1());
      list.add(new Garbage2());
      list.add(new Garbage3());
    }
    checkResults(exec.invokeAll(list));
    System.out.println("Test twoPlayersMoreGarbage would pass.");
  }

  @Test(val=5, timeout=30000) void tenPlayers () throws Throwable {
    List<Callable<Void>> list = new ArrayList<Callable<Void>>();
    for (int i=0; i<5; i++)
      list.add(new LobbyPlayer("a"+i));
    for (int i=0; i<5; i++)
      list.add(new OtherPlayer("b"+i, "a"+i));
    checkResults(exec.invokeAll(list));
    System.out.println("Test tenPlayers would pass.");
  }

  @Test(val=5, timeout=60000) void fiftyPlayers () throws Throwable {
    List<Callable<Void>> list = new ArrayList<Callable<Void>>();
    for (int i=0; i<25; i++)
      list.add(new LobbyPlayer("a"+i));
    for (int i=0; i<25; i++)
      list.add(new OtherPlayer("b"+i, "a"+i));
    checkResults(exec.invokeAll(list));
    System.out.println("Test fiftyPlayers would pass.");
  }

  @Test(val=5, timeout=10000) void testRemote () throws Exception {
    Runtime r = Runtime.getRuntime();
    String remote = "charpov@berlioz.cs.unh.edu";
    String local = "agate.cs.unh.edu";
    Process p = r.exec("ssh "+remote+" ./testServer "+local+" "+port);
    Scanner in = new Scanner(p.getErrorStream());
    StringBuilder b = new StringBuilder();
    while (in.hasNextLine())
      b.append(in.nextLine());
    in.close();
    assertEquals(p.waitFor(), 0, b.toString());
    System.out.println("Test testRemote would pass.");
  }

  public static void main (String[] args) throws Exception {
    java.util.logging.Logger.getLogger("charpov.grader")
      .setLevel(java.util.logging.Level.WARNING);
    Tester tester = new Tester(TestMatchMaker.class);
    tester.run();
  }
}

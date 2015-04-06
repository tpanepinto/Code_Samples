// $Id: TestStrategyToProtocol.java 696 2012-06-04 18:09:33Z charpov $

package tests.pig;

import cs671.pig.Strategy;
import cs671.pig.server.StrategyToProtocol;
import cs671.pig.server.Message;
import java.io.*;
import java.util.*;

import charpov.grader.*;
import static org.testng.Assert.*;

@Test(val=20)
public class TestStrategyToProtocol {

  public static class DetStrategy implements Strategy {
    int games;
    Random rand;
    StringBuffer rolls, opPlays, starts, ends;
    public DetStrategy (long seed, int games) {
      rand = new Random(seed);
      rolls = new StringBuffer();
      starts = new StringBuffer();
      ends = new StringBuffer();
      opPlays = new StringBuffer();
      this.games = games;
    }
    public DetStrategy (long seed) {
      this(seed, 1);
    }
    public boolean roll (int d) {
      rolls.append(d);
      return rand.nextDouble() < .5;
    }
    public void opponentPlay (int[] a, int c) {
      for (int i=0; i<c; i++)
        opPlays.append(a[i]);
    }
    public boolean startGame (boolean iStart) {
      starts.append(iStart);
      return games-- > 0;
    }
    public void endGame (String info) {
      ends.append(info);
    }
    public String getName () {
      return "Det";
    }
    public String getStarts () {
      return starts.toString();
    }
    public String getEnds () {
      return ends.toString();
    }
    public String getRolls () {
      return rolls.toString();
    }
    public String getOpPlays () {
      return opPlays.toString();
    }
  }

  DetStrategy strategy;

  String runProtocol (String in) throws IOException {
    BufferedReader bin = new BufferedReader(new StringReader(in));
    StringWriter out = new StringWriter();
    BufferedWriter bout = new BufferedWriter(out);
    new StrategyToProtocol(strategy, bin, bout).run();
    bin.close();
    bout.close();
    return out.toString();
  }

  @Test(val=12)
  public void test1() throws IOException {
    strategy = new DetStrategy(42);
    String in = 
      "START:NO\n"+
      "DICE:433\n"+
      "DIE:6\n"+
      "DICE:1\n"+
      "DIE:4\n"+
      "DICE:456\n"+
      "DIE:2\n"+
      "DIE:6\n"+
      "DIE:3\n"+
      "DICE:666542\n"+
      "DIE:1\n"+
      "DICE:65\n"+
      "DIE:5\n"+
      "DIE:6\n"+
      "DIE:2\n"+
      "DIE:6\n"+
      "DICE:646\n"+
      "DIE:1\n"+
      "DICE:6662\n"+
      "DIE:6\n"+
      "DIE:6\n"+
      "DICE:65\n"+
      "END:You lost!\n"+
      "START:YES\n";
    String out = 
      "START:YES\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:HOLD\n"+
      "START:NO\n";
    assertEquals( runProtocol(in), out);
    assertEquals( strategy.getRolls(), "6426315626166");
    assertEquals( strategy.getOpPlays(), "433145666654265646666265");
    assertEquals( strategy.getStarts(), "falsetrue");
    assertEquals( strategy.getEnds(), "You lost!");
  }

  @Test(val=12)
  public void test2() throws IOException {
    strategy = new DetStrategy(1789);
    String in =
      "START:YES\n"+
      "DIE:3\n"+
      "DICE:232\n"+
      "DIE:4\n"+
      "DICE:6652\n"+
      "DIE:1\n"+
      "DICE:23\n"+
      "DIE:4\n"+
      "DICE:231\n"+
      "DIE:6\n"+
      "DIE:5\n"+
      "DIE:3\n"+
      "DICE:222\n"+
      "DIE:6\n"+
      "DIE:6\n"+
      "DICE:34562\n"+
      "DIE:5\n"+
      "DICE:3\n"+
      "DIE:6\n"+
      "DICE:55\n"+
      "DIE:6\n"+
      "DIE:6\n"+
      "DIE:6\n"+
      "DIE:5\n"+
      "DICE:2341\n"+
      "DIE:5\n"+
      "DICE:2224\n"+
      "DIE:6\n"+
      "DICE:55\n"+
      "DIE:6\n"+
      "DICE:543454343234354321\n"+
      "DIE:6\n"+
      "DIE:5\n"+
      "DIE:2\n"+
      "DIE:2\n"+
      "END:You win!\n"+
      "START:NO\n";
    String out =
      "START:YES\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:HOLD\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:ROLL\n"+
      "DECIDE:HOLD\n"+
      "START:NO\n";
    assertEquals( runProtocol(in), out);
    assertEquals( strategy.getRolls(), "3414653665666655666522");
    assertEquals("232665223231222345623552341222455543454343234354321",
                 strategy.getOpPlays());
    assertEquals( strategy.getStarts(), "truefalse");
    assertEquals( strategy.getEnds(), "You win!");
  }

  @Test(val=7)
  public void test3() throws IOException {
    strategy = new DetStrategy(42);
    String in = 
      "START:NO\n"+
      "DICE:66666666666666665\n"+
      "END:Done\n"+
      "START:YES\n";
    String out = 
      "START:YES\n"+
      "START:NO\n";
    assertEquals( runProtocol(in), out);
    assertEquals( strategy.getRolls(), "");
    assertEquals( strategy.getOpPlays(), "66666666666666665");
    assertEquals( strategy.getStarts(), "falsetrue");
    assertEquals( strategy.getEnds(), "Done");
  }

  @Test(val=7)
  public void test4() throws IOException {
    strategy = new DetStrategy(42,2);
    String in = 
      "START:NO\n"+
      "DICE:66666666666666665\n"+
      "END:Done\n"+
      "START:YES\n"+
      "DIE:6\n"+
      "DICE:66666666666666665\n"+
      "END:101 to 6\n"+
      "START:NO\n";
    String out = 
      "START:YES\n"+
      "START:YES\n"+
      "DECIDE:HOLD\n"+
      "START:NO\n";
    assertEquals( runProtocol(in), out);
    assertEquals( strategy.getRolls(), "6");
    assertEquals( strategy.getOpPlays(), "6666666666666666566666666666666665");
    assertEquals( strategy.getStarts(), "falsetruefalse");
    assertEquals( strategy.getEnds(), "Done101 to 6");
  }

  @Test(val=2, timeout=1000)
    public void test5()
    throws IOException, InterruptedException, Message.Exception {
    PipedInputStream pin = new PipedInputStream();
    BufferedReader in = new BufferedReader(new InputStreamReader(pin));
    PipedOutputStream pout = new PipedOutputStream(pin);
    BufferedWriter out =
      new BufferedWriter(new OutputStreamWriter(new OutputStream() {
          public void write (int b) {
          }
        }));
    strategy = new DetStrategy(42,5);
    StrategyToProtocol r = new StrategyToProtocol(strategy, in, out);
    Thread t = new Thread(r);
    t.start();
    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(pout));
    Message.START_YES.send(w);
    Thread.sleep(100);
    r.endPlay();
    try {
      Message.makeDIE(1).send(w);
      Message.makeDICE(new int[]{6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,5}, 17).send(w);
      Message.makeEND("You lost!").send(w);
      Message.START_NO.send(w);
    } catch (Message.Exception e) {}
    t.join();
    w.close();
    in.close();
    out.close();
    assertTrue(strategy.getStarts().startsWith("true"));
  }

  public static void main (String[] args) throws Exception {
    long seed = Long.parseLong(args[0]);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
    StrategyToProtocol r = new StrategyToProtocol
      (new DetStrategy(seed,2), in, out);
    r.run();
  }
}

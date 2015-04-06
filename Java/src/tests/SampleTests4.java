// $Id: SampleTests4.java 696 2012-06-04 18:09:33Z charpov $

package tests;

import charpov.grader.*;
import static org.testng.Assert.*;

import cs671.pig.*;
import cs671.pig.server.*;

import java.util.*;
import java.io.*;

class SampleTests4 {

  public static void main (String[] args) throws Exception {
    java.util.logging.Logger.getLogger("charpov.grader")
      .setLevel(java.util.logging.Level.WARNING);
    Tester tester = new Tester(Sample.class);
    tester.run();
  }
}

class Sample {

  @Test(val=1) void testParse1 () throws Message.Exception {
    Message m = Message.parse("DIE:6");
    assertEquals(m.header, Message.Header.DIE);
    assertEquals(m.content, "6");
  }

  @Test(val=2) void testParse2 () throws Message.Exception {
    Message m = Message.parse("INIT:foo\00bar");
    assertEquals(m.header, Message.Header.INIT);
    assertEquals(m.content, "foo\nbar");
  }

  @Test(val=1) void testIOExRead () throws IOException {
    BufferedReader in = new BufferedReader(new FileReader("/dev/null"));
    in.close();
    try {
      Message.read(in);
      fail();
    } catch (Message.Exception e) {
      assertTrue(e.getCause() instanceof IOException);
    }
  }

  @Test(val=1) void testParseEx () {
    try {
      Message.parse("Init:foo");
      fail();
    } catch (Message.Exception e) {
      assertTrue(e.getMessage().contains("Init"));
    }
  }

  StringWriter out;
  BufferedWriter bout;
  BufferedReader bin;
  Strategy strategy;

  Strategy createStrategy (String in) throws IOException {
    bin = new BufferedReader(new StringReader(in));
    out = new StringWriter();
    bout = new BufferedWriter(out);
    return strategy = new ProtocolToStrategy("Test", bin, bout);
  }

  void opponentPlay (int... values) {
    strategy.opponentPlay(values, values.length);
  }

  @Test(val=10) void test1() throws IOException {
    String in =
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
    String out =
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
    strategy = createStrategy(in);
    assertTrue(strategy.startGame(false));
    opponentPlay(4,3,3);
    assertFalse(strategy.roll(6));
    opponentPlay(1);
    assertFalse(strategy.roll(4));
    opponentPlay(4,5,6);
    assertTrue(strategy.roll(2));
    assertTrue(strategy.roll(6));
    assertFalse(strategy.roll(3));
    opponentPlay(6,6,6,5,4,2);
    strategy.roll(1); // returned value is not specified
    opponentPlay(6,5);
    assertTrue(strategy.roll(5));
    assertTrue(strategy.roll(6));
    assertTrue(strategy.roll(2));
    assertFalse(strategy.roll(6));
    opponentPlay(6,4,6);
    strategy.roll(1);
    opponentPlay(6,6,6,2);
    assertTrue(strategy.roll(6));
    assertFalse(strategy.roll(6));
    opponentPlay(6,5);
    strategy.endGame("You lost!");
    assertFalse(strategy.startGame(true));
    bin.close();
    bout.close();
    assertEquals(this.out.toString(), out);
  }

  static class DetStrategy implements Strategy {
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

  DetStrategy strategy2;

  String runProtocol (String in) throws IOException {
    BufferedReader bin = new BufferedReader(new StringReader(in));
    StringWriter out = new StringWriter();
    BufferedWriter bout = new BufferedWriter(out);
    new StrategyToProtocol(strategy2, bin, bout).run();
    bin.close();
    bout.close();
    return out.toString();
  }

  @Test(val=10) void test2() throws IOException {
    strategy2 = new DetStrategy(42);
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
    assertEquals(runProtocol(in), out);
    assertEquals(strategy2.getRolls(), "6426315626166");
    assertEquals(strategy2.getOpPlays(), "433145666654265646666265");
    assertEquals(strategy2.getStarts(), "falsetrue");
    assertEquals(strategy2.getEnds(), "You lost!");
  }
}

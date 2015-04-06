// $Id: TestProtocolToStrategy.java 696 2012-06-04 18:09:33Z charpov $

package tests.pig;

import cs671.pig.Strategy;
import cs671.pig.server.ProtocolToStrategy;
import java.io.*;

import charpov.grader.*;
import static org.testng.Assert.*;

@Test(val=20)
public class TestProtocolToStrategy {

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

  @Test(val=13)
  public void test1() throws IOException {
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
    assertEquals( this.out.toString(), out);
  }

  @Test(val=13)
  public void test2() throws IOException {
    String in = 
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
    String out = 
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
    strategy = createStrategy(in);
    assertTrue(strategy.startGame(true));
    assertFalse(strategy.roll(3));
    opponentPlay(2,3,2);
    assertFalse(strategy.roll(4));
    opponentPlay(6,6,5,2);
    strategy.roll(1);
    opponentPlay(2,3);
    assertFalse(strategy.roll(4));
    opponentPlay(2,3,1);
    assertTrue(strategy.roll(6));
    assertTrue(strategy.roll(5));
    assertFalse(strategy.roll(3));
    opponentPlay(2,2,2);
    assertTrue(strategy.roll(6));
    assertFalse(strategy.roll(6));
    opponentPlay(3,4,5,6,2);
    assertFalse(strategy.roll(5));
    opponentPlay(3);
    assertFalse(strategy.roll(6));
    opponentPlay(5,5);
    assertTrue(strategy.roll(6));
    assertTrue(strategy.roll(6));
    assertTrue(strategy.roll(6));
    assertFalse(strategy.roll(5));
    opponentPlay(2,3,4,1);
    assertFalse(strategy.roll(5));
    opponentPlay(2,2,2,4);
    assertFalse(strategy.roll(6));
    opponentPlay(5,5);
    assertFalse(strategy.roll(6));
    opponentPlay(5,4,3,4,5,4,3,4,3,2,3,4,3,5,4,3,2,1);
    assertTrue(strategy.roll(6));
    assertTrue(strategy.roll(5));
    assertTrue(strategy.roll(2));
    assertFalse(strategy.roll(2));
    strategy.endGame("You win!");
    assertFalse(strategy.startGame(false));
    bin.close();
    bout.close();
    assertEquals( this.out.toString(), out);
  }

  @Test(val=7)
  public void test3() throws IOException {
    String in = 
      "START:YES\n"+
      "START:NO\n";
    String out = 
      "START:NO\n"+
      "DICE:66666666666666665\n"+
      "END:Done\n"+
      "START:YES\n";
    strategy = createStrategy(in);
    assertTrue(strategy.startGame(false));
    opponentPlay(6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,5);
    strategy.endGame("Done");
    assertFalse(strategy.startGame(true));
    bin.close();
    bout.close();
    assertEquals( this.out.toString(), out);
  }

  @Test(val=7)
  public void test4() throws IOException {
    String in = 
      "START:YES\n"+
      "START:YES\n"+
      "DECIDE:HOLD\n"+
      "START:NO\n";
    String out = 
      "START:NO\n"+
      "DICE:66666666666666665\n"+
      "END:Done\n"+
      "START:YES\n"+
      "DIE:6\n"+
      "DICE:66666666666666665\n"+
      "END:101 to 6\n"+
      "START:NO\n";
    strategy = createStrategy(in);
    assertTrue(strategy.startGame(false));
    opponentPlay(6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,5);
    strategy.endGame("Done");
    assertTrue(strategy.startGame(true));
    assertFalse(strategy.roll(6));
    opponentPlay(6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,5);
    strategy.endGame("101 to 6");
    assertFalse(strategy.startGame(false));
    bin.close();
    bout.close();
    assertEquals( this.out.toString(), out);
  }
}

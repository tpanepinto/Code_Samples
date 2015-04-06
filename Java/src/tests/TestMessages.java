// $Id: TestMessages.java 696 2012-06-04 18:09:33Z charpov $

package tests.pig;

import cs671.pig.server.Message;
import java.io.*;


import charpov.grader.*;
import static org.testng.Assert.*;

@Test(val=10)
public class TestMessages {
  
  @Test(val=2)
    public void testParse1 () throws Message.Exception {
    Message m = Message.parse("DIE:6");
    assertEquals( Message.Header.DIE,  m.header);
    assertEquals( "6",  m.content);
  }

  @Test(val=2)
    public void testParse2 () throws Message.Exception {
    Message m = Message.parse("INIT:foo\00bar");
    assertEquals( Message.Header.INIT,  m.header);
    assertEquals( "foo\nbar",  m.content);
  }

  @Test(val=2)
    public void testToString () throws Message.Exception {
    Message m = Message.makeERROR("foo\nbar");
    assertFalse(m.toString().contains("\n"));
    assertEquals( Message.Header.ERROR,  m.header);
    assertEquals( "foo\nbar",  m.content);
  }

  void testReadSend (Message m) throws Message.Exception, IOException {
    StringWriter out = new StringWriter();
    BufferedWriter bout = new BufferedWriter(out);
    m.send(bout);
    bout.close();
    String sout = out.toString();
    BufferedReader in = new BufferedReader(new StringReader(sout));
    Message n = Message.read(in);
    assertEquals( m,  n);
    assertEquals( sout.length()-1,  sout.indexOf('\n'));
  }

  @Test(val=2)
    public void testReadSend1 () throws Message.Exception, IOException {
    testReadSend(Message.START_NO);
  }

  @Test(val=2)
    public void testReadSend2 () throws Message.Exception, IOException {
    testReadSend(Message.makeEND("\nbar\n\n"));
  }

  @Test(val=1)
  public void testIOExSend () throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter("/dev/null"));
    out.close();
    try {
      Message.START_YES.send(out);
      fail();
    } catch (Message.Exception e) {
      assertTrue(e.getCause() instanceof IOException);
    }
  }

  @Test(val=1)
  public void testIOExRead () throws IOException {
    BufferedReader in = new BufferedReader(new FileReader("/dev/null"));
    in.close();
    try {
      Message.read(in);
      fail();
    } catch (Message.Exception e) {
      assertTrue(e.getCause() instanceof IOException);
    }
  }

  @Test(val=1)
    public void testParseEx () {
    try {
      Message.parse("Init:foo");
      fail();
    } catch (Message.Exception e) {
      assertTrue(e.getMessage().contains("Init"));
    }
  }

   
  @Test(val=1)
  public void testMakeEx () {
     try{
    Message.makeINIT("foo\00");
    fail("expexted IllegalArgumentException");
     } catch ( IllegalArgumentException e){
        //ok
     }
     
  }

  @Test(val=1)
  public void testEquals () throws Message.Exception {
    Object x = Message.parse("DECIDE:ROLL");
    Object y = Message.DECIDE_ROLL;
    assertEquals( x,  y);
  }

  @Test(val=1)
  public void testHashCode () throws Message.Exception {
    Object x = Message.parse("DECIDE:ROLL");
    Object y = Message.DECIDE_ROLL;
    assertEquals( 0,  x.hashCode() - y.hashCode());
  }

  @Test(val=1)
  public void testMakeDice () {
    Message m = Message.makeDICE(new int[] {1,2,3}, 2);
    assertEquals( Message.Header.DICE,  m.header);
    assertEquals( "12",  m.content);
  }

  @Test(val=1)
  public void testMakeDiceEx1 () {
     try {
    Message.makeDICE(new int[] {1,2,3}, 0);
    fail("expected IllegalArgumentException");
     }  catch (IllegalArgumentException e){
       //ok
    }
  }

  @Test(val=1)
  public void testMakeDiceEx2 () {
     try {
    Message.makeDICE(new int[] {1,2,7}, 3);
    fail("expected IllegalArgumentException");
     }  catch (IllegalArgumentException e){
       //ok
    }
  }

  @Test(val=1)
  public void testMakeDieEx () {
     try {
    Message.makeDIE(0);
    fail("expected IllegalArgumentException");
     }  catch (IllegalArgumentException e){
       //ok
    }
  }
}

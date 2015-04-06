// $Id: HW2Grading0.java 250 2014-02-27 13:59:05Z cs671a $

package tests;

import static org.testng.Assert.*;
import cs671.*;
import java.lang.reflect.Method;
/*
class HW2Tests {
  public static void main (String[] args) throws Exception {
    java.util.logging.Logger.getLogger("charpov.grader")
      .setLevel(java.util.logging.Level.WARNING);
    new charpov.grader.Tester(HW2Grading1.class,
                              HW2Grading2.class,
                              HW2Grading3.class,
                              HW2Grading4.class).run();
  }
}*/


class TestableAdapter implements Testable {
  public boolean beforeMethod (Method m) throws Exception {
    return true;
  }
  public void afterMethod (Method m) throws Exception {
  }
}

class T1 extends TestableAdapter {
  T1 () {
    throw new UnknownError("foo");
  }
}

abstract class T3 extends TestableAdapter {
}

class T4 extends TestableAdapter {
  static {
    Object x = null;
    x.toString();
  }
}

class T5 extends TestableAdapter {
  @TestMethod static void foo () {
  }
}

class T6 extends TestableAdapter {
  @TestMethod void foo (int x) {
  }
}

class T2 extends TestableAdapter {
  int i;
  static boolean aRan, dRan;
  T2 () {
    aRan = dRan = false;
  }
  @Override public boolean beforeMethod (Method m) {
    return i++ < 2;
  }
  @TestMethod(weight=-2.3) void aaa () {
    aRan = true;
  }
  @TestMethod(weight=2.3) int bbb () {
    assertTrue(i < 2);
    return i;
  }
  @TestMethod(weight=3.7) void ccc () {
    assertTrue(i < 2);
  }
  @TestMethod(weight=7) boolean ddd () {
    return (dRan = true);
  }
}

class T7 extends TestableAdapter {
  @TestMethod(weight=3.3) void foo () {
  }
  @TestMethod(weight=7.1, info="bad test") void bar () throws Exception {
    throw new java.io.IOException("I/O");
  }
  @TestMethod(weight=-1) void noTest () {
  }
}

class T8 extends TestableAdapter {
  T8 () {
    reset();
  }
  @TestMethod void bfoo () {
    bfooRan = true;
  }
  static boolean bfooRan;
  public static void reset () {
    bfooRan = false;
  }
}

class T9 implements Testable {
  public boolean beforeMethod (Method m) throws Exception {
    if (m.getName().equals("foo"))
      throw new Exception("foo not run");
    return true;
  }
  public void afterMethod(Method m) throws Exception {
    if (m.getName().equals("bar"))
      throw new Exception("bar not run");
  }
  @TestMethod void foo () {}
  @TestMethod void bar () {}
}

class Timing implements Testable {
  @TestMethod void shortTest () {
  }
  @TestMethod void longTest () throws Exception {
    Thread.sleep(1000);
  }
  public boolean beforeMethod (Method m) throws Exception {
    Thread.sleep(1500);
    return true;
  }
  public void afterMethod (Method m) throws Exception {
    Thread.sleep(1500);
  }
}

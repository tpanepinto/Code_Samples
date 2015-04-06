package tests;

import charpov.grader.Test;
import static org.testng.Assert.*;

import cs671.*;

import java.util.*;
import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

// main application
@Test(val=15)
class HW2Grading4 {

  @Test(val=3) void analyse_report () throws Exception {
    PrintStream err = System.err;
    PrintStream out = System.out;
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(w);
    System.setOut(p);
    PrintStream e = new PrintStream("/dev/null");
    System.setErr(e);
    try {
      Tester.main(new String[] {"tests.T7"});
    } finally {
      System.setErr(err);
      System.setOut(out);
      p.close();
      e.close();
    }
    int line = 1;
    for (String s : w.toString().split("\n")) {
      s = s.trim();
      if (s.isEmpty())
        continue;
      switch (line) {
      case 1:
        assertTrue(s.contains("SUCCESSFUL TESTS:"));
        break;
      case 2:
        assertTrue(s.startsWith("tests.T7.foo (3.3) in "));
        assertTrue(s.endsWith("milliseconds"));
        break;
      case 3:
        assertTrue(s.contains("FAILED TESTS:"));
        break;
      case 4:
        assertTrue(s.startsWith("tests.T7.bar: bad test (7.1) from java.io.IOException"));
        break;
      case 5:
        assertTrue(s.contains("SCORE ="));
        assertTrue(s.contains("31") || s.contains("32"));
        break;
      default:
        fail();
      }
      line++;
    }
    assertEquals(line, 6);
  }

  @Test(val=1) void no_tests_run_on_error1 () throws Exception {
    PrintStream err = System.err;
    PrintStream out = System.out;
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(w);
    System.setOut(p);
    System.setErr(p);
    tests.T8.reset();
    try {
      Tester.main(new String[] {"java.util.ArrayList", "tests.T8"});
    } finally {
      System.setErr(err);
      System.setOut(out);
      p.close();
    }
    boolean ok = false;
    for (String s : w.toString().split("\n"))
      if (s.contains("java.util.ArrayList"))
        ok = true;
    assertTrue(ok);
    assertFalse(T8.bfooRan);
  }

  @Test(val=1) void no_tests_run_on_error2 () throws Exception {
    PrintStream err = System.err;
    PrintStream out = System.out;
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    PrintStream p = new PrintStream(w);
    System.setOut(p);
    System.setErr(p);
    tests.T8.reset();
    try {
      Tester.main(new String[] {"foo.Bar", "tests.T8"});
    } finally {
      System.setErr(err);
      System.setOut(out);
      p.close();
    }
    boolean ok = false;
    for (String s : w.toString().split("\n"))
      if (s.contains("foo.Bar"))
        ok = true;
    assertTrue(ok);
    assertFalse(T8.bfooRan);
  }
}

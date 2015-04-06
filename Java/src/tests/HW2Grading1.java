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

// ERRORS and WARNINGS
@Test(val=15)
class HW2Grading1{

  String[] createAndRun (boolean silent, Tester tester) {
    ByteArrayOutputStream w = new ByteArrayOutputStream();
    PrintWriter p = new PrintWriter(w);
    try {
      if (silent)
        tester.setPrintWriter(null);
      else
        tester.setPrintWriter(p);
      tester.run();
    } finally {
      p.close();
    }
    return w.toString().split("\n");
  }

  String[] createAndRun (boolean silent, Class<? extends Testable> c) {
    return createAndRun(silent, Tester.makeTester(c));
  }

  @Test(val=1) void constructor_throws () {
    for (String s : createAndRun(false, tests.T1.class)) {
      if (s.contains("ERROR")
          && (s.contains("T1"))
          && (s.contains("foo") || s.contains("UnknownError")))
        return;
    }
    fail("no suitable error message");
  }

  @Test(val=2) void abstract_class () {
    for (String s : createAndRun(false, tests.T3.class)) {
      if (s.contains("ERROR")
          && (s.contains("T3")))
        return;
    }
    fail("no suitable error message");
  }

  @Test(val=1) void init_throws () {
    for (String s : createAndRun(false, tests.T4.class)) {
      if (s.contains("ERROR")
          && (s.contains("T4")))
        return;
    }
    fail("no suitable error message");
  }

  @Test(val=2) void static_method () {
    for (String s : createAndRun(false, tests.T5.class)) {
      if (s.contains("WARNING")
          && (s.contains("foo"))
          && (s.contains("static")))
        return;
    }
    fail("no suitable warning message");
  }

  @Test(val=2) void method_with_parameters () {
    for (String s : createAndRun(false, tests.T6.class)) {
      if (s.contains("WARNING")
          && (s.contains("foo")))
        return;
    }
    fail("no suitable warning message");
  }

  @Test(val=1) void skipped_test_from_beforeMethod () {
    for (String s : createAndRun(false, tests.T2.class)) {
      if (s.contains("WARNING")
          && (s.contains("ddd")))
        return;
    }
    fail("no suitable warning message");
  }

  @Test(val=1) void failure_beforeMethod () {
    for (String s : createAndRun(false, tests.T9.class)) {
      if (s.contains("WARNING")
          && (s.contains("foo")))
        return;
    }
    fail("no suitable warning message");
  }

  @Test(val=1) void failure_afterMethod () {
    for (String s : createAndRun(false, tests.T9.class)) {
      if (s.contains("WARNING")
          && (s.contains("bar")))
        return;
    }
    fail("no suitable warning message");
  }

  @Test(val=3) void silent () {
    Tester t1 = Tester.makeTester(tests.T1.class);
    Tester t2 = Tester.makeTester(tests.T2.class);
    Tester t3 = Tester.makeTester(tests.T3.class);
    Tester t4 = Tester.makeTester(tests.T4.class);
    Tester t5 = Tester.makeTester(tests.T5.class);
    Tester t6 = Tester.makeTester(tests.T6.class);
    Tester t7 = Tester.makeTester(tests.T7.class);
    Tester t8 = Tester.makeTester(tests.T8.class);
    Tester t = Tester.makeSuite(t1, t2, t3, t4, t5, t6, t7, t8);
    for (String s : createAndRun(true, t))
      assertTrue(s.trim().isEmpty());
  }
}

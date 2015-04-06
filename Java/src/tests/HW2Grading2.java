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

// General tests, single runner
@Test(val=40)
class HW2Grading2{

  @Test(val=1) void results_not_available_before_run () {
    Tester tester = Tester.makeTester(tests.T7.class);
    tester.setPrintWriter(null);
    try {
      tester.getResults();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // OK
    }
  }

  @Test(val=1) void cannot_rerun () {
    Tester tester = Tester.makeTester(tests.T7.class);
    tester.setPrintWriter(null);
    tester.run();
    tester.getResults();
    try {
      tester.run();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // OK
    }
  }

  @Test(val=2) void new_result_list_allocated () {
    Tester tester = Tester.makeTester(tests.T7.class);
    tester.setPrintWriter(null);
    tester.run();
    List<TestResult> results1 = tester.getResults();
    assertEquals(results1.size(), 2);
    results1.clear();
    List<TestResult> results2 = tester.getResults();
    assertNotSame(results1, results2);
    assertEquals(results2.size(), 2);
  }

  @Test(val=5) void correct_test_results () {
    Tester tester = Tester.makeTester(tests.T7.class);
    tester.setPrintWriter(null);
    tester.run();
    List<TestResult> results = tester.getResults();
    assertEquals(results.size(), 2);

    TestResult r = results.get(0);
    assertEquals(r.getInfo(), "tests.T7.bar: bad test");
    assertEquals(r.getWeight(), 7.1, 1e-5);
    assertTrue(r.getDuration() < .1);
    assertFalse(r.success());
    assertTrue(r.error() instanceof java.io.IOException);
    assertEquals(r.error().getMessage(), "I/O");

    r = results.get(1);
    assertEquals(r.getInfo(), "tests.T7.foo");
    assertEquals(r.getWeight(), 3.3, 1e-5);
    assertTrue(r.getDuration() < .1);
    assertTrue(r.success());
    assertNull(r.error());
  }

  @Test(val=2) void skipped_tests () {
    Tester tester = Tester.makeTester(tests.T2.class);
    tester.setPrintWriter(null);
    tester.run();
    List<TestResult> results = tester.getResults();
    assertEquals(results.size(), 2);
    assertFalse(T2.aRan);
    assertFalse(T2.dRan);
  }

  @Test(timeout=7500, val=3) void correct_timing () {
    Tester tester = Tester.makeTester(tests.Timing.class);
    tester.setPrintWriter(null);
    long time = System.nanoTime();
    tester.run();
    time = System.nanoTime() - time;
    double rTime = time / 1e9;
    assertTrue(rTime >= 7 && rTime < 7.1);
    List<TestResult> results = tester.getResults();
    rTime = results.get(0).getDuration();
    assertTrue(rTime >= 1 && rTime < 1.1);
    rTime = results.get(1).getDuration();
    assertTrue(rTime < 0.1);
  }
}

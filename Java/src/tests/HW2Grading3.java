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

// General tests, suite runner
@Test(val=30)
class HW2Grading3 {

  Tester t7, t8, t9, suite1, suite2;

  void BEFORE () {
    t7 = Tester.makeTester(tests.T7.class);
    t8 = Tester.makeTester(tests.T8.class);
    t9 = Tester.makeTester(tests.T9.class);
    suite1 = Tester.makeSuite(t7, t8);
    suite2 = Tester.makeSuite(suite1, t9);
    suite1.setPrintWriter(null);
    suite2.setPrintWriter(null);
  }

  @Test(val=1) void results_not_available_before_run_suite () {
    try {
      suite1.getResults();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // OK
    }
  }

  @Test(val=1) void cannot_rerun_suite () {
    suite1.run();
    suite1.getResults();
    try {
      suite1.run();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // OK
    }
  }

  @Test(val=1) void cannot_rerun_sub_suite () {
    t7.run();
    t7.getResults();
    try {
      suite1.run();
      fail("expected IllegalStateException");
    } catch (IllegalStateException e) {
      // OK
    }
  }

  @Test(val=2) void new_result_list_allocated () {
    suite1.run();
    List<TestResult> results1 = suite1.getResults();
    assertEquals(results1.size(), 3);
    results1.clear();
    List<TestResult> results2 = suite1.getResults();
    assertNotSame(results1, results2);
    assertEquals(results2.size(), 3);
  }

  void checkResult (List<TestResult> results) {
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

    r = results.get(2);
    assertEquals(r.getInfo(), "tests.T8.bfoo");
    assertEquals(r.getWeight(), 0, 1e-5);
    assertTrue(r.getDuration() < .1);
    assertTrue(r.success());
    assertNull(r.error());
  }

  @Test(val=3) void correct_result_suite () throws Exception {
    suite1.run();
    List<TestResult> results = suite1.getResults();
    checkResult(results);
  }

  @Test(val=3) void correct_result_nested_suite () throws Exception {
    suite2.run();
    List<TestResult> results = suite2.getResults();
    assertEquals(results.size(), 4);
    checkResult(results);
    TestResult r = results.get(3);
    assertEquals(r.getInfo(), "tests.T9.bar");
    assertEquals(r.getWeight(), 0, 1e-5);
    assertTrue(r.getDuration() < .1);
    assertTrue(r.success());
    assertNull(r.error());
  }
}

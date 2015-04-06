// $Id: HW2Grading0.java 234 2014-01-27 20:55:17Z cs671a $

package tests;

import static org.testng.Assert.*;;
import java.lang.reflect.Method;
import charpov.grader.Test;

import cs671.*;

import java.util.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;



class SampleTests2 {
	public static void main (String[] args) throws Exception {
		java.util.logging.Logger.getLogger("charpov.grader")
		.setLevel(java.util.logging.Level.WARNING);
		new charpov.grader.Tester(HW2GradingSample.class).run();
	}
}

class HW2GradingSample{
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

	@Test(val=2) void static_method () {
		for (String s : createAndRun(false, tests.T2.class)) {
			if (s.contains("WARNING")
					&& (s.contains("foo"))
					&& (s.contains("static")))
				return;
		}
		fail("no suitable warning message");
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



	@Test(val=1) void failure_beforeMethod () {
		for (String s : createAndRun(false, tests.T5.class)) {
			if (s.contains("WARNING")
					&& (s.contains("foo")))
				return;
		}
		fail("no suitable warning message");
	}

	@Test(val=1) void failure_afterMethod () {
		for (String s : createAndRun(false, tests.T5.class)) {
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
		Tester t = Tester.makeSuite(t1, t2, t3, t4);
		for (String s : createAndRun(true, t))
			assertTrue(s.trim().isEmpty());
	}
}


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

class T2 extends TestableAdapter {
	@TestMethod static void foo () {
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

class T5 implements Testable {
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


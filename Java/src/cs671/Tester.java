// $Id: Tester.java 232 2014-01-27 02:40:06Z cs671a $

package cs671;

//import java.lang.reflect.Constructor;
//import com.sun.org.apache.xpath.internal.operations.Mod;
//import com.sun.tools.javac.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.* ;
import java.util.*;
import java.io.PrintWriter;
import java.util.List;

/** Test runner.  This class is used to run the tests methods
 * (annotated with {@code @TestMethod}) found in classes that
 * implement {@code Testable}.
 * 
 * <p>Instances of this class exist in two flavors, whether they run a
 * single test class (see {@code makeTester}) or a suite of test
 * classes (see {@code makeSuite}).  Suites can be nested.  They
 * cannot be empty.  All the test classes (or sub-suites) in a suite
 * are processed in order.  All the test methods in a test class are
 * invoked in string order of their names.  Furthermore, they are all
 * invoked on the same instance.
 *
 * <p>Test classes have type {@code Testable}. A {@code beforeMethod}
 * is run before each test method.  If this method returns {@code
 * false} (or fails), the corresponding test is not run.  An {@code
 * afterMethod} is run after each test, whether the test was
 * successful or not.  It is not run of the test was not run.
 * Failures in the {@code afterMethod} are reported as warnings but
 * have no further effect.
 *
 *<p>In general, no-argument methods are valid test methods if they
 * are annotated, even when they are non-public or non-void.  Static
 * method and methods that require arguments are ignored.  A warning
 * is displayed if they are test-annotated.
 *
 * <p>Tests results are produced as immutable {@code TestResult}
 * instances.  No pass/fail report is generated.  I/O is used to
 * report problems with test classes (which are disctinct from failed
 * tests), including incorrectly annotated methods, classes that fail
 * to load, classes that cannot be instantiated, etc.  These are
 * reported either as ERRORS (no tests are run) or as WARNINGS (some
 * tests are run).  By default, errors and warnings go to {@code
 * System.err} but they can be redirected or even ignored (see {@code
 * setPrintWriter}).
 *
 * <p>A tester can only been run once (whether directly or as part of
 * a suite).  Any attempt to run it again will result in a {@code
 * IllegalStateException},
 *
 * <p>Instances of this class are <em>not</em> safe for multi-threading.
 *
 * @author  Michel Charpentier
 * @version 2.0, 01/26/14
 * @see TestMethod
 * @see Testable
 * @author Tim Panepinto
 */
public abstract class Tester extends Object implements Runnable {

  /** Package-private constructor. */
  Tester () {}

  /** Sets the tester output.  By default, the output is {@code
   * System.err}.  It is valid to set the output to {@code null}, in
   * which case the tester is completely silent.
   *
   * @param w the output for tester info; can be {@code null}
   */
  public abstract void setPrintWriter (PrintWriter w);

  /** Runs all the tests in the test class or suite.
   * @throws IllegalStateException if this tester has already been run.
   */
  public abstract void run ();

  /** Test results.  This method returns a list that contains a {@code
   * TestResult} object for each test that was run (in the order the
   * test method were actually run).  The returned list can be
   * modified and modifications have no affect on this tester (i.e.,
   * this method make a copy of the list of results)
   *
   * @throws IllegalStateException if the tester has not yet been run
   */
  public abstract List<TestResult> getResults ();


  /** Starts a console-based application.  Command line arguments are
   * the names of the classes to be tested.  The application produces
   * a summary output of tests that succeeded and tests that failed.
   */
  @SuppressWarnings("unchecked")
  public static void main (String[] args) {
      /**
       * ArrayList of classes
       */
      ArrayList<Class<? extends Testable>> classes= new ArrayList<>();

    for ( int i = 0; i< args.length; i++)
    {
        Class<? extends Testable> tmpClass = null;

        try{

            tmpClass = (Class<? extends Testable>) Class.forName(args[i]);

        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Class '" + args[i] + "' was not found!");
            return;
        }

        if (!Testable.class.isAssignableFrom(tmpClass))
        {
            System.out.println("Class '" + args[i] + "' is not testable");
            return;
        }


        if (tmpClass != null)
            classes.add(tmpClass);
    }
    if (classes.size() > 0)
    {
        Tester test = null;
        List<TestResult> results = null;
        if (classes.size() == 1)
        {
            test = makeTester(classes.get(0));
            test.run();
            results = test.getResults();



        }
        else
        {
            ArrayList<Tester> testSuite = new ArrayList<>();

            for ( int j = 0; j < classes.size(); j++)
            {
                Tester tmpTest = null;
                tmpTest = makeTester(classes.get(j));
                testSuite.add(tmpTest);
            }
            test = makeSuite(testSuite);
            test.run();
            results = test.getResults();
        }


        String success = "";
        String fail = "";
        int failedTests = 0;
        int successTests = 0;
        double score = 0.0;
        double pointsTotal = 0.0;
        double pointsPossible = 0.0;

        System.out.print("SUCCESSFUL TESTS:\n");
        for( int k = 0; k < results.size(); k++)
        {
            TestResult tmpRes = results.get(k);
            if( results.get(k).success())
            {
                System.out.print("  " + tmpRes.getInfo() + " (" + tmpRes.getWeight() + ") in " + (tmpRes.getDuration() * 1000) + " milliseconds\n");
                successTests++;
                pointsTotal += tmpRes.getWeight();
                pointsPossible += tmpRes.getWeight();

            }
        }
        System.out.print("FAILED TESTS:\n");
        for( int k = 0; k < results.size(); k++)
        {
            TestResult tmpRes = results.get(k);
            if( !results.get(k).success())
            {
                System.out.print("  " + tmpRes.getInfo() + " (" + tmpRes.getWeight() + ") from " + tmpRes.error() + " \n");
                failedTests++;
                pointsPossible += tmpRes.getWeight();
            }
        }

        score = (pointsTotal / pointsPossible)*100;

        System.out.print("SCORE = " + score + "%\n");

    }
    else
        System.out.println("No classes to run");
  }

  /** Creates a tester for a single test class.  This method only
   * creates the tester.  It makes no attempt to load the class,
   * instantiate it, look for test methods, etc.
   */
  public static Tester makeTester (Class<? extends Testable> tests) {
     TestMake testing = new TestMake( tests );

     return testing;
  }

  /** Creates a tester for a suite of testers. */
  public static Tester makeSuite (Tester... testers) {
     SuiteMake testSuite = new SuiteMake( testers );

    return testSuite;
  }

  /** Creates a tester for a suite of testers. */
  public static Tester makeSuite (List<? extends Tester> testers) {
      SuiteMake testSuite = new SuiteMake( testers );

      return testSuite;
  }

    /** Class that makes the Tester Object for a single {@code Tester}
     *
     *
     */
    private static class TestMake extends Tester {
        /**
         * ArrayList of results for the tester
         */
        private ArrayList<TestResult> results = new ArrayList<>();
        /**
         * PrintWriter that prints out all of the prints from the tests
         */
        private PrintWriter printer;
        /**
         * Class that is going to be tested with this tester
         */
        private Class<? extends Testable> tests;
        /**
         * Is true if the test has already been run, is false if it has not.
         */
        public boolean hasRun;

        /** Constructs a TestMake object from the {@code Class} that is passed in.
         * @param testsIn Class<? extends Testable>
         */
        TestMake(Class<? extends Testable> testsIn)
        {
            tests = testsIn;

            printer = new PrintWriter(System.err);
            results = new ArrayList<>();


        }



        @Override
        public void setPrintWriter(PrintWriter w) {



            if(w!=null)
                printer = w;
        }

        @Override
        public void run() {
            Constructor<?> mainConstructor = null;
            Method [] methods;
            Annotation[] annotations;

            Testable testRun = null;

            boolean suitable = true;

            double timer = 0.0;

            if ( hasRun )
                throw new IllegalStateException("This has run before");
            else
            {
                if ( Testable.class.isAssignableFrom(tests))
                {


                    try{
                        mainConstructor = tests.getDeclaredConstructor();
                        mainConstructor.setAccessible(true);
                    }
                    catch (NoSuchMethodException e){
                        printer.println("ERROR: "+ tests.getName() + " does not have a suitable constructor!");
                        suitable = false;
                        return;

                    }

                    if (suitable)
                    {
                        try{
                            testRun = (Testable) mainConstructor.newInstance();
                        }
                        catch (NoClassDefFoundError | InstantiationException | IllegalAccessException | InvocationTargetException | ExceptionInInitializerError e){
                            printer.println("ERROR: " + tests.getName() + "was not initialized with exception" + e.getCause());
                            return;
                        }
                        methods = tests.getDeclaredMethods();

                        if ( methods.length == 0)
                        {
                            printer.println("ERROR: " + tests.getName() + " does not have any methods!");
                            return;
                        }

                        Arrays.sort(methods, new SortIt());

                        for ( int j = 0; j < methods.length; j++)
                        {
                            Annotation annotation;
                            methods[j].setAccessible(true);
                           // annotations = methods[j].getAnnotations();
                            annotation = methods[j].getAnnotation(TestMethod.class);
                            TestMethod testAnn = (TestMethod) annotation;

                            if (testAnn != null)
                            {
                                if(methods[j].getParameterTypes().length > 0)
                                {
                                    printer.println("WARNING: " + methods[j].getName() + " has parameters; ignored");
                                    continue;
                                }
                                if(Modifier.isStatic(methods[j].getModifiers()))
                                {
                                    printer.println("WARNING: " + methods[j].getName() + " is static; ignored");
                                    continue;
                                }
                                if(Modifier.isAbstract(methods[j].getModifiers()))
                                {
                                    printer.println("WARNING: " + methods[j].getName() + " is abstract; ignored");
                                    continue;
                                }
                                if (testAnn.weight() < 0)
                                    continue;
                                try{
                                    if ( !testRun.beforeMethod(methods[j]))
                                    {
                                        printer.println("WARNING: beforeMethod( " + methods[j].getName() + " ) failed to initialize; ignored");
                                        continue;
                                    }
                                }
                                catch ( Exception e)
                                {
                                    printer.println("WARNING: beforeMethod( " + methods[j].getName() + " ) failed to initialize; ignored");
                                    continue;
                                }
                                Results currResult = new Results();
                                currResult.methodName = methods[j].getName();
                                currResult.className = tests.getName();
                                currResult.info = testAnn.info();
                                currResult.weight = testAnn.weight();

                                timer = System.currentTimeMillis();

                                try
                                {
                                    methods[j].invoke(testRun);
                                    currResult.duration = (System.currentTimeMillis() - timer) / 1000;
                                    currResult.success = true;
                                }
                                catch (IllegalAccessException | InvocationTargetException e)
                                {
                                    currResult.error = e.getCause();
                                    currResult.duration = (System.currentTimeMillis() - timer) / 1000;
                                    currResult.success = false;
                                }

                                try
                                {
                                    testRun.afterMethod(methods[j]);
                                }
                                catch(Exception e)
                                {
                                    printer.println("WARNING: afterMethod ( " + methods[j].getName() + " ) did not run: " + e.getCause());
                                    //currResult.success = false;
                                }
                                results.add(currResult);


                            }



                        }
                    }


                }
                else{
                    printer.println("ERROR: class ' " + tests.getName() + " ' is not testable");
                    return;
                }
            }
            hasRun = true;
        }

        @Override
        public List<TestResult> getResults() {
            if( hasRun)
            {

                ArrayList<TestResult> tmpResults = new ArrayList<>();
                tmpResults.addAll(results);
                return tmpResults;

            }

            else
                throw new IllegalStateException();
        }



    }
    /** Makes a suite of {@code Tester} and runs each one of the tests. Get results can
     * be called on the suite to return all of the {@code Results} from each test
     * that was run in the suite.
     *
     */
    private static class SuiteMake extends Tester{
        /**
         * ArrayList of results for the tester
         */
        private ArrayList<TestResult> results = new ArrayList<>();
        /**
         * Boolean that tells if the suite has run already
         */
        private boolean suiteRun;
        /**
         * PrintWriter that prints out all of the prints from the tests
         */
        private PrintWriter printer;
        /**
         * ArrayList of testers that will be tested in the order that they are in
         */
        private ArrayList<Tester> tests = new ArrayList<>();

        /** Constructs a SuiteMake object from the collection of {@code Tester} that is passed in.
         * @param testsIn Array of testers
         */
        SuiteMake(Tester... testsIn)
        {
            for ( int i = 0; i < testsIn.length; i++)
                tests.add(testsIn[i]);
            printer = new PrintWriter(System.err);
            suiteRun= false;

        }
        /** Constructs a SuiteMake object from the collection of {@code Tester} that is passed in.
         * @param testsIn List of testers
         */
        SuiteMake( List<? extends Tester> testsIn)
        {
            for ( int i = 0; i < testsIn.size() - 1; i++)
                tests.add(testsIn.get(i));
            printer = new PrintWriter(System.err);
            suiteRun = false;

        }



        @Override
        public void setPrintWriter(PrintWriter w) {
            if (w != null)
                printer = w;

        }

        @Override
        public void run() {
            if (suiteRun)
                throw new IllegalStateException();
            else{
                for ( int i = 0; i < tests.size(); i++)
                {
                    //tests.get(i).setPrintWriter(printer);
                    tests.get(i).run();

                    try{
                        List<TestResult> tmpRes = tests.get(i).getResults();
                        results.addAll(tmpRes);
                    }
                    catch( IllegalStateException e)
                    {}


                }
                suiteRun = true;
            }


        }

        @Override
        public List<TestResult> getResults() {
            if ( !suiteRun )
                throw new IllegalStateException("Suite has not run");
            else
            {
                ArrayList<TestResult> tmpResults = new ArrayList<>();
                tmpResults.addAll(results);
                return tmpResults;
            }


        }
    }
    /** Class that creates a result object that stores all of the information about the result of the test that is run.
     * A result object is per method that is run in the test.
     */
    private static class Results implements TestResult{

        /**
         * Info for the result that contains the full method name along with the annotation info.
         */
        private String info = null;
        /**
         * Weight of the method being tested, used to score the final outcome.
         */
        private double weight = 0.0;
        /**
         * Duration of the test on the method
         */
        private double duration = 0.0;
        /**
         * Whether the test on the method was successful or not.
         */
        private boolean success = false;
        /**
         * If the test is not successful this is the error.
         */
        private Throwable error = null;
        /**
         * Name of the method from method.getName()
         */
        private String methodName = null;
        /**
         * Name of the class that this method is in.
         */
        private String className = null;

        @Override
        public String getInfo() {
            if ( info == null || info.equals(""))
                return (className + "." + methodName);
            else
                return (className + "." +methodName + ": " + info);
        }

        @Override
        public double getWeight() {
            return weight;
        }

        @Override
        public double getDuration() {
            return duration;
        }

        @Override
        public boolean success() {
            return success;
        }

        @Override
        public Throwable error() {
            return error;
        }
    }
    /**
     * Compares the methods and sorts the methods alphabetically by name
     */
    private static class SortIt implements Comparator<Method>{


        @Override
        public int compare(Method o1, Method o2) {
            return o1.getName().compareTo(o2.getName());
        }



    }
}

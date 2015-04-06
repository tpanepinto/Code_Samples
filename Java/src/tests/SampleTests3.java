package tests;

import cs671.*;

import charpov.grader.*;
import static org.testng.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

class SampleTests3 {

  public static void main (String[] args) throws Exception {
    java.util.logging.Logger.getLogger("charpov.grader")
      .setLevel(java.util.logging.Level.WARNING);
    new Tester(TestImmediateFuture.class,
               TestFuture.class,
               TestCallback.class,
               TestContinuation.class).run();
  }
}

class TestThread extends Thread {
  public TestThread (Runnable behavior) {
    super(behavior);
  }
}

class Factory implements FactoryExecutor.ThreadFactory {

  private final List<TestThread> threads = new java.util.ArrayList<>();
  private final AtomicInteger id = new AtomicInteger();

  public synchronized TestThread getThread (Runnable r) {
    TestThread t = new TestThread(r);
    t.setName("TestThread-"+id.incrementAndGet());
    threads.add(t);
    return t;
  }

  public synchronized int getThreadCount () {
    return threads.size();
  }

  public synchronized TestThread getThread (int i) {
    return threads.get(i);
  }
}

class Run {

  private volatile Thread runner;

  public Thread getRunner () {
    return runner;
  }

  public void run () {
    runner = Thread.currentThread();
  }
}
 
class RunnableTask extends Run implements Runnable {

  private final long[] durations;

  public RunnableTask (long... times) {
    durations = times;
  }

  public void run () {
    super.run();
    try {
      for (long duration : durations)
        Thread.sleep(duration);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}

class Task<T> extends Run implements Callable<T> {

  private final T result;
  private final long[] durations;

  public Task (T value, long... times) {
    result = value;
    durations = times;
  }

  public T call () throws InterruptedException {
    run();
    for (long duration : durations)
      Thread.sleep(duration);
    return result;
  }
}

class CB extends Run implements Callback<Number> {
  private final Number expected;

  public CB (Number n) {
    expected = n;
  }

  public CB () {
    expected = -1;
  }
  
  public void call (Number n) {
    run();
    if (expected == null || expected.intValue() >= 0)
      assertEquals(n, expected);
  }

  public void failure (Throwable t) {
    fail("should not be called");
  }
}

class CBE extends Run implements Callback<Object> {
  private volatile Throwable failure;

  public Throwable getFailure () {
    return failure;
  }
  
  public void call (Object o) {
    fail("should not be called");
  }

  public void failure (Throwable t) {
    run();
    assertNotNull(t);
    failure = t;
  }
}

class ToString extends Run implements Continue<Number,String> {

  private final long duration;

  public ToString () {
    this(0);
  }

  public ToString (long time) {
    duration = time;
  }

  public String call (Number n) throws InterruptedException {
    run();
    Thread.sleep(duration);
    return "[" + n + "]";
  }
}

// Actual tests start here

class TestImmediateFuture {

  Factory  factory;
  Executor exec;

  void BEFORE () {
    factory = new Factory();
    exec = new FactoryExecutor(factory);
  }

  @Test void test1 () throws Exception {
    Future<String> future = new CompletedFuture<>("foo");
    assertTrue(future.isDone());
    assertEquals(future.get(), "foo");
    assertNull(future.getFailure());
  }

  @Test void test2 () throws Exception {
    Future<Integer> future = new CompletedFuture<>(42);
    CB cb = new CB(42);
    future.whenComplete(cb);
    assertTrue(future.isDone());
    assertSame(cb.getRunner(), Thread.currentThread());
  }

  @Test void test6 () throws Exception {
    Exception e = new Exception();
    Future<String> future = new FailedFuture<>(e);
    RunnableTask cb = new RunnableTask();
    future.whenComplete(cb);
    assertNull(cb.getRunner());
  }

  @Test void test7 () throws Exception {
    Future<Integer> future1 = new CompletedFuture<>(42);
    ToString cont = new ToString();
    Future<String> future2 = future1.whenComplete(cont);
    assertTrue(future2.isDone());
    assertEquals(future2.get(), "[42]");
    assertNull(future2.getFailure());
    assertSame(cont.getRunner(), Thread.currentThread());
  }

  @Test void test8 () throws Exception {
    Exception e = new Exception();
    ToString cont = new ToString();
    Future<Integer> future1 = new FailedFuture<>(e);
    Future<String> future2 = future1.whenComplete(cont);
    assertTrue(future2.isDone());
    assertNull(future2.get());
    Throwable t = future2.getFailure();
    assertTrue(t instanceof NotExecutedException);
    assertSame(t.getCause(), e);
    assertNull(cont.getRunner());
  }

  @Test void test9 () throws Exception {
    final Future<String> future = new CompletedFuture<>("foo");
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            for (int j=0; j<10000; j++) {
              assertTrue(future.isDone());
              assertEquals(future.get(), "foo");
              assertNull(future.getFailure());
            }
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }
}

class TestFuture {

  Factory  factory;
  Executor exec;

  void BEFORE () {
    factory = new Factory();
    exec = new FactoryExecutor(factory);
  }

  @Test void test1 () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    Future<Integer> future = exec.execute(task);
    Thread.sleep(500);
    assertFalse(future.isDone());
    assertEquals(future.get().intValue(), 42);
    assertNull(future.getFailure());
    assertTrue(future.isDone());
    TestThread t = (TestThread)task.getRunner();
    assertEquals(factory.getThreadCount(), 1);
    assertSame(factory.getThread(0), t);
  }

  @Test void test1a () throws Exception {
    List<Task<Integer>> tasks = new java.util.ArrayList<>(100);
    List<Future<Integer>> futures = new java.util.ArrayList<>(100);
    for (int i=0; i<100; i++) {
      Task<Integer> task = new Task<>(42, 800);
      tasks.add(task);
      futures.add(exec.execute(task));
    }
    Thread.sleep(500);
    for (Future<Integer> future : futures)
      assertFalse(future.isDone());
    for (int i=0; i<100; i++) {
      Future<Integer> future = futures.get(i);
      Task<Integer> task = tasks.get(i);
      assertEquals(future.get().intValue(), 42);
      assertNull(future.getFailure());
      assertTrue(future.isDone());
      TestThread t = (TestThread)task.getRunner();
      assertSame(factory.getThread(i), t);
    }
    assertEquals(factory.getThreadCount(), 100);
  }

  @Test void test2 () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    Future<Integer> future = exec.execute(task);
    Thread t;
    while ((t = task.getRunner()) == null); // BUSY WAIT
    t.interrupt();
    Thread.sleep(500);
    assertTrue(future.isDone());
    assertNull(future.get());
    assertTrue(future.getFailure() instanceof InterruptedException);
    assertEquals(factory.getThreadCount(), 1);
    assertSame(factory.getThread(0), t);
  }

}

class TestCallback {

  Factory  factory;
  Executor exec;

  void BEFORE () {
    factory = new Factory();
    exec = new FactoryExecutor(factory);
  }

  @Test void test1 () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    Future<Integer> future = exec.execute(task);
    CB c = new CB(42);
    future.whenComplete(c);
    future.get();
    TestThread t = (TestThread)c.getRunner();
    assertEquals(factory.getThreadCount(), 1);
    assertSame(t, factory.getThread(0));
  }

  @Test void test1a () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    final Future<Integer> future = exec.execute(task);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            CB c = new CB(42);
            future.whenComplete(c);
            future.get();
            TestThread t = (TestThread)c.getRunner();
            assertEquals(factory.getThreadCount(), 1);
            assertSame(t, factory.getThread(0));
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test void test7 () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    Future<Integer> future = exec.execute(task);
    CB cb1 = new CB();
    future.whenComplete(cb1);
    future.get();
    CB cb2 = new CB();
    future.whenComplete(cb2);
    assertSame(cb1.getRunner(), factory.getThread(0));
    assertSame(cb2.getRunner(), Thread.currentThread());
  }

}

class TestContinuation {

  Factory  factory;
  Executor exec;

  void BEFORE () {
    factory = new Factory();
    exec = new FactoryExecutor(factory);
  }

  @Test void test1 () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    ToString cont = new ToString();
    Future<Integer> future1 = exec.execute(task);
    Future<String> future2 = future1.whenComplete(cont);
    Thread.sleep(500);
    assertNull(cont.getRunner());
    assertEquals(future2.get(), "[42]");
    assertTrue(future2.isDone());
    assertNull(future2.getFailure());
    assertSame(cont.getRunner(), factory.getThread(0));
    while (!future1.isDone()); // BUSY WAIT
  }

  @Test void test3 () throws Exception {
    Task<Integer> task = new Task<>(42, 800, -1);
    ToString cont = new ToString();
    Future<Integer> future1 = exec.execute(task);
    Future<String> future2 = future1.whenComplete(cont);
    assertNull(future2.get());
    assertTrue(future2.isDone());
    Throwable t1 = future1.getFailure();
    Throwable t2 = future2.getFailure();
    assertTrue(t2 instanceof NotExecutedException);
    assertSame(t2.getCause(), t1);
    assertNull(cont.getRunner());
  }

  @Test void test5 () throws Exception {
    Task<Integer> task = new Task<>(42, 400);
    final Future<Integer> future1 = exec.execute(task);
    ToString cont = new ToString(400);
    Future<String> future2 = future1.whenComplete(cont);
    Thread t = new Thread() {
        public void run () {
          try {
            future1.get();
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      };
    t.start();
    Thread.sleep(600);
    assertFalse(future1.isDone());
    assertTrue(t.isAlive());
  }
}

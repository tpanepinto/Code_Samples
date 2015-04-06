package tests;

import cs671.*;

import charpov.grader.*;
import static org.testng.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

class HW3Grading0 {

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
  private final long duration;

  public CB (Number n, long time) {
    expected = n;
    duration = time;
  }

  public CB (Number n) {
    this(n, 0);
  }

  public CB () {
    this(-1, 0);
  }
  
  public void call (Number n) {
    try {
      run();
      if (expected == null || expected.intValue() >= 0)
        assertEquals(n, expected);
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      fail("interrupted");
    }
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
@Test (val=20)
class TestImmediateFuture {

  void BEFORE () {
  }

  @Test (val=1) void test1 () throws Exception {
    Future<String> future = new CompletedFuture<>("foo");
    assertTrue(future.isDone());
    assertEquals(future.get(), "foo");
    assertNull(future.getFailure());
  }

  @Test (val=1) void test2 () throws Exception {
    Future<Integer> future = new CompletedFuture<>(42);
    CB cb = new CB(42);
    future.whenComplete(cb);
    assertTrue(future.isDone());
    assertSame(cb.getRunner(), Thread.currentThread());
  }

  @Test (val=1)  void test3 () throws Exception {
    Exception e = new Exception();
    Future<String> future = new FailedFuture<>(e);
    assertTrue(future.isDone());
    assertSame(future.getFailure(), e);
    assertNull(future.get());
  }

  @Test (val=1) void test4 () throws Exception {
    Exception e = new Exception();
    Future<String> future = new FailedFuture<>(e);
    CBE cb = new CBE();
    future.whenComplete(cb);
    assertTrue(future.isDone());
    assertSame(cb.getRunner(), Thread.currentThread());
    assertSame(cb.getFailure(), e);
  }

  @Test (val=1) void test5 () throws Exception {
    Future<String> future = new CompletedFuture<>("foo");
    RunnableTask cb = new RunnableTask();
    future.whenComplete(cb);
    assertTrue(future.isDone());
    assertSame(cb.getRunner(), Thread.currentThread());
  }

  @Test (val=1) void test6 () throws Exception {
    Exception e = new Exception();
    Future<String> future = new FailedFuture<>(e);
    RunnableTask cb = new RunnableTask();
    future.whenComplete(cb);
    assertNull(cb.getRunner());
  }

  @Test (val=1) void test7 () throws Exception {
    Future<Integer> future1 = new CompletedFuture<>(42);
    ToString cont = new ToString();
    Future<String> future2 = future1.whenComplete(cont);
    assertTrue(future2.isDone());
    assertEquals(future2.get(), "[42]");
    assertNull(future2.getFailure());
    assertSame(cont.getRunner(), Thread.currentThread());
  }

  @Test (val=1) void test8 () throws Exception {
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

  @Test(timeout=5000, val=1) void test9 () throws Exception {
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

  @Test(timeout=5000, val=1) void test10 () throws Exception {
    final Future<Integer> future = new CompletedFuture<>(42);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          for (int j=0; j<10000; j++) {
            CB cb = new CB(42);
            future.whenComplete(cb);
            assertTrue(future.isDone());
            assertSame(cb.getRunner(), this);
          }
        }
      }.start();
    }
  }

  @Test(timeout=5000, val=1) void test11 () throws Exception {
    final Exception e = new Exception();
    final Future<String> future = new FailedFuture<>(e);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            for (int j=0; j<10000; j++) {
              assertTrue(future.isDone());
              assertSame(future.getFailure(), e);
              assertNull(future.get());
            }
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test(timeout=5000, val=1) void test12 () throws Exception {
    final Exception e = new Exception();
    final Future<String> future = new FailedFuture<>(e);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          for (int j=0; j<10000; j++) {
            CBE cb = new CBE();
            future.whenComplete(cb);
            assertTrue(future.isDone());
            assertSame(cb.getRunner(), this);
            assertSame(cb.getFailure(), e);
          }
        }
      }.start();
    }
  }

  @Test(timeout=5000, val=1) void test13 () throws Exception {
    final Future<String> future = new CompletedFuture<>("foo");
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          for (int j=0; j<10000; j++) {
            RunnableTask cb = new RunnableTask();
            future.whenComplete(cb);
            assertTrue(future.isDone());
            assertSame(cb.getRunner(), this);
          }
        }
      }.start();
    }
  }

  @Test(timeout=5000, val=1) void test14 () throws Exception {
    final Exception e = new Exception();
    final Future<String> future = new FailedFuture<>(e);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          for (int j=0; j<10000; j++) {
            RunnableTask cb = new RunnableTask();
            future.whenComplete(cb);
            assertNull(cb.getRunner());
          }
        }
      }.start();
    }
  }

  @Test(timeout=5000, val=1) void test15 () throws Exception {
    final Future<Integer> future1 = new CompletedFuture<>(42);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            for (int j=0; j<10000; j++) {
              ToString cont = new ToString();
              Future<String> future2 = future1.whenComplete(cont);
              assertTrue(future2.isDone());
              assertEquals(future2.get(), "[42]");
              assertNull(future2.getFailure());
              assertSame(cont.getRunner(), this);
            }
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test(timeout=5000, val=1) void test16 () throws Exception {
    final Exception e = new Exception();
    final Future<Integer> future1 = new FailedFuture<>(e);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            for (int j=0; j<10000; j++) {
              ToString cont = new ToString();
              Future<String> future2 = future1.whenComplete(cont);
              assertTrue(future2.isDone());
              assertNull(future2.get());
              Throwable t = future2.getFailure();
              assertTrue(t instanceof NotExecutedException);
              assertSame(t.getCause(), e);
              assertNull(cont.getRunner());
            }
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }
}

@Test (val=30)
class TestFuture {

  Factory  factory;
  Executor exec;

  void BEFORE () {
    factory = new Factory();
    exec = new FactoryExecutor(factory);
  }

  @Test (val=1)  void test1 () throws Exception {
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

  @Test(timeout=5000, val=1) void test1a () throws Exception {
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

  @Test (val=1)  void test2 () throws Exception {
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

  @Test(timeout=5000, val=1) void test2a () throws Exception {
    List<Task<Integer>> tasks = new java.util.ArrayList<>(100);
    List<Future<Integer>> futures = new java.util.ArrayList<>(100);
    for (int i=0; i<100; i++) {
      Task<Integer> task = new Task<>(42, 800);
      tasks.add(task);
      futures.add(exec.execute(task));
    }
    for (Task<Integer> task : tasks) {
      Thread t;
      while ((t = task.getRunner()) == null); // BUSY WAIT
      t.interrupt();
    }
    Thread.sleep(500);
    for (int i=0; i<100; i++) {
      Future<Integer> future = futures.get(i);
      Task<Integer> task = tasks.get(i);
      assertTrue(future.isDone());
      assertNull(future.get());
      assertTrue(future.getFailure() instanceof InterruptedException);
      assertSame(tasks.get(i).getRunner(), factory.getThread(i));
    }
    assertEquals(factory.getThreadCount(), 100);
  }

  @Test  (val=1) void test3 () throws Exception {
    RunnableTask task = new RunnableTask(800);
    Future<Void> future = exec.execute((Runnable)task);
    Thread.sleep(500);
    assertFalse(future.isDone());
    assertNull(future.get());
    assertNull(future.getFailure());
    assertTrue(future.isDone());
    TestThread t = (TestThread)task.getRunner();
    assertEquals(factory.getThreadCount(), 1);
    assertSame(factory.getThread(0), t);
  }

  @Test(timeout=5000, val=1) void test3a () throws Exception {
    List<RunnableTask> tasks = new java.util.ArrayList<>(100);
    List<Future<Void>> futures = new java.util.ArrayList<>(100);
    for (int i=0; i<100; i++) {
      RunnableTask task = new RunnableTask(800);
      Future<Void> future = exec.execute(task);
      tasks.add(task);
      futures.add(future);
    }
    Thread.sleep(500);
    for (Future<Void> future : futures)
      assertFalse(future.isDone());
    for (int i=0; i<100; i++) {
      Future<Void> future = futures.get(i);
      RunnableTask task = tasks.get(i);
      assertNull(future.get());
      assertNull(future.getFailure());
      assertTrue(future.isDone());
      TestThread t = (TestThread)task.getRunner();
      assertSame(factory.getThread(i), t);
    }
    assertEquals(factory.getThreadCount(), 100);
  }

  @Test  (val=1) void test4 () throws Exception {
    RunnableTask task = new RunnableTask(-1);
    Future<Void> future = exec.execute((Runnable)task);
    while (!future.isDone()); // BUSY WAIT
    assertNull(future.get());
    assertTrue(future.getFailure() instanceof IllegalArgumentException);
    TestThread t = (TestThread)task.getRunner();
    assertEquals(factory.getThreadCount(), 1);
    assertSame(factory.getThread(0), t);
  }

  @Test(timeout=5000, val=1) void test4a () throws Exception {
    List<RunnableTask> tasks = new java.util.ArrayList<>(100);
    List<Future<Void>> futures = new java.util.ArrayList<>(100);
    for (int i=0; i<100; i++) {
      RunnableTask task = new RunnableTask(-1);
      Future<Void> future = exec.execute((Runnable)task);
      tasks.add(task);
      futures.add(future);
    }
    for (int i=0; i<100; i++) {
      Future<Void> future = futures.get(i);
      RunnableTask task = tasks.get(i);
      while (!future.isDone()); // BUSY WAIT
      assertNull(future.get());
      assertTrue(future.getFailure() instanceof IllegalArgumentException);
      TestThread t = (TestThread)task.getRunner();
      assertSame(factory.getThread(i), t);
    }
    assertEquals(factory.getThreadCount(), 100);
  }
}

@Test (val=30)
class TestCallback {

  Factory  factory;
  Executor exec;

  void BEFORE () {
    factory = new Factory();
    exec = new FactoryExecutor(factory);
  }

  @Test  (val=1) void test1 () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    Future<Integer> future = exec.execute(task);
    CB c = new CB(42);
    future.whenComplete(c);
    future.get();
    TestThread t = (TestThread)c.getRunner();
    assertEquals(factory.getThreadCount(), 1);
    assertSame(t, factory.getThread(0));
  }

  @Test(timeout=5000, val=1) void test1a () throws Exception {
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

  @Test  (val=1) void test2 () throws Exception {
    Task<Integer> task = new Task<>(42);
    Future<Integer> future = exec.execute(task);
    CB c = new CB(42);
    future.get();
    future.whenComplete(c);
    assertSame(c.getRunner(), Thread.currentThread());
    assertEquals(factory.getThreadCount(), 1);
  }

  @Test(timeout=5000, val=1) void test2a () throws Exception {
    Task<Integer> task = new Task<>(42, 500);
    final Future<Integer> future = exec.execute(task);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            CB c = new CB(42);
            future.get();
            future.whenComplete(c);
            assertSame(c.getRunner(), this);
            assertEquals(factory.getThreadCount(), 1);
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test  (val=1) void test3 () throws Exception {
    Task<Integer> task = new Task<>(42);
    CB c = new CB(42);
    exec.execute(task, c);
    Thread t;
    while ((t = c.getRunner()) == null); // BUSY WAIT
    assertEquals(factory.getThreadCount(), 1);
    assertSame(t, factory.getThread(0));
  }

  @Test(timeout=5000, val=1) void test3a () throws Exception {
    Thread[] threads = new Thread[100];
    for (int i=0; i<100; i++) {
      (threads[i] = new Thread() {
          public void run () {
            Task<Integer> task = new Task<>(42);
            CB c = new CB(42);
            exec.execute(task, c);
            Thread t;
            while ((t = c.getRunner()) == null); // BUSY WAIT
            assertNotSame(t, this);
          }
        }).start();
    }
    for (Thread thread : threads)
      thread.join();
    assertEquals(factory.getThreadCount(), 100);
  }


  @Test  (val=1) void test4 () throws Exception {
    Task<Integer> task = new Task<>(42, 800, -1);
    Future<Integer> future = exec.execute(task);
    CBE c = new CBE();
    future.whenComplete(c);
    future.get();
    TestThread t = (TestThread)c.getRunner();
    assertEquals(factory.getThreadCount(), 1);
    assertSame(t, factory.getThread(0));
    assertSame(c.getFailure(), future.getFailure());
  }

  @Test(timeout=5000, val=1) void test4a () throws Exception {
    Task<Integer> task = new Task<>(42, 800, -1);
    final Future<Integer> future = exec.execute(task);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            CBE c = new CBE();
            future.whenComplete(c);
            future.get();
            TestThread t = (TestThread)c.getRunner();
            assertEquals(factory.getThreadCount(), 1);
            assertSame(t, factory.getThread(0));
            assertSame(c.getFailure(), future.getFailure());
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test  (val=1) void test5 () throws Exception {
    Runnable task = new RunnableTask();
    CB c = new CB(null);
    exec.execute(task, c);
    Thread t;
    while ((t = c.getRunner()) == null); // BUSY WAIT
    assertEquals(factory.getThreadCount(), 1);
    assertSame(factory.getThread(0), t);
  }

  @Test(timeout=5000, val=1) void test5a () throws Exception {
    Thread[] threads = new Thread[100];
    for (int i=0; i<100; i++) {
      (threads[i] = new Thread() {
          public void run () {
            Runnable task = new RunnableTask();
            CB c = new CB(null);
            exec.execute(task, c);
            Thread t;
            while ((t = c.getRunner()) == null); // BUSY WAIT
            assertNotSame(t, this);
          }
        }).start();
    }
    for (Thread thread : threads)
      thread.join();
    assertEquals(factory.getThreadCount(), 100);
  }

  @Test  (val=1) void test6 () throws Exception {
    Runnable task = new RunnableTask(800, -1);
    Future<Void> future = exec.execute(task);
    CBE c = new CBE();
    future.whenComplete(c);
    future.get();
    TestThread t = (TestThread)c.getRunner();
    assertEquals(factory.getThreadCount(), 1);
    assertSame(factory.getThread(0), t);
    assertSame(c.getFailure(), future.getFailure());
  }

  @Test(timeout=5000, val=1) void test6a () throws Exception {
    Runnable task = new RunnableTask(800, -1);
    final Future<Void> future = exec.execute(task);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            CBE c = new CBE();
            future.whenComplete(c);
            future.get();
            TestThread t = (TestThread)c.getRunner();
            assertEquals(factory.getThreadCount(), 1);
            assertSame(factory.getThread(0), t);
            assertSame(c.getFailure(), future.getFailure());
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test  (val=1) void test7 () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    Future<Integer> future = exec.execute(task);
    CB cb1 = new CB();
    future.whenComplete(cb1);
    future.get();
    CB cb2 = new CB();
    future.whenComplete(cb2);
    assertSame(cb1.getRunner(), factory.getThread(0));
    assertSame(cb2.getRunner(), Thread.currentThread());
    assertEquals(factory.getThreadCount(), 1);
  }

  @Test(timeout=5000, val=1) void test7a () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    final Future<Integer> future = exec.execute(task);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            CB cb1 = new CB();
            future.whenComplete(cb1);
            future.get();
            CB cb2 = new CB();
            future.whenComplete(cb2);
            assertSame(cb1.getRunner(), factory.getThread(0));
            assertSame(cb2.getRunner(), this);
            assertEquals(factory.getThreadCount(), 1);
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test  (val=1) void test8 () throws Exception {
    Task<Integer> task = new Task<>(42, 400);
    CB cb = new CB(42, 400);
    final Future<Integer> future = exec.execute(task);
    future.whenComplete(cb);
    Thread t = new Thread() {
        public void run () {
          try {
            future.get();
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      };
    t.start();
    Thread.sleep(600);
    assertFalse(future.isDone());
    assertTrue(t.isAlive());
    assertEquals(factory.getThreadCount(), 1);
  }

  @Test  (val=1) void test9 () throws Exception {
    Task<Integer> task = new Task<>(42, 400);
    CB cb1 = new CB(42, 400);
    CB cb2 = new CB(42);
    Future<Integer> future = exec.execute(task);
    future.whenComplete(cb1);
    Thread.sleep(500);
    future.whenComplete(cb2);
    future.get();
    assertSame(cb1.getRunner(), factory.getThread(0));
    while (cb2.getRunner() == null); // BUSY WAIT
    assertEquals(factory.getThreadCount(), 1);
  }

  @Test(timeout=10000, val=1) void test10 () throws Exception {
    Task<Integer> task = new Task<>(42, 2000);
    List<CB> callbacks = new java.util.ArrayList<>(1000000);
    Future<Integer> future = exec.execute(task);
    for (int i=0; i<1000000; i++) {
      CB cb = new CB(42);
      callbacks.add(cb);
      future.whenComplete(cb);
    }
    future.get();
    Thread t = factory.getThread(0);
    for (CB cb : callbacks)
      assertSame(cb.getRunner(), t);
    assertEquals(factory.getThreadCount(), 1);
  }

  @Test(timeout=5000, val=1) void test11 () throws Exception {
    Task<Integer> task = new Task<>(42, 200);
    List<CB> callbacks = new java.util.ArrayList<>(1000000);
    CB cb0 = new CB(42, 500);
    Future<Integer> future = exec.execute(task);
    future.whenComplete(cb0);
    while (cb0.getRunner() == null); // BUSY WAIT
    for (int i=0; i<1000000; i++) {
      CB cb = new CB(42);
      callbacks.add(cb);
      future.whenComplete(cb);
    }
    for (CB cb : callbacks)
      while (cb.getRunner() == null); // BUSY WAIT
    assertEquals(factory.getThreadCount(), 1);
  }
}

@Test (val=20)
class TestContinuation {

  Factory  factory;
  Executor exec;

  void BEFORE () {
    factory = new Factory();
    exec = new FactoryExecutor(factory);
  }

  @Test  (val=1) void test1 () throws Exception {
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

  @Test(timeout=5000, val=1) void test1a () throws Exception {
    Task<Integer> task = new Task<>(42, 800);
    final Future<Integer> future1 = exec.execute(task);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            ToString cont = new ToString();
            Future<String> future2 = future1.whenComplete(cont);
            Thread.sleep(500);
            assertNull(cont.getRunner());
            assertEquals(future2.get(), "[42]");
            assertTrue(future2.isDone());
            assertNull(future2.getFailure());
            assertSame(cont.getRunner(), factory.getThread(0));
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test  (val=1)  void test2 () throws Exception {
    Task<Integer> task = new Task<>(42, 500);
    ToString cont = new ToString();
    Future<Integer> future1 = exec.execute(task);
    future1.get();
    Future<String> future2 = future1.whenComplete(cont);
    assertTrue(future2.isDone());
    assertEquals(future2.get(), "[42]");
    assertNull(future2.getFailure());
    assertSame(cont.getRunner(), Thread.currentThread());
  }

  @Test(timeout=5000, val=1) void test2a () throws Exception {
    Task<Integer> task = new Task<>(42);
    final Future<Integer> future1 = exec.execute(task);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            ToString cont = new ToString();
            future1.get();
            Future<String> future2 = future1.whenComplete(cont);
            assertTrue(future2.isDone());
            assertEquals(future2.get(), "[42]");
            assertNull(future2.getFailure());
            assertSame(cont.getRunner(), this);
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test  (val=1) void test3 () throws Exception {
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

  @Test(timeout=5000, val=1) void test3a () throws Exception {
    Task<Integer> task = new Task<>(42, 800, -1);
    final Future<Integer> future1 = exec.execute(task);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            ToString cont = new ToString();
            Future<String> future2 = future1.whenComplete(cont);
            assertNull(future2.get());
            assertTrue(future2.isDone());
            Throwable t1 = future1.getFailure();
            Throwable t2 = future2.getFailure();
            assertTrue(t2 instanceof NotExecutedException);
            assertSame(t2.getCause(), t1);
            assertNull(cont.getRunner());
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test  (val=1) void test4 () throws Exception {
    Task<Integer> task = new Task<>(42, -1);
    ToString cont = new ToString();
    Future<Integer> future1 = exec.execute(task);
    Throwable t1 = future1.getFailure();
    Future<String> future2 = future1.whenComplete(cont);
    assertTrue(future2.isDone());
    assertNull(future2.get());
    Throwable t2 = future2.getFailure();
    assertTrue(t2 instanceof NotExecutedException);
    assertSame(t2.getCause(), t1);
    assertNull(cont.getRunner());
  }

  @Test(timeout=5000, val=1) void test4a () throws Exception {
    Task<Integer> task = new Task<>(42, 500, -1);
    final Future<Integer> future1 = exec.execute(task);
    for (int i=0; i<100; i++) {
      new Thread() {
        public void run () {
          try {
            ToString cont = new ToString();
            Throwable t1 = future1.getFailure();
            Future<String> future2 = future1.whenComplete(cont);
            assertTrue(future2.isDone());
            assertNull(future2.get());
            Throwable t2 = future2.getFailure();
            assertTrue(t2 instanceof NotExecutedException);
            assertSame(t2.getCause(), t1);
            assertNull(cont.getRunner());
          } catch (InterruptedException e) {
            fail("interrupted");
          }
        }
      }.start();
    }
  }

  @Test  (val=1) void test5 () throws Exception {
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

  @Test  (val=1) void test6 () throws Exception {
    Task<Integer> task = new Task<>(42, 400);
    ToString cont1 = new ToString(400);
    ToString cont2 = new ToString();
    Future<Integer> future = exec.execute(task);
    future.whenComplete(cont1);
    Thread.sleep(500);
    future.whenComplete(cont2);
    future.get();
    assertSame(cont1.getRunner(), factory.getThread(0));
    while (cont2.getRunner() == null); // BUSY WAIT
    assertEquals(factory.getThreadCount(), 1);
  }

  @Test(timeout=5000, val=1) void test7 () throws Exception {
    Task<Integer> task = new Task<>(42, 2000);
    List<ToString> conts = new java.util.ArrayList<>(1000000);
    Future<Integer> future = exec.execute(task);
    for (int i=0; i<1000000; i++) {
      ToString cont = new ToString();
      conts.add(cont);
      future.whenComplete(cont);
    }
    future.get();
    Thread t = factory.getThread(0);
    for (ToString cont : conts)
      assertSame(cont.getRunner(), t);
    assertEquals(factory.getThreadCount(), 1);
  }

  @Test(timeout=5000, val=1) void test8 () throws Exception {
    Task<Integer> task = new Task<>(42, 200);
    List<ToString> conts = new java.util.ArrayList<>(1000000);
    ToString cont0 = new ToString(500);
    Future<Integer> future = exec.execute(task);
    future.whenComplete(cont0);
    while (cont0.getRunner() == null); // BUSY WAIT
    for (int i=0; i<1000000; i++) {
      ToString cont = new ToString();
      conts.add(cont);
      future.whenComplete(cont);
    }
    for (ToString cont : conts)
      while (cont.getRunner() == null); // BUSY WAIT
    assertEquals(factory.getThreadCount(), 1);
  }

  static class C implements Continue<Integer,Integer> {
    public Integer call (Integer n) {
      return Integer.valueOf(n.intValue() + 1);
    }
  }

  @Test(timeout=5000, val=1) void test9 () throws Exception {
    Task<Integer> task = new Task<>(42, 500);
    List<CB> callbacks = new java.util.ArrayList<>(1000);
    for (int i=0; i<1000; i++)
      callbacks.add(new CB(43+i));
    Future<Integer> future = exec.execute(task);
    for (int i=0; i<1000; i++) {
      future = future.whenComplete(new C());
      future.whenComplete(callbacks.get(i));
    }
    future.get();
    Thread t = factory.getThread(0);
    for (CB cb : callbacks)
      assertSame(cb.getRunner(), t);
    assertEquals(factory.getThreadCount(), 1);
  }

}

package cs671;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tim on 3/12/14.
 */
public final class FactoryExecutor extends Object implements Executor {

    private ThreadFactory thisFactory;

    public FactoryExecutor(ThreadFactory factory)
    {
        thisFactory = factory;

    }

    /**
     * Submits a callable task for execution.
     *
     * @param task
     * @return a future with the given task as its underlying computation
     */
    @Override
    public <T> Future<T> execute(Callable<T> task) {
        //create a future to throw
        GenFuture<T> f = new GenFuture<>(task, false,null);

        Thread t = thisFactory.getThread(f);
        t.start();
        return f;

    }

    /**
     * Submits a runnable task for execution.  Note that the result
     * returned by {@code Future.get} will be {@code null}, whether the
     * task terminates normally or abruptly.
     *
     * @param task
     * @return a future with the given task as its underlying computation
     * @see cs671.Future#get
     */
    @Override
    public <T> Future<T> execute(Runnable task) {
        GenFuture<T> f = new GenFuture<>(task, false, null);

        Thread t = thisFactory.getThread(f);
        t.start();
        return f;
    }

    /**
     * Submits a callable task for execution, alongside a callback.
     * After the task terminates, the callback's {@code call} method
     * will be invoked with the value produced by the task, if the task
     * was successful.  If the task fails abruptly, the callback's
     * {@code failure} method will be called instead, with the cause of
     * the failure.
     *
     * @param task
     * @param callback
     */
    @Override
    public <T> void execute(Callable<T> task, Callback<? super T> callback) {

        GenFuture<T> f = new GenFuture<>(task, false,null);

        Thread t = thisFactory.getThread(f);
        f.whenComplete(callback);
        t.start();


    }

    /**
     * Submits a runnable task for execution, alongside a callback.
     * After the task terminates, the callback's {@code call} method
     * will be invoked with {@code null}, if the task
     * was successful.  If the task fails abruptly, the callback's
     * {@code failure} method will be called instead, with the cause of
     * the failure.
     *
     * @param task
     * @param callback
     */
    @Override
    public <T> void execute(Runnable task, Callback<T> callback) {
        GenFuture<T> f = new GenFuture<>(task, false, null);

        Thread t = thisFactory.getThread(f);
        f.whenComplete(callback);
        t.start();

    }

    public static interface ThreadFactory{
        Thread getThread(Runnable behavior);

    }
    @SuppressWarnings("rawtypes")
    private class GenFuture<T> implements Runnable, Future<T> {

        private boolean passed = false;
        private boolean done;
        private Runnable runThis;
        private Throwable thrown;
        private T thisValue;
        private boolean cb;
        private Callable call;
        private boolean isRun;
        private List<Callback> calling = Collections.synchronizedList(new ArrayList<Callback>());
        //private ArrayList<Continue> continuation = new ArrayList<>();
        private boolean continued = false;

        public GenFuture(Callable callable, boolean callback, Callback c)
        {

            call = callable;
            thisValue = null;
            done = false;
            isRun = false;
            cb = callback;
            if (cb)
                calling.add(c);


        }
        public GenFuture(Runnable r, boolean callback, Callback c)
        {

            runThis = r;
            thisValue = null;
            done = false;
            isRun = true;
            cb = callback;
            if (cb)
                calling.add(c);

        }

        @Override
        public  boolean isDone() {
           return done;
        }

        @Override
        public synchronized T get() throws InterruptedException {
           while (!done)
           {
               try{
                   wait();
               }
               catch (InterruptedException e)
               {
                   throw new InterruptedException("This is interrupted");
               }

           }
           return thisValue;
        }

        @Override
        public synchronized Throwable getFailure() throws InterruptedException {
            return thrown;
        }

        @Override
        public synchronized void whenComplete(Callback<? super T> callback) {
           if (!done)
           {
                calling.add(callback);
                cb = true;
           }
           else
           {
               if ( passed )
                    callback.call(thisValue);
               else
                   callback.failure(thrown);
           }

        }

        @Override
        public synchronized void whenComplete(Runnable callback) {
            callback.run();
        }

        @Override
        public synchronized  <U> Future<U> whenComplete(Continue<? super T, ? extends U> cont) {
            GenFuture<U> f;
            final Continue<? super T, ? extends U> conts = cont;
            class ContinueFuture implements Callable{
                GenFuture<T> g;
                @Override
                public Object call() throws Exception {
                    return conts.call(thisValue);
                }
            }


            if (!done)
            {
               continued = true;
                f = null;
            }
            else
            {
               ContinueFuture contFut = new ContinueFuture();
               f = new GenFuture<>(contFut, false, null);
               f.run();

            }
            return f;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public synchronized void run() {
            if (isRun)
            {
                try{
                runThis.run();
                }
                catch(Exception e){
                    thrown = e;
                    passed = false;
                }

                done = true;
                passed = true;
                notify();
            }
            else
            {
                try{
                    thisValue = (T) this.call.call();
                }
                catch (Exception e)
                {
                    thrown = e;
                    passed = false;
                   // throw new NotExecutedException(e);

                }
                passed = true;

                notify();

            }
            if( cb )
            {
                if (passed){
                    for ( int i = 0; i< calling.size(); i++)
                        calling.get(i).call(this.thisValue);
                }
                else
                    for ( int i = 0; i< calling.size(); i++)
                        calling.get(i).failure(thrown);
            }
            if (continued)
            {
                //thisValue = (T) cont.call(thisValue);
            }
            done = true;

        }
    }
//    private class CToR<T> implements Runnable{
//        private Callback callable;
//        private T value;
//        public CToR (Callback c, T val){
//            callable = c;
//            value = val;
//        }
//
//        @Override
//        public void run() {
//            callable.call(value);
//        }
//    }

//    private class TF implements ThreadFactory{
//        private final AtomicInteger id = new AtomicInteger();
//        @Override
//        public Thread getThread(Runnable behavior) {
//            Thread t = new Thread(behavior);
//            //t.setName("Thread-"+id.incrementAndGet());
//            return t;
//
//
//        }
//    }
}

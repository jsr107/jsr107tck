/**
 *  Copyright 2011-2013 Terracotta, Inc.
 *  Copyright 2011-2013 Oracle and/or its affiliates.
 *  Copyright 2016 headissue GmbH
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.integration;

import org.junit.Test;

import java.lang.UnsupportedOperationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * Functional tests for CompletionListenerFuture
 *
 * @author Greg Luck
 * @author Jens Wilke
 *
 * @see CompletionListenerFuture
 */
public class CompletionListenerFutureTest {

  @Test(expected = IllegalStateException.class)
  public void testOnCompletionIllegalStateExceptionThrown() {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.onCompletion();
    future.onCompletion();
  }

  @Test(expected = IllegalStateException.class)
  public void testOnExceptionIllegalStateExceptionThrown() {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.onException(new IllegalStateException());
    future.onException(new IllegalStateException());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testCancel() {
    CompletionListenerFuture future = new CompletionListenerFuture();
    assertFalse(future.isCancelled());
    future.cancel(true);
  }

  @Test
  public void testTimedGetNoWait() throws TimeoutException, ExecutionException, InterruptedException {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.onCompletion();
    future.get(3L, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testTimedGet() throws TimeoutException, ExecutionException, InterruptedException {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.onCompletion();
    future.get(3L, TimeUnit.MILLISECONDS);
  }

  @Test(expected = TimeoutException.class)
  public void testTimedGetTimeOutException() throws TimeoutException, ExecutionException, InterruptedException {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.get(3L, TimeUnit.MILLISECONDS);
  }

  @Test(expected = TimeoutException.class)
  public void testTimedGetTimeOutException0ms() throws TimeoutException, ExecutionException, InterruptedException {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.get(0L, TimeUnit.MILLISECONDS);
  }

  @Test(expected = ExecutionException.class)
  public void testTimedGetOnException() throws TimeoutException, ExecutionException, InterruptedException {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.onException(new IllegalStateException());
    future.get(3L, TimeUnit.MILLISECONDS);
  }

  @Test(expected = ExecutionException.class)
  public void testTimedGetOnException0ms() throws TimeoutException, ExecutionException, InterruptedException {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.onException(new IllegalStateException());
    future.get(0, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testGetCompleted() throws TimeoutException, ExecutionException, InterruptedException {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.onCompletion();
    future.get();
  }

  @Test(expected = ExecutionException.class)
  public void testGetOnException() throws TimeoutException, ExecutionException, InterruptedException {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.onException(new IllegalStateException());
    future.get();
  }

  @Test
  public void testIsDone() {
    CompletionListenerFuture future = new CompletionListenerFuture();
    assertFalse(future.isDone());
    future.onCompletion();
    assertTrue(future.isDone());
  }

  /**
   * Test that thread(s) is correctly waking up when completed.
   *
   * @see <a href="https://github.com/jsr107/jsr107spec/issues/320">spec#320</a>
   */
  @Test
  public void testWakeup() throws InterruptedException {
    /* Currently test only with 1 thread to be compatible with 1.0. Can be incremented later. */
    final int THREAD_COUNT = 1;
    final long TIMEOUT_MILLIS = 1 * 60 * 1000;
    final CompletionListenerFuture future = new CompletionListenerFuture();
    Thread[] threads = new Thread[THREAD_COUNT];
    for (int i = 0; i < THREAD_COUNT; i++) {
      Thread t = threads[i] = new Thread() {
        @Override
        public void run() {
          try {
            future.get();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };
      t.start();
    }
    future.onCompletion();
    for (int i = 0; i < THREAD_COUNT; i++) {
      Thread t = threads[i];
      t.join(TIMEOUT_MILLIS);
      if (t.isAlive()) {
        fail("thread not terminated");
      }
    }
  }

  /**
   * Checks that get with timeout is honoring the timeout and waiting for the
   * specified time.
   */
  @Test
  public void testCorrectWaitTime() throws Exception {
    final long TIMEOUT_MILLIS = 42;
    final CompletionListenerFuture future = new CompletionListenerFuture();
    long t0 = System.currentTimeMillis();
    try {
      future.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
      fail("exception expected");
    } catch (TimeoutException e) {
      // expected
    }
    assertTrue("minimum time passed", System.currentTimeMillis() - t0 >= TIMEOUT_MILLIS);
  }

}

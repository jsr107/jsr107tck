/**
 *  Copyright (c) 2011-2013 Terracotta, Inc.
 *  Copyright (c) 2011-2013 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.integration;

import org.junit.Test;
import sun.awt.SunToolkit;

import java.lang.UnsupportedOperationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertFalse;

/**
 * Added to complete code coverage
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

  @Test(expected = ExecutionException.class)
  public void testTimedGetOnException() throws TimeoutException, ExecutionException, InterruptedException {
    CompletionListenerFuture future = new CompletionListenerFuture();
    future.onException(new IllegalStateException());
    future.get(3L, TimeUnit.MILLISECONDS);
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
}

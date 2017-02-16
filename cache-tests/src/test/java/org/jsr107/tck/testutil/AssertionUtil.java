/**
 *  Copyright (c) 2011-2017 Terracotta, Inc.
 *  Copyright (c) 2011-2017 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package org.jsr107.tck.testutil;

import static java.lang.System.currentTimeMillis;

/**
 * Helper methods for assertions
 */
public abstract class AssertionUtil {

  /**
   * Periodically executes the {@code assertionRunnable} until it does not throw an {@code AssertionError} or
   * {@code timeoutSeconds} seconds pass, whichever happens first. In the former case, the assertion is considered
   * successful, otherwise it is considered a failure and the last {@code AssertionError} thrown by the
   * {@code assertionRunnable} is rethrown.
   * <p>
   * This method does not make any attempt to enforce the given {@code timeoutSeconds} in case execution of the
   * {@code assertionRunnable} takes longer than {@code timeoutSeconds}.
   *
   * @param assertionRunnable
   * @param timeoutSeconds
   */
  public static void assertEventually(AssertionRunnable assertionRunnable, int timeoutSeconds) {
    long deadline = currentTimeMillis() + timeoutSeconds * 1000;
    while (currentTimeMillis() < deadline) {
      try {
        runSafely(assertionRunnable);
        return;
      } catch (AssertionError assertionError) {
      }
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
      }
    }
    runSafely(assertionRunnable);
  }

  /**
   * Periodically executes the {@code assertionRunnable} and asserts it never throws an {@code AssertionError} until
   * {@code timeoutSeconds} seconds pass.
   * <p>
   * This method does not try to enforce the given {@code timeoutSeconds} in case execution of the
   * {@code assertionRunnable} takes longer than that duration.
   *
   * @param assertionRunnable
   * @param timeoutSeconds
   */
  public static void assertAllTheTime(AssertionRunnable assertionRunnable, int timeoutSeconds) {
    long deadline = currentTimeMillis() + timeoutSeconds * 1000;
    while (currentTimeMillis() < deadline) {
      try {
        runSafely(assertionRunnable);
      } catch (AssertionError assertionError) {
        throw assertionError;
      }
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
      }
    }
    // ensure the assertion is executed at least once, even when timeout = 0
    runSafely(assertionRunnable);
  }

  public interface AssertionRunnable {
    void run() throws Exception;
  }

  /**
   * Run the given {@code assertionRunnable}. Checked exceptions thrown by {@code assertionRunnable} will be
   * rethrown wrapped in {@code RuntimeException}s; runtime exceptions will be propagated as-is.
   * @param assertionRunnable
   */
  private static void runSafely(AssertionRunnable assertionRunnable) {
    try {
      assertionRunnable.run();
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      } else {
        throw new RuntimeException("Exception was thrown during assertion execution", e);
      }
    }
  }
}

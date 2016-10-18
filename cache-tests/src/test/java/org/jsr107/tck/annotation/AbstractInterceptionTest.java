/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package org.jsr107.tck.annotation;

import org.jsr107.tck.testutil.AbstractTestExcluder;
import org.junit.Rule;
import org.junit.rules.MethodRule;

import javax.cache.annotation.BeanProvider;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Base class that ALL annotation/interceptor tests MUST extend from
 *
 * @author Eric Dalquist
 * @version $Revision$
 */
public class AbstractInterceptionTest {
  private static final BeanProvider beanProvider;

  static {
    BeanProvider localBeanProvider = null;
    try {
      final ServiceLoader<BeanProvider> serviceLoader = ServiceLoader.load(BeanProvider.class);
      final Iterator<BeanProvider> it = serviceLoader.iterator();
      localBeanProvider = it.hasNext() ? it.next() : null;
    } catch (Throwable t) {
      //ignore
      System.err.println("Failed to load BeanProvider SPI impl, annotation tests will be ignored");
      t.printStackTrace(System.err);
    }

    beanProvider = localBeanProvider;
  }


  /**
   * Rule used to exclude tests that do not implement Annotations
   */
  @Rule
  public final MethodRule rule = new AbstractTestExcluder() {
    @Override
    protected boolean isExcluded(String methodName) {
      //Exclude all tests if annotations are not supported or no beanProvider has been set
      return beanProvider == null;
    }
  };

  /**
   * Loads a specified bean by type, used to retrieve an annotated bean to test from the underlying implementation
   *
   * @param beanClass The type to load
   * @return The loaded bean
   */
  protected final <T> T getBeanByType(Class<T> beanClass) {
    if (beanProvider == null) {
      throw new IllegalStateException("No tests should be run if beanProvider is null");
    }

    return beanProvider.getBeanByType(beanClass);
  }
}
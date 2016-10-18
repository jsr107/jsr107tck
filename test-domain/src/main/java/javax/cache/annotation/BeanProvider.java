/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.annotation;

/**
 * SPI used by an annotation implementation test harness to make testable beans available to the TCK
 *
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface BeanProvider {
  /**
   * Load the specified bean from the test-domain project configured appropriately for annotation testing
   *
   * @param <T>        bean type
   * @param beanClass  the bean class
   *
   * @return instance of Bean Provider
   */
  <T> T getBeanByType(Class<T> beanClass);
}

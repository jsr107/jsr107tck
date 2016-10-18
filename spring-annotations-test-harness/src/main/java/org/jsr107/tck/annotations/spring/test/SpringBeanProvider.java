/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package org.jsr107.tck.annotations.spring.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.cache.annotation.BeanProvider;

/**
 * Spring specific bean provider that loads up the spring app context when constructed
 *
 * @author Eric Dalquist
 * @version $Revision$
 */
public class SpringBeanProvider implements BeanProvider {
  private final ApplicationContext applicationContext;

  public SpringBeanProvider() {
    final ClassPathXmlApplicationContext classPathXmlApplicationContext =
        new ClassPathXmlApplicationContext("/annotationsTestContext.xml");
    classPathXmlApplicationContext.registerShutdownHook();
    this.applicationContext = classPathXmlApplicationContext;
  }

  /* (non-Javadoc)
   * @see javax.cache.annotation.BeanProvider#getBeanByType(java.lang.Class)
   */
  @Override
  public <T> T getBeanByType(Class<T> beanClass) {
    return this.applicationContext.getBean(beanClass);
  }

}

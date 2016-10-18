/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package org.jsr107.tck.annotations.cdi.test;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.cache.annotation.BeanProvider;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Set;

/**
 * Weld specific bean provider that loads up the CDI BeanManager when constructed
 *
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CdiBeanProvider implements BeanProvider {
  private final BeanManager beanManager;

  public CdiBeanProvider() {
    Weld weld = new Weld();
    WeldContainer delegate = weld.initialize();
    beanManager = delegate.getBeanManager();
  }

  /* (non-Javadoc)
   * @see javax.cache.annotation.BeanProvider#getBeanByType(java.lang.Class)
   */
  @Override
  public <T> T getBeanByType(Class<T> beanClass) {
    if (beanClass == null) {
      throw new IllegalArgumentException("CDI Bean type cannot be null");
    }

    Set<Bean<?>> beans = beanManager.getBeans(beanClass);
    if (beans.isEmpty()) {
      throw new IllegalStateException("Could not locate a bean of type " + beanClass.getName());
    }
    Bean<?> bean = beanManager.resolve(beans);
    CreationalContext<?> context = beanManager.createCreationalContext(bean);
    @SuppressWarnings("unchecked")
    T result = (T) beanManager.getReference(bean, bean.getBeanClass(),
        context);
    return result;
  }
}

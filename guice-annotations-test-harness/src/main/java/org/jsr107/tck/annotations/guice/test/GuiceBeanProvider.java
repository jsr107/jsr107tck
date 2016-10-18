/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package org.jsr107.tck.annotations.guice.test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import manager.CacheNameOnEachMethodBlogManagerImpl;
import manager.ClassLevelCacheConfigBlogManagerImpl;
import manager.UsingDefaultCacheNameBlogManagerImpl;
import org.jsr107.ri.annotations.guice.module.CacheAnnotationsModule;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.annotation.BeanProvider;
import javax.cache.spi.CachingProvider;
import java.util.logging.Logger;

/**
 * Guice specific bean provider that loads up guice modules when constructed
 *
 * @author Michael Stachel
 * @version $Revision$
 */
public class GuiceBeanProvider implements BeanProvider {
  private final Injector injector;

  public GuiceBeanProvider() {
    super();
    this.injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
        install(new CacheAnnotationsModule());
        bind(CacheNameOnEachMethodBlogManagerImpl.class);
        bind(ClassLevelCacheConfigBlogManagerImpl.class);
        bind(UsingDefaultCacheNameBlogManagerImpl.class);
        bind(CacheManager.class).toProvider(new Provider<CacheManager>() {

          @Override
          public CacheManager get() {
            CachingProvider provider = Caching.getCachingProvider();
            return provider.getCacheManager(provider.getDefaultURI(), provider.getDefaultClassLoader());
          }

        });
      }

    });
    this.injector.getInstance(Logger.class).info("Guice started successfully");
  }

  @Override
  public <T> T getBeanByType(Class<T> beanClass) {
    return this.injector.getInstance(beanClass);
  }

}

/**
 *  Copyright 2011 Terracotta, Inc.
 *  Copyright 2011 Oracle, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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

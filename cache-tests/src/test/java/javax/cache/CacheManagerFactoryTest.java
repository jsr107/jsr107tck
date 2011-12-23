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
package javax.cache;

import org.junit.Test;

import javax.cache.spi.CacheManagerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Tests the {@link javax.cache.spi.CacheManagerFactory} class.
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 *
 * @see javax.cache.spi.CacheManagerFactory
 */
public class CacheManagerFactoryTest {
    private static final String CACHE_MANAGER_NAME = CacheManagerFactoryTest.class.getName();
    private static final ClassLoader CLASS_LOADER = CacheManagerFactoryTest.class.getClassLoader();

    @Test
    public void getCacheManager1_null() {
        CacheManagerFactory factory = Caching.getCacheManagerFactory();
        try {
            factory.getCacheManager(null);
            fail();
        } catch (NullPointerException e) {
            // ok
        }
    }

    @Test
    public void getCacheManager1_sameName() {
        CacheManagerFactory factory = Caching.getCacheManagerFactory();
        String name = CACHE_MANAGER_NAME;
        CacheManager cacheManager1 = factory.getCacheManager(name);
        assertNotNull(cacheManager1);
        assertEquals(name, cacheManager1.getName());
        CacheManager cacheManager2 = factory.getCacheManager(name);
        assertSame(cacheManager1, cacheManager2);
    }

    @Test
    public void getCacheManager2_null() {
        CacheManagerFactory factory = Caching.getCacheManagerFactory();
        try {
            factory.getCacheManager(null, CACHE_MANAGER_NAME);
            fail();
        } catch (NullPointerException e) {
            // ok
        }
        try {
            factory.getCacheManager(CLASS_LOADER, null);
            fail();
        } catch (NullPointerException e) {
            // ok
        }
    }

    @Test
    public void getCacheManager2_sameNameSameClassLoader() {
        CacheManagerFactory factory = Caching.getCacheManagerFactory();
        String name = CACHE_MANAGER_NAME;
        ClassLoader classLoader = CLASS_LOADER;
        CacheManager cacheManager1 = factory.getCacheManager(classLoader, name);
        assertNotNull(cacheManager1);
        CacheManager cacheManager2 = factory.getCacheManager(classLoader, name);
        assertSame(cacheManager1, cacheManager2);
    }

    @Test
    public void getCacheManager2_sameNameDifferentClassLoader() {
        CacheManagerFactory factory = Caching.getCacheManagerFactory();
        String name = CACHE_MANAGER_NAME;
        ClassLoader classLoader1 = CLASS_LOADER;
        CacheManager cacheManager1 = factory.getCacheManager(classLoader1, name);
        assertNotNull(cacheManager1);
        factory.close();
        ClassLoader classLoader2 = new MyClassLoader(CLASS_LOADER);
        CacheManager cacheManager2 = factory.getCacheManager(classLoader2, name);
        assertNotNull(cacheManager2);
        assertNotSame(cacheManager1, cacheManager2);
    }

    @Test
    public void close() {
        CacheManagerFactory factory = Caching.getCacheManagerFactory();
        String name = CACHE_MANAGER_NAME;
        ClassLoader classLoader1 = CLASS_LOADER;
        CacheManager cacheManager1 = factory.getCacheManager(classLoader1, name);
        ClassLoader classLoader2 = new MyClassLoader(CLASS_LOADER);
        CacheManager cacheManager2 = factory.getCacheManager(classLoader2, name);
        factory.close();
        assertNotSame(cacheManager1, factory.getCacheManager(classLoader1, name));
        assertNotSame(cacheManager2, factory.getCacheManager(classLoader2, name));
    }

    @Test
    public void close1() {
        CacheManagerFactory factory = Caching.getCacheManagerFactory();
        String name = CACHE_MANAGER_NAME;
        ClassLoader classLoader1 = CLASS_LOADER;
        CacheManager cacheManager1 = factory.getCacheManager(classLoader1, name);
        ClassLoader classLoader2 = new MyClassLoader(CLASS_LOADER);
        CacheManager cacheManager2 = factory.getCacheManager(classLoader2, name);
        factory.close(classLoader2);
        assertSame(cacheManager1, factory.getCacheManager(classLoader1, name));
        assertNotSame(cacheManager2, factory.getCacheManager(classLoader2, name));
    }

    private static class MyClassLoader extends ClassLoader{
        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }
    }
}

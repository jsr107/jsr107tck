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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.util.ExcludeListExcluder;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * Tests the {@link CacheManagerFactory} class.
 * The tests here implicitly also test the {@link javax.cache.spi.CacheManagerFactoryProvider} used by the
 * CacheManagerFactory to create instances of {@link CacheManager}
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 *
 * @see CacheManagerFactory
 */
public class CacheManagerFactoryClassLoaderTest {
    CacheManagerFactory factory;

    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

    @Before
    public void startUp() {
        factory = CacheManagerFactory.INSTANCE;
    }


    /**
     * Multiple invocations of {@link CacheManagerFactory#getCacheManager()} return the same CacheManager
     */
    @Test
    public void getCacheManager_singleton() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        CacheManager defaultCacheManager = factory.getCacheManager(cl);
        assertNotNull(defaultCacheManager);
        assertSame(defaultCacheManager, factory.getCacheManager(cl));

        // for a different class loader
        ClassLoader cl1 = new MyClassLoader(cl);
        CacheManager defaultCacheManager1 = factory.getCacheManager(cl1);
        assertNotSame(defaultCacheManager, defaultCacheManager1);
        assertSame(defaultCacheManager1, factory.getCacheManager(cl1));
    }

    /**
     * {@link CacheManagerFactory#getCacheManager()} returns a default CacheManager with the name
     * {@link CacheManagerFactory#DEFAULT_CACHE_MANAGER_NAME}
     *
     */
    @Test
    public void getCacheManager_name() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        CacheManager defaultCacheManager = factory.getCacheManager(cl);
        assertSame(CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME, defaultCacheManager.getName());
    }

    /**
     * {@link CacheManagerFactory#getCacheManager(ClassLoader, String)} invoked with {@link CacheManagerFactory#DEFAULT_CACHE_MANAGER_NAME}
     * returns the default CacheManager
     */
    @Test
    public void getCacheManager_named_default() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME;
        assertSame(factory.getCacheManager(cl), factory.getCacheManager(cl, name));

        // is different for different class loader
        ClassLoader cl1 = new MyClassLoader(cl);
        assertSame(factory.getCacheManager(cl1), factory.getCacheManager(cl1, name));
        assertNotSame(factory.getCacheManager(cl), factory.getCacheManager(cl1, name));
    }

    /**
     * Multiple invocations of {@link CacheManagerFactory#getCacheManager(ClassLoader, String)} with the same name
     * return the same CacheManager instance
     */
    @Test
    public void getCacheManager_named() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "1";
        CacheManager cacheManager = factory.getCacheManager(cl, name);
        assertNotNull(cacheManager);
        assertSame(cacheManager, factory.getCacheManager(cl, name));

        // is different for different class loader
        ClassLoader cl1 = new MyClassLoader(cl);
        CacheManager cacheManager1 = factory.getCacheManager(cl1, name);
        assertSame(cacheManager1, factory.getCacheManager(cl1, name));
        assertNotSame(cacheManager, cacheManager1);
    }

    /**
     * The name of the CacheManager returned by {@link CacheManagerFactory#getCacheManager(ClassLoader, String)} is the same
     * as the name used in the invocation
     */
    @Test
    public void getCacheManager_named_name() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "1";
        assertEquals(name, factory.getCacheManager(cl, name).getName());
    }

    /**
     * Invocations of {@link CacheManagerFactory#getCacheManager(ClassLoader, String)} using a names other
     * than the default returns a CacheManager other than the default
     */
    @Test
    public void getCacheManager_named_notDefault() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "1";
        assertNotSame(factory.getCacheManager(cl), factory.getCacheManager(cl, name));
    }

    /**
     * Invocations of {@link CacheManagerFactory#getCacheManager(ClassLoader, String)} using different names return
     * different instances
     */
    @Test
    public void getCacheManager_named_different() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name1 = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "1";
        String name2 = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "2";
        assertNotSame(factory.getCacheManager(cl, name1), factory.getCacheManager(cl, name2));
    }

    @Test
    public void release() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ClassLoader cl1 = new MyClassLoader(cl);
        CacheManager cacheManager = factory.getCacheManager(cl);
        CacheManager cacheManager1 = factory.getCacheManager(cl1);
        assertSame(cacheManager, factory.getCacheManager(cl));
        assertSame(cacheManager1, factory.getCacheManager(cl1));
        factory.shutdown();
        assertNotSame(cacheManager, factory.getCacheManager(cl));
        assertNotSame(cacheManager1, factory.getCacheManager(cl1));
    }

    @Test
    public void classLoader() throws Exception {
        AppDomainHandler domainHandler1 = new AppDomainHandler();
        Class class1 = domainHandler1.getClassForDomainClass();
        Object storedInstance = class1.newInstance();
        Cache<Integer, Object> cache1 = domainHandler1.getCache();
        cache1.put(1, storedInstance);
        Object o1_1 = cache1.get(1);
        assertSame(storedInstance.getClass(), o1_1.getClass());
        assertSame(class1, o1_1.getClass());

        AppDomainHandler domainHandler2 = new AppDomainHandler();
        Class class2 = domainHandler2.getClassForDomainClass();
        Cache<Integer, Object> cache2 = domainHandler2.getCache();
        cache2.put(1, storedInstance);
        Object o2_1 = cache2.get(1);
        assertNotSame(storedInstance.getClass(), o2_1.getClass());
        assertSame(class2, o2_1.getClass());
    }

    // utilities --------------------------------------------------------------

    /**
     * Wrapper round domain program.
     */
    private static class AppDomainHandler {
        private static String TEST_CLASS_NAME = "domain.Zoo";
        /**
         * this should be set by maven to point at the domain jar
         */
        private static final String DOMAINJAR = "domainJar";
        private static final String DEFAULT_DOMAINJAR = "jsr107tck/implementation-tester/target/domainlib/domain.jar";
        private final ClassLoader classLoader;
        private final Cache<Integer, Object> cache;

        public AppDomainHandler() throws MalformedURLException {
            this.classLoader = createClassLoader();
            cache = createCache();
        }

        private ClassLoader createClassLoader() throws MalformedURLException {
            String domainJar = System.getProperty(DOMAINJAR, DEFAULT_DOMAINJAR);
            URL urls[] = new URL[]{new File(domainJar).toURI().toURL()};
            ClassLoader parent = Thread.currentThread().getContextClassLoader();
            return new URLClassLoader(urls, parent);
        }

        private Cache<Integer, Object> createCache() {
            return CacheManagerFactory.INSTANCE.getCacheManager(classLoader).<Integer, Object>createCacheBuilder("c1").build();
        }

        public Class getClassForDomainClass() throws ClassNotFoundException {
            return Class.forName(TEST_CLASS_NAME, false, classLoader);
        }

        public Cache<Integer, Object> getCache() {
            return cache;
        }
    }

    private static class MyClassLoader extends ClassLoader{
        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }
    }
}

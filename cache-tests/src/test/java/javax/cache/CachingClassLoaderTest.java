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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@link Caching} class.
 * The tests here implicitly also test the {@link javax.cache.spi.CachingProvider} used by
 * Caching to create instances of {@link CacheManager}
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 *
 * @see Caching
 */
public class CachingClassLoaderTest {

    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

    @Before
    public void startUp() {
        // clear down
        shutdown();
    }

    /**
     * Multiple invocations of {@link Caching#getCacheManager()} return the same CacheManager
     */
    @Test
    public void getCacheManager_singleton() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        CacheManager defaultCacheManager = getCacheManager(cl);
        assertNotNull(defaultCacheManager);
        assertSame(defaultCacheManager, getCacheManager(cl));

        // for a different class loader
        ClassLoader cl1 = new MyClassLoader(cl);
        CacheManager defaultCacheManager1 = getCacheManager(cl1);
        assertNotSame(defaultCacheManager, defaultCacheManager1);
        assertSame(defaultCacheManager1, getCacheManager(cl1));
    }

    /**
     * {@link Caching#getCacheManager()} returns a default CacheManager with the name
     * {@link Caching#DEFAULT_CACHE_MANAGER_NAME}
     *
     */
    @Test
    public void getCacheManager_name() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        CacheManager defaultCacheManager = getCacheManager(cl);
        assertSame(Caching.DEFAULT_CACHE_MANAGER_NAME, defaultCacheManager.getName());
    }

    /**
     * {@link Caching#getCacheManager(ClassLoader, String)} invoked with {@link Caching#DEFAULT_CACHE_MANAGER_NAME}
     * returns the default CacheManager
     */
    @Test
    public void getCacheManager_named_default() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = Caching.DEFAULT_CACHE_MANAGER_NAME;
        assertSame(getCacheManager(cl), getCacheManager(cl, name));

        // is different for different class loader
        ClassLoader cl1 = new MyClassLoader(cl);
        assertSame(getCacheManager(cl1), getCacheManager(cl1, name));
        assertNotSame(getCacheManager(cl), getCacheManager(cl1, name));
    }

    /**
     * Multiple invocations of {@link Caching#getCacheManager(ClassLoader, String)} with the same name
     * return the same CacheManager instance
     */
    @Test
    public void getCacheManager_named() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = Caching.DEFAULT_CACHE_MANAGER_NAME + "1";
        CacheManager cacheManager = getCacheManager(cl, name);
        assertNotNull(cacheManager);
        assertSame(cacheManager, getCacheManager(cl, name));

        // is different for different class loader
        ClassLoader cl1 = new MyClassLoader(cl);
        CacheManager cacheManager1 = getCacheManager(cl1, name);
        assertSame(cacheManager1, getCacheManager(cl1, name));
        assertNotSame(cacheManager, cacheManager1);
    }

    /**
     * The name of the CacheManager returned by {@link Caching#getCacheManager(ClassLoader, String)} is the same
     * as the name used in the invocation
     */
    @Test
    public void getCacheManager_named_name() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = Caching.DEFAULT_CACHE_MANAGER_NAME + "1";
        assertEquals(name, getCacheManager(cl, name).getName());
    }

    /**
     * Invocations of {@link Caching#getCacheManager(ClassLoader, String)} using a names other
     * than the default returns a CacheManager other than the default
     */
    @Test
    public void getCacheManager_named_notDefault() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = Caching.DEFAULT_CACHE_MANAGER_NAME + "1";
        assertNotSame(getCacheManager(cl), getCacheManager(cl, name));
    }

    /**
     * Invocations of {@link Caching#getCacheManager(ClassLoader, String)} using different names return
     * different instances
     */
    @Test
    public void getCacheManager_named_different() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name1 = Caching.DEFAULT_CACHE_MANAGER_NAME + "1";
        String name2 = Caching.DEFAULT_CACHE_MANAGER_NAME + "2";
        assertNotSame(getCacheManager(cl, name1), getCacheManager(cl, name2));
    }

    @Test
    public void shutdown_0_Empty() {
        //will fail if an exception thrown
        shutdown();
    }

    @Test
    public void shutdown_0_Full() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = this.toString();
        ClassLoader cl1 = new MyClassLoader(cl);
        ClassLoader cl2 = new MyClassLoader(cl);
        ClassLoader cl3 = new MyClassLoader(cl);
        CacheManager cacheManager1 = getCacheManager(cl1);
        CacheManager cacheManager2 = getCacheManager(cl2);
        CacheManager cacheManager3 = getCacheManager(cl3, name);

        //will fail if an exception thrown
        shutdown();

        assertNotSame(cacheManager1, getCacheManager(cl1));
        assertNotSame(cacheManager2, getCacheManager(cl2));
        assertNotSame(cacheManager3, getCacheManager(cl3, name));
    }

    @Test
    public void shutdown_1_hit() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = this.toString();
        ClassLoader cl1 = new MyClassLoader(cl);
        ClassLoader cl2 = new MyClassLoader(cl);
        ClassLoader cl3 = new MyClassLoader(cl);
        CacheManager cacheManager1 = getCacheManager(cl1);
        CacheManager cacheManager2 = getCacheManager(cl2);
        CacheManager cacheManager3 = getCacheManager(cl3, name);

        assertTrue(shutdown(cl2));

        assertSame(cacheManager1, getCacheManager(cl1));
        assertNotSame(cacheManager2, getCacheManager(cl2));
        assertSame(cacheManager3, getCacheManager(cl3, name));
    }

    @Test
    public void shutdown_1_miss() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = this.toString();
        ClassLoader cl1 = new MyClassLoader(cl);
        ClassLoader cl2 = new MyClassLoader(cl);
        ClassLoader cl3 = new MyClassLoader(cl);
        CacheManager cacheManager1 = getCacheManager(cl1);
        CacheManager cacheManager2 = getCacheManager(cl2);
        CacheManager cacheManager3 = getCacheManager(cl3, name);

        assertFalse(shutdown(new MyClassLoader(cl)));

        assertSame(cacheManager1, getCacheManager(cl1));
        assertSame(cacheManager2, getCacheManager(cl2));
        assertSame(cacheManager3, getCacheManager(cl3, name));
    }

    @Test
    public void shutdown_2_hit() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = this.toString();
        ClassLoader cl1 = new MyClassLoader(cl);
        ClassLoader cl2 = new MyClassLoader(cl);
        ClassLoader cl3 = new MyClassLoader(cl);
        CacheManager cacheManager1 = getCacheManager(cl1);
        CacheManager cacheManager2 = getCacheManager(cl2);
        CacheManager cacheManager3 = getCacheManager(cl3, name);

        assertTrue(shutdown(cl3, name));

        assertSame(cacheManager1, getCacheManager(cl1));
        assertSame(cacheManager2, getCacheManager(cl2));
        assertNotSame(cacheManager3, getCacheManager(cl3, name));
    }

    @Test
    public void shutdown_2_miss() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String name = this.toString();
        ClassLoader cl1 = new MyClassLoader(cl);
        ClassLoader cl2 = new MyClassLoader(cl);
        ClassLoader cl3 = new MyClassLoader(cl);
        CacheManager cacheManager1 = getCacheManager(cl1);
        CacheManager cacheManager2 = getCacheManager(cl2);
        CacheManager cacheManager3 = getCacheManager(cl3, name);

        assertFalse(shutdown(cl3, name + "a"));

        assertSame(cacheManager1, getCacheManager(cl1));
        assertSame(cacheManager2, getCacheManager(cl2));
        assertSame(cacheManager3, getCacheManager(cl3, name));
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

    private static CacheManager getCacheManager(ClassLoader classLoader) {
        return Caching.getCacheManager(classLoader);
    }

    private static CacheManager getCacheManager(ClassLoader classLoader, String name) {
        return Caching.getCacheManager(classLoader, name);
    }

    private static void shutdown() {
        Caching.close();
    }

    private static boolean shutdown(ClassLoader classLoader) {
        return Caching.close(classLoader);
    }

    private static boolean shutdown(ClassLoader classLoader, String name) {
        return Caching.close(classLoader, name);
    }

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
            return Caching.getCacheManager(classLoader).<Integer, Object>createCacheBuilder("c1").build();
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

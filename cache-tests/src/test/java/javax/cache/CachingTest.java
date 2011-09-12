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

import org.junit.Rule;
import org.junit.Test;

import javax.cache.util.ExcludeListExcluder;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Tests the {@link Caching} class.
 * The tests here implicitly also test the {@link javax.cache.spi.CachingProvider} used by the
 * CacheManagerFactory to create instances of {@link CacheManager}
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 *
 * @see Caching
 */
public class CachingTest {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

    /**
     * Multiple invocations of {@link Caching#getCacheManager()} return the same CacheManager
     */
    @Test
    public void getCacheManager_singleton() {
        CacheManager defaultCacheManager = getCacheManager();
        assertNotNull(defaultCacheManager);
        assertSame(defaultCacheManager, getCacheManager());
    }

    /**
     * {@link Caching#getCacheManager()} returns a default CacheManager with the name
     * {@link Caching#DEFAULT_CACHE_MANAGER_NAME}
     *
     */
    @Test
    public void getCacheManager_name() {
        CacheManager defaultCacheManager = getCacheManager();
        assertSame(Caching.DEFAULT_CACHE_MANAGER_NAME, defaultCacheManager.getName());
    }

    /**
     * {@link Caching#getCacheManager(ClassLoader, String)} invoked with {@link Caching#DEFAULT_CACHE_MANAGER_NAME}
     * returns the default CacheManager
     */
    @Test
    public void getCacheManager_named_default() {
        String name = Caching.DEFAULT_CACHE_MANAGER_NAME;
        assertSame(getCacheManager(), getCacheManager(name));
    }

    /**
     * Multiple invocations of {@link Caching#getCacheManager(ClassLoader, String)} with the same name
     * return the same CacheManager instance
     */
    @Test
    public void getCacheManager_named() {
        String name = Caching.DEFAULT_CACHE_MANAGER_NAME + "1";
        CacheManager cacheManager = getCacheManager(name);
        assertNotNull(cacheManager);
        assertSame(cacheManager, getCacheManager(name));
    }

    /**
     * The name of the CacheManager returned by {@link Caching#getCacheManager(ClassLoader, String)} is the same
     * as the name used in the invocation
     */
    @Test
    public void getCacheManager_named_name() {
        String name = Caching.DEFAULT_CACHE_MANAGER_NAME + "1";
        assertEquals(name, getCacheManager(name).getName());
    }

    /**
     * Invocations of {@link Caching#getCacheManager(ClassLoader, String)} using a names other
     * than the default returns a CacheManager other than the default
     */
    @Test
    public void getCacheManager_named_notDefault() {
        String name = Caching.DEFAULT_CACHE_MANAGER_NAME + "1";
        assertNotSame(getCacheManager(), getCacheManager(name));
    }

    /**
     * Invocations of {@link Caching#getCacheManager(ClassLoader, String)} using different names return
     * different instances
     */
    @Test
    public void getCacheManager_named_different() {
        String name1 = Caching.DEFAULT_CACHE_MANAGER_NAME + "1";
        String name2 = Caching.DEFAULT_CACHE_MANAGER_NAME + "2";
        assertNotSame(getCacheManager(name1), getCacheManager(name2));
    }

    @Test
    public void isSupported() {
        OptionalFeature[] features = OptionalFeature.values();
        for (OptionalFeature feature:features) {
            boolean value = isSupported(feature);
            Logger.getLogger(getClass().getName()).info("Optional feature " + feature + " supported=" + value);
        }
    }

    @Test
    public void release() {
        CacheManager cacheManager = getCacheManager();
        assertSame(cacheManager, getCacheManager());
        shutdown();
        assertNotSame(cacheManager, getCacheManager());
    }

    /**
     * Used to confirm that file exclusion works.
     * @see #rule
     */
    @Test
    public void dummyTest() {
        fail();
    }

    // utilities --------------------------------------------------------------

    private static CacheManager getCacheManager() {
        return Caching.getCacheManager();
    }

    private static CacheManager getCacheManager(String name) {
        return Caching.getCacheManager(name);
    }

    private static void shutdown() {
        Caching.close();
    }

    private static boolean isSupported(OptionalFeature optionalFeature) {
        return Caching.isSupported(optionalFeature);
    }
}

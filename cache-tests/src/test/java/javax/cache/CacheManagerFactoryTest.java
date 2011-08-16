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
 * Tests the {@link CacheManagerFactory} class.
 * The tests here implicitly also test the {@link javax.cache.spi.CacheManagerFactoryProvider} used by the
 * CacheManagerFactory to create instances of {@link CacheManager}
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 *
 * @see CacheManagerFactory
 */
public class CacheManagerFactoryTest {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

    /**
     * Multiple invocations of {@link CacheManagerFactory#getCacheManager()} return the same CacheManager
     */
    @Test
    public void getCacheManager_singleton() {
        CacheManager defaultCacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        assertNotNull(defaultCacheManager);
        assertSame(defaultCacheManager, CacheManagerFactory.INSTANCE.getCacheManager());
    }

    /**
     * {@link CacheManagerFactory#getCacheManager()} returns a default CacheManager with the name
     * {@link CacheManagerFactory#DEFAULT_CACHE_MANAGER_NAME}
     *
     */
    @Test
    public void getCacheManager_name() {
        CacheManager defaultCacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        assertSame(CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME, defaultCacheManager.getName());
    }

    /**
     * {@link CacheManagerFactory#getCacheManager(String)} invoked with {@link CacheManagerFactory#DEFAULT_CACHE_MANAGER_NAME}
     * returns the default CacheManager
     */
    @Test
    public void getCacheManager_named_default() {
        String name = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME;
        assertSame(CacheManagerFactory.INSTANCE.getCacheManager(), CacheManagerFactory.INSTANCE.getCacheManager(name));
    }

    /**
     * Multiple invocations of {@link CacheManagerFactory#getCacheManager(String)} with the same name
     * return the same CacheManager instance
     */
    @Test
    public void getCacheManager_named() {
        String name = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "1";
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager(name);
        assertNotNull(cacheManager);
        assertSame(cacheManager, CacheManagerFactory.INSTANCE.getCacheManager(name));
    }

    /**
     * The name of the CacheManager returned by {@link CacheManagerFactory#getCacheManager(String)} is the same
     * as the name used in the invocation
     */
    @Test
    public void getCacheManager_named_name() {
        String name = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "1";
        assertEquals(name, CacheManagerFactory.INSTANCE.getCacheManager(name).getName());
    }

    /**
     * Invocations of {@link CacheManagerFactory#getCacheManager(String)} using a names other
     * than the default returns a CacheManager other than the default
     */
    @Test
    public void getCacheManager_named_notDefault() {
        String name = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "1";
        assertNotSame(CacheManagerFactory.INSTANCE.getCacheManager(), CacheManagerFactory.INSTANCE.getCacheManager(name));
    }

    /**
     * Invocations of {@link CacheManagerFactory#getCacheManager(String)} using different names return
     * different instances
     */
    @Test
    public void getCacheManager_named_different() {
        String name1 = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "1";
        String name2 = CacheManagerFactory.DEFAULT_CACHE_MANAGER_NAME + "2";
        assertNotSame(CacheManagerFactory.INSTANCE.getCacheManager(name1), CacheManagerFactory.INSTANCE.getCacheManager(name2));
    }

    @Test
    public void isSupported() {
        OptionalFeature[] features = OptionalFeature.values();
        for (OptionalFeature feature:features) {
            boolean value = CacheManagerFactory.INSTANCE.isSupported(feature);
            Logger.getLogger(getClass().getName()).info("Optional feature " + feature + " supported=" + value);
        }
    }

    /**
     * Used to confirm that file exclusion works.
     * @see #rule
     */
    @Test
    public void dummyTest() {
        fail();
    }
}

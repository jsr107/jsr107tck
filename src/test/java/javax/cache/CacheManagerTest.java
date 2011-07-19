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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for CacheManager
 * <p/>
 *
 * Implementers of Cache should subclass this test, overriding {@link #getCacheManager()}
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheManagerTest extends TestSupport {

    @Test
    public void createCacheBuilder_NullCacheName() {
        CacheManager cacheManager = getCacheManager();
        try {
            cacheManager.createCacheBuilder(null);
            fail("should have thrown an exception - null cache name not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void createCache_Same() {
        String name = "c1";
        Cache cache = getCacheManager().createCacheBuilder(name).build();
        assertSame(cache, getCacheManager().getCache(name));
    }

    @Test
    public void createCache_NameOK() {
        String name = "c1";
        Cache cache = getCacheManager().createCacheBuilder(name).build();
        assertEquals(name, cache.getCacheName());
    }

    @Test
    public void createCache_StatusOK() {
        String name = "c1";
        Cache cache = getCacheManager().createCacheBuilder(name).build();
        assertSame(Status.STARTED, cache.getStatus());
    }

    @Test
    public void createCache_NullCacheConfiguration() {
        String name = "c1";
        CacheBuilder builder = getCacheManager().createCacheBuilder(name);
        try {
            builder.setCacheConfiguration(null);
            fail("should have thrown an exception - null cache configuration not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void createCache_CacheConfiguration_NameOK() {
        String name = "c1";
        Cache cache = getCacheManager().createCacheBuilder(name).
                setCacheConfiguration(createCacheConfiguration()).build();
        assertEquals(name, cache.getCacheName());
        assertSame(cache, getCacheManager().getCache(name));
    }

    @Test
    public void createCache_CacheConfiguration_StatusOK() {
        String name = "c1";
        Cache cache = getCacheManager().createCacheBuilder(name).
                setCacheConfiguration(createCacheConfiguration()).build();
        assertSame(Status.STARTED, cache.getStatus());
    }

    @Test
    public void createCache_Different() {
        String name1 = "c1";
        Cache<Integer, String> cache1 = getCacheManager().<Integer, String>createCacheBuilder(name1).build();
        assertEquals(Status.STARTED, cache1.getStatus());

        String name2 = "c2";
        Cache<Integer, String> cache2 = getCacheManager().<Integer, String>createCacheBuilder(name2).build();
        assertEquals(Status.STARTED, cache2.getStatus());

        assertEquals(cache1, getCacheManager().getCache(name1));
        assertEquals(cache2, getCacheManager().getCache(name2));
    }

    @Test
    public void createCache_DifferentSameName() {
        CacheManager cacheManager = getCacheManager();
        String name1 = "c1";
        Cache<Integer, String> cache1 = getCacheManager().<Integer, String>createCacheBuilder(name1).build();
        assertEquals(cache1, cacheManager.<Integer, String>getCache(name1));
        checkStarted(cache1);

        Cache<Integer, String> cache2 = getCacheManager().<Integer, String>createCacheBuilder(name1).build();
        assertEquals(cache2, cacheManager.<Integer, String>getCache(name1));
        checkStarted(cache2);
        checkStopped(cache1);
    }

    @Test
    public void addCache_NullCache() {
        CacheManager cacheManager = getCacheManager();
        try {
            cacheManager.addCache(null);
            fail("should have thrown an exception - cache null");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void addCache_2Different() {
        CacheManager cacheManager = getCacheManager();

        String name1 = "c1";
        Cache<Integer, String> cache1 = createOrphanCache(name1);
        cacheManager.addCache(cache1);
        assertEquals(Status.STARTED, cache1.getStatus());

        String name2 = "c2";
        Cache<Integer, String> cache2 = createOrphanCache(name2);
        cacheManager.addCache(cache2);
        assertEquals(Status.STARTED, cache2.getStatus());

        assertEquals(cache1, cacheManager.<Integer, String>getCache(name1));
        assertEquals(cache2, cacheManager.<Integer, String>getCache(name2));
    }

    @Test
    public void addCache_2DifferentSameName() {
        CacheManager cacheManager = getCacheManager();
        String name1 = "c1";
        Cache<Integer, String> cache1 = createOrphanCache(name1);
        cacheManager.addCache(cache1);
        assertEquals(cache1, cacheManager.<Integer, String>getCache(name1));
        checkStarted(cache1);

        Cache<Integer, String> cache2 = createOrphanCache(name1);
        cacheManager.addCache(cache2);
        assertEquals(cache2, cacheManager.<Integer, String>getCache(name1));
        checkStarted(cache2);
        checkStopped(cache1);
    }

    @Test
    public void removeCache_Null() {
        CacheManager cacheManager = getCacheManager();
        try {
            cacheManager.removeCache(null);
            fail("should have thrown an exception - cache name null");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void removeCache() {
        CacheManager cacheManager = getCacheManager();
        String name1 = "c1";
        CacheBuilder<Integer, String> builder1 = cacheManager.createCacheBuilder(name1);
        Cache<Integer, String> cache1 = builder1.build();
        assertTrue(cacheManager.removeCache(name1));
        checkStopped(cache1);
    }

    @Test
    public void removeCache_NotThere() {
        CacheManager cacheManager = getCacheManager();
        assertFalse(cacheManager.removeCache("c1"));
    }

    /**
     * Checks that stop is called on all caches
     */
    @Test
    public void shutdown() {
        CacheManager cacheManager = getCacheManager();

        String name1 = "c1";
        CacheBuilder<Integer, String> builder1 = cacheManager.createCacheBuilder(name1);
        Cache<Integer, String> cache1 = builder1.build();

        String name2 = "c2";
        CacheBuilder<Integer, String> builder2 = cacheManager.createCacheBuilder(name2);
        Cache<Integer, String> cache2 = builder2.build();

        cacheManager.shutdown();

        checkStopped(cache1);
        checkStopped(cache2);
    }

    // ---------- utilities ----------

    private void checkStarted(Cache cache) {
        Status status = cache.getStatus();
        //may be asynchronous
        assertTrue(status == Status.STARTED || status == Status.STARTING);
    }

    private void checkStopped(Cache cache) {
        Status status = cache.getStatus();
        //may be asynchronous
        assertTrue(status == Status.STOPPED || status == Status.STOPPING);
    }
}

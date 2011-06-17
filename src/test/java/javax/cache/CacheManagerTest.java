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

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;

import javax.cache.implementation.RICache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
public class CacheManagerTest {

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
        Cache<Integer, String> cache1 = new RICache.Builder<Integer, String>().setCacheName(name1).build();
        cacheManager.addCache(cache1);
        assertEquals(Status.STARTED, cache1.getStatus());

        String name2 = "c2";
        Cache<Integer, String> cache2 = new RICache.Builder<Integer, String>().setCacheName(name2).build();
        cacheManager.addCache(cache2);
        assertEquals(Status.STARTED, cache2.getStatus());

        assertEquals(cache1, cacheManager.<Integer, String>getCache(name1));
        assertEquals(cache2, cacheManager.<Integer, String>getCache(name2));
    }

    @Test
    public void addCache_2DifferentSameName() {
        CacheManager cacheManager = getCacheManager();
        String name1 = "c1";
        Cache<Integer, String> cache1 = new RICache.Builder<Integer, String>().setCacheName(name1).build();
        cacheManager.addCache(cache1);
        assertEquals(cache1, cacheManager.<Integer, String>getCache(name1));
        checkStarted(cache1);

        Cache<Integer, String> cache2 = new RICache.Builder<Integer, String>().setCacheName(name1).build();
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
        Cache<Integer, String> cache1 = new RICache.Builder<Integer, String>().setCacheName(name1).build();
        cacheManager.addCache(cache1);
        assertTrue(cacheManager.removeCache(name1));
        checkStopped(cache1);
    }

    @Test
    public void removeCache_NotThere() {
        CacheManager cacheManager = getCacheManager();
        assertFalse(cacheManager.removeCache("c1"));
    }

    /**
     * Checks that stop is called on all caches, even after exception is thrown
     * @param cache1 a mock cache that throws CacheException on stop
     * @param cache2 a mock cache
     */
    @Test
    public void shutdown(@Mocked final Cache cache1, @Mocked final Cache cache2) {
        CacheManager cacheManager = getCacheManager();
        new Expectations() {{
            cache1.getCacheName(); returns("c1");
            cache2.getCacheName(); returns("c2");

            cache1.stop(); times = 1; result = new CacheException("something bad stopping 1");
            cache2.stop(); times = 1; result = new CacheException("something bad stopping 2");
        }};
        cacheManager.addCache(cache1);
        cacheManager.addCache(cache2);
        cacheManager.shutdown();
    }

    // ---------- utilities ----------

    private CacheManager getCacheManager() {
        return TestInstanceFactory.getInstance().getCacheManager();
    }

    private void checkStarted(Cache cache) {
        Status status = cache.getStatus();
        //may be asynchronous
        assertTrue(status == Status.STARTED || status == Status.STARTING);
    }

    private void checkStopped(Cache cache) {
        Status status = cache.getStatus();
        //may be asynchronous
        assertTrue(status == Status.STOPPED|| status == Status.STOPPING);
    }
}

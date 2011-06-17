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

import static org.junit.Assert.assertEquals;
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
    public void addCache_NullCacheName(@Mocked final Cache cache) {
        CacheManager cacheManager = getCacheManager();
        new Expectations() {{
            cache.getCacheName(); returns(null); times = 1;
        }};
        try {
            cacheManager.addCache(cache);
            fail("should have thrown an exception - cache name null");
        } catch (NullPointerException e) {
            //good
        }
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
    public void addCache_2Different(@Mocked final Cache<Integer, String> cache1, @Mocked final Cache cache2) {
        CacheManager cacheManager = getCacheManager();
        final String name1 = "c1";
        final String name2 = "c2";
        new Expectations() {{
            cache1.getCacheName(); returns(name1);
            cache2.getCacheName(); returns(name2);
        }};
        cacheManager.addCache(cache1);
        cacheManager.addCache(cache2);
        assertEquals(cache1, cacheManager.<Integer, String>getCache(name1));
        assertEquals(cache2, cacheManager.<Integer, String>getCache(name2));
    }

    @Test
    public void addCache_2DifferentSameName(@Mocked final Cache<Integer, String> cache1, @Mocked final Cache cache2) {
        CacheManager cacheManager = getCacheManager();
        final String name1 = "c1";
        new Expectations() {{
            cache1.getCacheName(); returns(name1);
            cache2.getCacheName(); returns(name1);
        }};
        cacheManager.addCache(cache1);
        assertEquals(cache1, cacheManager.<Integer, String>getCache(name1));
        cacheManager.addCache(cache2);
        assertEquals(cache2, cacheManager.<Integer, String>getCache(name1));
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
}

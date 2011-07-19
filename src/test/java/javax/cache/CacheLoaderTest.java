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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for Cache.
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 */
public class CacheLoaderTest extends TestSupport {

    /**
     * the time to wait for a future
     */
    protected static final long FUTURE_WAIT_MILLIS = 100;

    @Test
    public void load_NullKey() {
        Cache<Integer, Integer> cache = createCache();
        CacheLoader<Integer, Integer> cl = new MockCacheLoader<Integer, Integer>();
        try {
            cache.load(null, cl, null);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void load_Found() {
        Cache<Integer, Integer> cache = createCache();

        CacheLoader<Integer, Integer> cl = new MockCacheLoader<Integer, Integer>();
        Integer key = 1;
        cache.put(key, key);
        try {
            assertNull(cache.load(key, cl, null));
        } catch (NullPointerException e) {
            fail("should not have thrown an exception - if key in store should return null");
        }
    }

    @Test
    public void load_NoCacheLoader() {
        Cache<Integer, Integer> cache = createCache();
        Integer key = 1;
        try {
            assertNull(cache.load(key, null, null));
        } catch (NullPointerException e) {
            fail("should not have thrown an exception - with no cache loader should return null");
        }
    }

    @Test
    public void load_NullValue() throws Exception {
        final Integer valueDefault = null;
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer key, Object arg) {
                return valueDefault;
            }
        };
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Future<Integer> future = cache.load(key, null, null);
        assertNotNull(future);
        try {
            assertEquals(valueDefault, future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS));
            fail("should have thrown an exception - null value not allowed");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof NullPointerException);
            assertFalse(cache.containsKey(key));
        }
    }

    @Test
    public void load_DefaultCacheLoader() throws Exception {
        final Integer valueDefault = 123;
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer key, Object arg) {
                return valueDefault;
            }
        };
        Cache<Integer, Integer> cache = createCache(clDefault);


        Integer key = 1;
        Future<Integer> future = cache.load(key, null, null);
        assertNotNull(future);
        assertEquals(valueDefault, future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS));
        assertTrue(cache.containsKey(key));
        assertEquals(valueDefault, cache.get(key));
    }

    @Test
    public void load_BothCacheLoader() throws Exception {
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>();
        final Integer valueSpecific = 123;
        CacheLoader<Integer, Integer> clSpecific = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer key, Object arg) {
                return valueSpecific;
            }
        };
        Cache<Integer, Integer> cache = createCache(clDefault);


        Integer key = 1;
        Future<Integer> future = cache.load(key, clSpecific, null);
        assertNotNull(future);
        assertEquals(valueSpecific, future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS));
        assertTrue(cache.containsKey(key));
        assertEquals(valueSpecific, cache.get(key));
    }

    @Test
    public void load_ExceptionPropagation() throws Exception {
        final RuntimeException expectedException = new RuntimeException("expected");
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer key, Object arg) {
                throw expectedException;
            }
        };
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Future<Integer> future = cache.load(key, null, null);
        assertNotNull(future);
        try {
            future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            fail("expected exception");
        } catch (ExecutionException e) {
            assertEquals(expectedException, e.getCause());
        }
    }

    @Test
    public void loadAll_NotStarted() {
        Cache<Integer, Integer> cache = createCache();
        cache.stop();
        try {
            cache.loadAll(null, null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void loadAll_NullKeys() {
        Cache<Integer, Integer> cache = createCache();
        try {
            cache.loadAll(null, null, null);
            fail("should have thrown an exception - keys null");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void loadAll_NullKey() throws Exception {
        final Cache<Integer, Integer> cache = createCache();
        CacheLoader<Integer, Integer> cl = new SimpleCacheLoader<Integer>();
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(null);
        try {
            cache.loadAll(keys, cl, null);
            fail("should have thrown an exception - keys contains null");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void loadAll_NullValue() throws Exception {
        final Cache<Integer, Integer> cache = createCache();
        CacheLoader<Integer, Integer> cl = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Map<Integer, Integer> loadAll(Collection<? extends Integer> keys, Object arg) {
                Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                for (Integer key : keys) {
                    map.put(key, null);
                }
                return map;
            }
        };
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        keys.add(2);
        Future<Map<Integer, Integer>> future = cache.loadAll(keys, cl, null);
        try {
            Map<Integer, Integer> map = future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            assertEquals(keys.size(), map.size());
            fail("should have thrown an exception - null value");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }

    @Test
    public void loadAll_1Found1Not() throws Exception {
        Cache<Integer, Integer> cache = createCache();


        CacheLoader<Integer, Integer> cl = new SimpleCacheLoader<Integer>();
        Integer keyThere = 1;
        cache.put(keyThere, keyThere);
        Integer keyNotThere = keyThere + 1;
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(keyThere);
        keys.add(keyNotThere);
        Future<Map<Integer, Integer>> future = cache.loadAll(keys, cl, null);
        Map<Integer, Integer> map = future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(1, map.size());
        assertEquals(keyNotThere, map.get(keyNotThere));
        assertEquals(keyThere, cache.get(keyThere));
        assertEquals(keyNotThere, cache.get(keyNotThere));
    }

    @Test
    public void loadAll_NoCacheLoader() throws Exception {
        Cache<Integer, Integer> cache = createCache();
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        try {
            assertNull(cache.loadAll(keys, null, null));
        } catch (NullPointerException e) {
            fail("should not have thrown an exception - with no cache loader should return null");
        }
    }

    @Test
    public void loadAll_DefaultCacheLoader() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        keys.add(2);
        CacheLoader<Integer, Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);


        Future<Map<Integer, Integer>> future = cache.loadAll(keys, null, null);
        Map<Integer, Integer> map = future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(keys.size(), map.size());
        for (Integer key : keys) {
            assertEquals(key, map.get(key));
            assertEquals(key, cache.get(key));
        }
    }

    @Test
    public void loadAll_BothCacheLoader() throws Exception {
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>();
        CacheLoader<Integer, Integer> clSpecific = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);


        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        keys.add(2);
        Future<Map<Integer, Integer>> future = cache.loadAll(keys, clSpecific, null);
        Map<Integer, Integer> map = future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(keys.size(), map.size());
        for (Integer key : keys) {
            assertEquals(key, map.get(key));
            assertEquals(key, cache.get(key));
        }
    }

    @Test
    public void loadAll_ExceptionPropagation() throws Exception {
        final RuntimeException expectedException = new RuntimeException("expected");
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Map<Integer, Integer> loadAll(Collection<? extends Integer> keys, Object arg) {
                throw expectedException;
            }
        };
        Cache<Integer, Integer> cache = createCache(clDefault);
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        Future<Map<Integer, Integer>> future = cache.loadAll(keys, null, null);
        assertNotNull(future);
        try {
            future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            fail("expected exception");
        } catch (ExecutionException e) {
            assertEquals(expectedException, e.getCause());
        }
    }

    // ---------- utilities ----------

    /**
     * A simple example of a Cache Loader which simply adds the key as the value.
     * @param <K>
     */
    private static class SimpleCacheLoader<K> implements CacheLoader<K, K> {
        public K load(K key, Object arg) {
            return key;
        }

        public Map<K, K> loadAll(Collection<? extends K> keys, Object arg) {
            Map<K, K> map = new HashMap<K, K>();
            for (K key : keys) {
                map.put(key, key);
            }
            return map;
        }
    }
}

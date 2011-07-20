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
import java.util.LinkedList;
import java.util.List;
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

    @Test
    public void get_Stored() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);

        Integer key = 1;
        assertFalse(cache.containsKey(key));
        assertEquals(key, cache.get(key));
        assertTrue(cache.containsKey(key));

        // Confirm that result is stored (no 2nd load)
        clDefault.exception = new NullPointerException();
        assertEquals(key, cache.get(key));
    }

    @Test
    public void get_Exception() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);

        Integer key = 1;
        clDefault.exception = new NullPointerException();
        try {
            cache.get(key);
            fail("should have got an exception ");
        } catch (RuntimeException e) {
            assertEquals(clDefault.exception, e);
        }
    }

    @Test
    public void getAll() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);

        ArrayList<Integer> keysToGet = new ArrayList<Integer>();
        keysToGet.add(1);
        keysToGet.add(2);
        keysToGet.add(3);

        Map<Integer, Integer> map = cache.getAll(keysToGet);
        assertEquals(keysToGet.size(), map.size());
        for (Integer key : keysToGet) {
            assertTrue(map.containsKey(key));
            assertEquals(cache.get(key), map.get(key));
            assertEquals(key, map.get(key));
        }

        // Confirm that result is stored (no 2nd load)
        for (Integer key : keysToGet) {
            assertTrue(cache.containsKey(key));
        }
    }

    @Test
    public void containsKey() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        assertFalse(cache.containsKey(key));
        assertEquals(key, cache.get(key));
        assertTrue(cache.containsKey(key));
    }

    @Test
    public void putIfAbsent() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Integer value = key + 1;
        assertTrue(cache.putIfAbsent(key, value));
        assertEquals(value, cache.get(key));
    }

    @Test
    public void getAndRemove_NotExistent() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        assertNull(cache.getAndRemove(key));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void getAndRemove_There() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Integer value = key  + 1;
        cache.put(key, value);
        assertEquals(value, cache.getAndRemove(key));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_3arg_Missing() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Integer oldValue = key;
        Integer newValue = oldValue  + 1;
        assertFalse(cache.replace(key, oldValue, newValue));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_3arg_Different() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Integer value1 = key  + 1;
        Integer value2 = value1 + 1;
        Integer value3 = value2 + 1;
        cache.put(key, value1);
        assertFalse(cache.replace(key, value2, value3));
        assertEquals(value1, cache.get(key));
    }

    @Test
    public void replace_3arg() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Integer value1 = key  + 1;
        Integer value2 = value1 + 1;
        Integer value3 = value2 + 1;
        cache.put(key, value2);
        assertTrue(cache.replace(key, value2, value3));
        assertEquals(value3, cache.get(key));
    }

    @Test
    public void replace_2arg_Missing() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Integer oldValue = key;
        assertFalse(cache.replace(key, oldValue));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_2arg() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Integer value1 = key  + 1;
        Integer value2 = value1 + 1;
        Integer value3 = value2 + 1;
        cache.put(key, value2);
        assertTrue(cache.replace(key, value3));
        assertEquals(value3, cache.get(key));
    }

    @Test
    public void getAndReplace() {
        SimpleCacheLoader<Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createCache(clDefault);
        Integer key = 1;
        Integer newValue = key + 1;
        assertNull(cache.getAndReplace(key, newValue));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void get_WithNonKeyKey() {
        ArrayList<Integer> key1 = new ArrayList<Integer>();
        key1.add(1);
        key1.add(2);
        LinkedList<Integer> key2 = new LinkedList<Integer>(key1);

        assertEquals(key1, key2);
        assertEquals(key2, key1);

        CacheLoader<ArrayList<Integer>, String> cacheLoader = new ArrayListCacheLoader();
        Cache<ArrayList<Integer>, String> cache = createCache(cacheLoader);

        String value = cache.get(key2);
        assertEquals(cacheLoader.loadEntry(key2,  null).getValue(), value);
    }

    @Test
    public void getWithNonKeyKey2() {
        ArrayList<Integer> key1 = new ArrayList<Integer>();
        key1.add(1);
        key1.add(2);
        LinkedList<Integer> key2 = new LinkedList<Integer>(key1);

        assertEquals(key1, key2);
        assertEquals(key2, key1);

        Cache<ArrayList<Integer>, String> cache = createCache();
        CacheLoader<ArrayList<Integer>, String> cacheLoader = new ArrayListCacheLoader();

        String value1 = "value1";

        cache.put(key1, value1);
        // Note: list2 is LinkedList which is NOT the key type (or be cast to it)
        String value2 = cache.get(key2);
        assertEquals(value1, value2);

        // The following is attempting what the implementation of Cache.get would need to do on a cache miss.

        // NOTE: The following does not compile
//        String value3 = cacheLoader.load(key2, null);
//        cache.put(key2, value3);

        // with proposed new api
        Cache.Entry<ArrayList<Integer>, String> entry3 = cacheLoader.loadEntry(key2, null);
        cache.put(entry3.getKey(), entry3.getValue());
    }

    // ---------- utilities ----------

    /**
     * A simple example of a Cache Loader which simply adds the key as the value.
     * @param <K>
     */
    private static class SimpleCacheLoader<K> implements CacheLoader<K, K> {
        private RuntimeException exception = null;

        public K load(K key, Object arg) {
            if (exception != null) {
                throw exception;
            }
            return key;
        }

        public Cache.Entry<K, K> loadEntry(Object key, Object arg) {
            throw new UnsupportedOperationException();
        }

        public Map<K, K> loadAll(Collection<? extends K> keys, Object arg) {
            Map<K, K> map = new HashMap<K, K>();
            for (K key : keys) {
                map.put(key, key);
            }
            return map;
        }
    }

    /**
     * A simple example of a Cache Loader
     */
    private static class ArrayListCacheLoader implements CacheLoader<ArrayList<Integer>, String> {
        public String load(ArrayList<Integer> key, Object arg) {
            throw new UnsupportedOperationException();
        }

        public Cache.Entry<ArrayList<Integer>, String> loadEntry(final Object key, Object arg) {
            return new Cache.Entry<ArrayList<Integer>, String>() {
                public ArrayList<Integer> getKey() {
                    return new ArrayList<Integer>((List) key);
                }

                public String getValue() {
                    return key.toString();
                }
            };
        }

        public Map<ArrayList<Integer>, String> loadAll(Collection<? extends ArrayList<Integer>> keys, Object arg) {
            throw new UnsupportedOperationException();
        }
    }
}

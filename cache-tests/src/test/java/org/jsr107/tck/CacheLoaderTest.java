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

package org.jsr107.tck;

import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheLoader;
import javax.cache.Factories;
import javax.cache.MutableConfiguration;
import javax.cache.event.CompletionListenerFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for {@link CacheLoader}s.
 *
 * @author Yannis Cosmadopoulos
 * @author Brian Oliver
 * 
 * @since 1.0
 */
public class CacheLoaderTest extends TestSupport {

    /**
     * the time to wait for a future
     */
    protected static final long FUTURE_WAIT_MILLIS = 100;


    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());


    @After
    public void cleanup() {
        for (Cache<?, ?> cache : getCacheManager().getCaches()) {
            getCacheManager().removeCache(cache.getName());
        }
    }

    @Test
    public void load_DefaultCacheLoader() throws Exception {
        CacheLoader<Integer, Integer> clDefault = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(clDefault)));

        Integer key = 123;

        CompletionListenerFuture future = new CompletionListenerFuture();
        cache.loadAll(Collections.singleton(key), true, future);

        future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);

        assertTrue(future.isDone());
        assertTrue(cache.containsKey(key));
        assertEquals(key, cache.get(key));
    }

    @Test
    public void load_ExceptionPropagation() throws Exception {
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(clDefault)));
        
        Integer key = 1;

        CompletionListenerFuture future = new CompletionListenerFuture();
        cache.loadAll(Collections.singleton(key), true, future);

        try {
            future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            assertTrue(future.isDone());

            fail("expected exception");
        } catch (ExecutionException e) {
            assertEquals(UnsupportedOperationException.class, e.getCause().getClass());
        }
    }

    @Test
    public void loadAll_NotStarted() {
        Cache<Integer, Integer> cache = getCacheManager().configureCache(
                getTestCacheName(), new MutableConfiguration<Integer, Integer>());
        
        cache.stop();
        try {
            cache.loadAll(null, true, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void loadAll_NullKeys() {
        Cache<Integer, Integer> cache = getCacheManager().configureCache(
                getTestCacheName(), new MutableConfiguration<Integer, Integer>());
        
        try {
            cache.loadAll(null, true, null);
            fail("should have thrown an exception - keys null");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void loadAll_NullKey() throws Exception {
        CacheLoader<Integer, Integer> loader =  new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        HashSet<Integer> keys = new HashSet<Integer>();
        keys.add(null);
        try {
            CompletionListenerFuture future = new CompletionListenerFuture();
            cache.loadAll(keys, true, future);

            future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            assertTrue(future.isDone());

            fail("should have thrown an exception - keys contains null");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }

    @Test
    public void loadAll_NullValue() throws Exception {
        CacheLoader<Integer, Integer> loader = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Map<Integer, Integer> loadAll(Iterable<? extends Integer> keys) {
                Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                for (Integer key : keys) {
                    map.put(key, null);
                }
                return map;
            }
        };
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        HashSet<Integer> keys = new HashSet<Integer>();
        keys.add(1);
        keys.add(2);

        CompletionListenerFuture future = new CompletionListenerFuture();
        cache.loadAll(keys, true, future);

        try {
            future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            assertTrue(future.isDone());

            fail("should have thrown an exception - null value");
        } catch (ExecutionException e) {
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }

    @Test
    public void loadAll_1Found1Not() throws Exception {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer keyThere = 1;
        cache.put(keyThere, keyThere);
        Integer keyNotThere = keyThere + 1;
        HashSet<Integer> keys = new HashSet<Integer>();
        keys.add(keyThere);
        keys.add(keyNotThere);

        CompletionListenerFuture future = new CompletionListenerFuture();
        cache.loadAll(keys, true, future);

        future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
        assertTrue(future.isDone());

        assertEquals(1, loader.getLoadCount());
        assertTrue(loader.hasLoaded(keyNotThere));
        assertEquals(keyThere, cache.get(keyThere));
        assertEquals(keyNotThere, cache.get(keyNotThere));
    }

    @Test
    public void loadAll_NoCacheLoader() throws Exception {
        Cache<Integer, Integer> cache = getCacheManager().configureCache(
                getTestCacheName(), new MutableConfiguration<Integer, Integer>());
        
        HashSet<Integer> keys = new HashSet<Integer>();
        keys.add(1);

        CompletionListenerFuture future = new CompletionListenerFuture();
        try {
            cache.loadAll(keys, true, future);
        } catch (NullPointerException e) {
            assertTrue(future.isDone());
            fail("should not have thrown an exception - with no cache loader should return null");
        }
    }

    @Test
    public void loadAll_DefaultCacheLoader() throws Exception {
        HashSet<Integer> keys = new HashSet<Integer>();
        keys.add(1);
        keys.add(2);

        CacheLoader<Integer, Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));

        CompletionListenerFuture future = new CompletionListenerFuture();
        cache.loadAll(keys, true, future);

        future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
        assertTrue(future.isDone());

        for (Integer key : keys) {
            assertEquals(key, cache.get(key));
        }
    }

    @Test
    public void loadAll_ExceptionPropagation() throws Exception {
        final RuntimeException expectedException = new RuntimeException("expected");
        
        CacheLoader<Integer, Integer> loader = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Map<Integer, Integer> loadAll(Iterable<? extends Integer> keys) {
                throw expectedException;
            }
        };
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        HashSet<Integer> keys = new HashSet<Integer>();
        keys.add(1);

        CompletionListenerFuture future = new CompletionListenerFuture();
        cache.loadAll(keys, true, future);

        try {
            future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            fail("expected exception");
        } catch (ExecutionException e) {
            assertTrue(future.isDone());
            assertEquals(expectedException, e.getCause());
        }
    }

    @Test
    public void get_Stored() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));

        Integer key = 1;
        assertFalse(cache.containsKey(key));
        assertEquals(key, cache.get(key));
        assertTrue(cache.containsKey(key));
    }

    @Test
    public void get_Exception() {
        CacheLoader<Integer, Integer> loader = new MockCacheLoader<Integer, Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));

        Integer key = 1;
        try {
            cache.get(key);
            fail("should have got an exception ");
        } catch (UnsupportedOperationException e) {
            //
        }
    }

    @Test
    public void getAll() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));

        HashSet<Integer> keysToGet = new HashSet<Integer>();
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
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer key = 1;
        assertFalse(cache.containsKey(key));
        assertEquals(key, cache.get(key));
        assertTrue(cache.containsKey(key));
    }

    @Test
    public void putIfAbsent() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer key = 1;
        Integer value = key + 1;
        assertTrue(cache.putIfAbsent(key, value));
        assertEquals(value, cache.get(key));
    }

    @Test
    public void getAndRemove_NotExistent() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer key = 1;
        assertNull(cache.getAndRemove(key));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void getAndRemove_There() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer key = 1;
        Integer value = key + 1;
        cache.put(key, value);
        assertEquals(value, cache.getAndRemove(key));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_3arg_Missing() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer key = 1;
        Integer newValue = key + 1;
        assertFalse(cache.replace(key, key, newValue));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_3arg_Different() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer key = 1;
        Integer value1 = key + 1;
        Integer value2 = value1 + 1;
        Integer value3 = value2 + 1;
        cache.put(key, value1);
        assertFalse(cache.replace(key, value2, value3));
        assertEquals(value1, cache.get(key));
    }

    @Test
    public void replace_3arg() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer key = 1;
        Integer value1 = key + 1;
        Integer value2 = value1 + 1;
        Integer value3 = value2 + 1;
        cache.put(key, value2);
        assertTrue(cache.replace(key, value2, value3));
        assertEquals(value3, cache.get(key));
    }

    @Test
    public void replace_2arg_Missing() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer key = 1;
        assertFalse(cache.replace(key, key));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_2arg() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
        Integer key = 1;
        Integer value1 = key + 1;
        Integer value2 = value1 + 1;
        Integer value3 = value2 + 1;
        cache.put(key, value2);
        assertTrue(cache.replace(key, value3));
        assertEquals(value3, cache.get(key));
    }

    @Test
    public void getAndReplace() {
        SimpleCacheLoader<Integer> loader = new SimpleCacheLoader<Integer>();
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<Integer, Integer>().setCacheLoaderFactory(Factories.of(loader)));
        
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
        //Illegal now with get(K)
        LinkedList<Integer> key2 = new LinkedList<Integer>(key1);

        CacheLoader<ArrayList<Integer>, String> loader = new ArrayListCacheLoader();
        
        Cache<ArrayList<Integer>, String> cache = getCacheManager().configureCache(getTestCacheName(), 
                new MutableConfiguration<ArrayList<Integer>, String>().setCacheLoaderFactory(Factories.of(loader)));

//        String value = cache.get(key2);
//        assertEquals(cacheLoaderFactory.load(key2).getValue(), value);
    }

    // ---------- utilities ----------

    /**
     * A mock CacheLoader which simply throws UnsupportedOperationException on all methods.
     *
     * @param <K>
     * @param <V>
     */
    public static class MockCacheLoader<K, V> implements CacheLoader<K, V> {

        @Override
        public Cache.Entry<K, V> load(K key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<K, V> loadAll(Iterable<? extends K> keys) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * A simple example of a Cache Loader which simply adds the key as the value.
     *
     * @param <K>
     */
    public static class SimpleCacheLoader<K> implements CacheLoader<K, K> {

        /**
         * The keys that have been loaded by this loader.
         */
        private ConcurrentHashMap<K, K> loaded = new ConcurrentHashMap<>();

        /**
         * The number of loads that have occurred.
         */
        private AtomicInteger loadCount = new AtomicInteger(0);

        @Override
        public Cache.Entry<K, K> load(final K key) {
            loaded.put(key, key);

            return new Cache.Entry<K, K>() {
                @Override
                public K getKey() {
                    return key;
                }

                @Override
                public K getValue() {
                    return key;
                }

                @Override
                public <T> T unwrap(Class<T> clazz) {
                    throw new IllegalArgumentException();
                }
            };
        }

        @Override
        public Map<K, K> loadAll(Iterable<? extends K> keys) {
            Map<K, K> map = new HashMap<K, K>();
            for (K key : keys) {
                map.put(key, key);
            }

            loaded.putAll(map);
            loadCount.addAndGet(map.size());

            return map;
        }

        /**
         * Obtain the number of entries that have been loaded.
         *
         * @return the number of entries loaded thus far.
         */
        public int getLoadCount() {
            return loadCount.get();
        }

        /**
         * Determines if the specified key has been loaded by this loader.
         *
         * @param key  the key
         *
         * @return true if the key has been loaded, false otherwise
         */
        public boolean hasLoaded(K key) {
            return loaded.containsKey(key);
        }
    }

    /**
     * A simple example of a Cache Loader
     */
    public static class ArrayListCacheLoader implements CacheLoader<ArrayList<Integer>, String> {

        @Override
        public Cache.Entry<ArrayList<Integer>, String> load(final ArrayList<Integer> key) {
            return new Cache.Entry<ArrayList<Integer>, String>() {
                @Override
                public ArrayList<Integer> getKey() {
                    return new ArrayList<Integer>(key);
                }

                @Override
                public String getValue() {
                    return key.toString();
                }

                @Override
                public <T> T unwrap(Class<T> clazz) {
                    throw new IllegalArgumentException();
                }
            };
        }

        @Override
        public Map<ArrayList<Integer>, String> loadAll(Iterable<? extends ArrayList<Integer>> keys) {
            throw new UnsupportedOperationException();
        }
    }
}

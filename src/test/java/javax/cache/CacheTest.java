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
import org.junit.Test;

import javax.cache.implementation.RICache;
import javax.cache.implementation.RICacheConfiguration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for simple App.
 * <p/>
 * These are very basic tests
 *
 * @author Yannis Cosmadopoulos
 */
public class CacheTest {
    private boolean ignoreNullKeyOnRead;
    private boolean allowNullValue;
    private static long FUTURE_WAIT_MILLIS = 100;

    @Before
    public void setUp() {
        ignoreNullKeyOnRead = isIgnoreNullKeyOnRead();
        allowNullValue = isAllowNullValue();
    }

    @Test
    public void test_get_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.get(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_get_NullKey() {
        Cache<String, Integer> cache = createAndStartCache();
        try {
            assertNull(cache.get(null));
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - null key not allowed");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - null key allowed");
            }
        }
    }

    @Test
    public void test_get_NotExisting() {
        Cache<String, Integer> cache = createAndStartCache();
        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        String key1 = existingKey + "XXX";
        assertNull(cache.get(key1));
    }

    @Test
    public void test_get_Existing() {
        Cache<String, Integer> cache = createAndStartCache();
        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        checkGetExpectation(existingValue, cache, existingKey);
    }

    @Test
    public void test_get_ExistingWithEqualButNonSameKey() {
        Cache<Date, Integer> cache = createAndStartCache();
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        Date newKey = new Date(now);
        assertNotSame(existingKey, newKey);
        checkGetExpectation(existingValue, cache, newKey);
    }

    @Test
    public void test_put_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.put(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_put_NullKey() throws Exception {
        Cache<String, Integer> cache = createAndStartCache();
        try {
            cache.put(null, 1);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_put_NullValue() throws Exception {
        Cache<String, Integer> cache = createAndStartCache();
        try {
            cache.put("key", null);
            if (!allowNullValue) {
                fail("should have thrown an exception - null value not allowed");
            }
        } catch (NullPointerException e) {
            if (allowNullValue) {
                fail("should not have thrown an exception - null value allowed");
            }
        }
    }

    @Test
    public void test_put_ExistingWithEqualButNonSameKey() throws Exception {
        Cache<Date, Integer> cache = createAndStartCache();
        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Integer value1 = 1;
        cache.put(key1, value1);
        Date key2 = new Date(now);
        Integer value2 = value1 + 1;
        cache.put(key2, value2);
        checkGetExpectation(value2, cache, key2);
    }

    @Test
    public void test_remove_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.remove(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_remove_NullKey() throws Exception {
        Cache<String, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.remove(null));
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - null key not allowed");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - null key allowed");
            }
        }
    }

    @Test
    public void test_remove_NotExistent() throws Exception {
        Cache<String, Integer> cache = createAndStartCache();
        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        String keyNotExisting = existingKey + "XXX";
        assertFalse(cache.remove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void test_remove_EqualButNotSameKey() {
        Cache<Date, Integer> cache = createAndStartCache();
        long now = System.currentTimeMillis();

        Date key1 = new Date(now);
        Integer value1 = 1;
        cache.put(key1, value1);

        Date key2 = new Date(now + 1);
        Integer value2 = value1 + 1;
        cache.put(key2, value2);

        assertTrue(cache.remove(key1.clone()));
        assertNull(cache.get(key1));
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void test_getAndRemove_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.getAndRemove(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_getAndRemove_NullKey() throws Exception {
        final Cache<String, Integer> cache = createAndStartCache();
        try {
            assertNull(cache.getAndRemove(null));
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - null key not allowed");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - null key allowed");
            }
        }
    }

    @Test
    public void test_getAndRemove_NotExistent() throws Exception {
        final Cache<String, Integer> cache = createAndStartCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String keyNotExisting = existingKey + "XXX";
        assertNull(cache.getAndRemove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void test_getAndRemove_EqualButNotSameKey() {
        final Cache<Date, Integer> cache = createAndStartCache();
        final long now = System.currentTimeMillis();

        final Date key1 = new Date(now);
        final Integer value1 = 1;
        cache.put(key1, value1);

        final Date key2 = new Date(now + 1);
        final Integer value2 = value1 + 1;
        cache.put(key2, value2);

        assertEquals(value1, cache.getAndRemove(key1.clone()));
        assertNull(cache.get(key1));
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void test_getAll_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.getAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_getAll_Null() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            cache.getAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_getAll_NullKey() {
        Cache<Integer, String> cache = createAndStartCache();
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        keys.add(null);
        keys.add(2);
        try {
            cache.getAll(keys);
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - null key in keys not allowed");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - null key in keys allowed");
            }
        }
    }

    @Test
    public void test_getAll() {
        Cache<Integer, Integer> cache = createAndStartCache();

        ArrayList<Integer> keysInMap = new ArrayList<Integer>();
        keysInMap.add(1);
        keysInMap.add(2);

        ArrayList<Integer> keysToGet = new ArrayList<Integer>();
        keysToGet.add(2);
        keysToGet.add(3);

        Map<Integer, Integer> map = cache.getAll(keysToGet);
        for (Integer key : keysToGet) {
            assertTrue(map.containsKey(key));
            if (keysInMap.contains(key)) {
                assertEquals(cache.get(key), map.get(key));
            } else {
                assertFalse(cache.containsKey(key));
                assertNull(map.get(key));
            }
        }
    }

    @Test
    public void test_containsKey_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.containsKey(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_containsKey_Null() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.containsKey(null));
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - null key not allowed");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - null key allowed");
            }
        }
    }

    @Test
    public void test_containsKey() {
        Cache<Date, Integer> cache = createAndStartCache();
        Map<Date, Integer> data = createData(3);
        for (Map.Entry<Date, Integer> entry : data.entrySet()) {
            assertFalse(cache.containsKey(entry.getKey()));
            cache.put(entry.getKey(), entry.getValue());
            assertTrue(cache.containsKey(entry.getKey().clone()));
        }
        for (Date key : data.keySet()) {
            assertTrue(cache.containsKey(key.clone()));
        }
    }

    @Test
    public void test_load_NotStarted() {
        Cache<Integer, Integer> cache = createCache();
        try {
            cache.load(null, null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_load_NullKey() {
        Cache<Integer, Integer> cache = createAndStartCache();
        CacheLoader<Integer, Integer> cl = new MockCacheLoader<Integer, Integer>();
        try {
            assertNull(cache.load(null, cl, null));
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - null key not allowed");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - null key allowed");
            }
        }
    }

    @Test
    public void test_load_Found() {
        Cache<Integer, Integer> cache = createAndStartCache();
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
    public void test_load_NoCacheLoader() {
        Cache<Integer, Integer> cache = createAndStartCache();
        Integer key = 1;
        try {
            assertNull(cache.load(key, null, null));
        } catch (NullPointerException e) {
            fail("should not have thrown an exception - with no cache loader should return null");
        }
    }

    @Test
    public void test_load_NullValue() throws Exception {
        final Integer valueDefault = null;
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer key, Object arg) {
                return valueDefault;
            }
        };
        Cache<Integer, Integer> cache = createAndStartCache(null, clDefault);
        Integer key = 1;
        Future<Integer> future = cache.load(key, null, null);
        assertNotNull(future);
        try {
            assertEquals(valueDefault, future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS));
            if (allowNullValue) {
                assertTrue(cache.containsKey(key));
                assertEquals(valueDefault, cache.get(key));
            } else {
                fail("should have thrown an exception - null value not allowed");
            }
        } catch (ExecutionException e) {
            if (allowNullValue) {
                fail("should not have thrown an exception - null value allowed");
            } else {
                assertFalse(cache.containsKey(key));
            }
        }
    }

    @Test
    public void test_load_DefaultCacheLoader() throws Exception {
        final Integer valueDefault = 123;
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer key, Object arg) {
                return valueDefault;
            }
        };
        Cache<Integer, Integer> cache = createAndStartCache(null, clDefault);
        Integer key = 1;
        Future<Integer> future = cache.load(key, null, null);
        assertNotNull(future);
        assertEquals(valueDefault, future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS));
        assertTrue(cache.containsKey(key));
        assertEquals(valueDefault, cache.get(key));
    }

    @Test
    public void test_load_BothCacheLoader() throws Exception {
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>();
        final Integer valueSpecific = 123;
        CacheLoader<Integer, Integer> clSpecific = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer key, Object arg) {
                return valueSpecific;
            }
        };
        Cache<Integer, Integer> cache = createAndStartCache(null, clDefault);
        Integer key = 1;
        Future<Integer> future = cache.load(key, clSpecific, null);
        assertNotNull(future);
        assertEquals(valueSpecific, future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS));
        assertTrue(cache.containsKey(key));
        assertEquals(valueSpecific, cache.get(key));
    }

    @Test
    public void test_load_ExceptionPropagation() throws Exception {
        final RuntimeException expectedException = new RuntimeException("expected");
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer key, Object arg) {
                throw expectedException;
            }
        };
        Cache<Integer, Integer> cache = createAndStartCache(null, clDefault);
        Integer key = 1;
        Future<Integer> future = cache.load(key, null, null);
        assertNotNull(future);
        try {
            future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            fail("expected exception: ");
        } catch (ExecutionException e) {
            assertEquals(expectedException, e.getCause());
        }
    }

    @Test
    public void test_loadAll_NotStarted() {
        Cache<Integer, Integer> cache = createCache();
        try {
            cache.loadAll(null, null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_loadAll_NullKeys() {
        Cache<Integer, Integer> cache = createAndStartCache();
        try {
            cache.loadAll(null, null, null);
            fail("should have thrown an exception - keys null");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_loadAll_NullKey() throws Exception {
        final Cache<Integer, Integer> cache = createAndStartCache();
        CacheLoader<Integer, Integer> cl = new SimpleCacheLoader<Integer>();
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(null);
        try {
            Future<Map<Integer, Integer>> future = cache.loadAll(keys, cl, null);
            assertNotNull(future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS));
            assertFalse(cache.containsKey(null));
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - keys contains null");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - keys contains null");
            }
        }
    }

    @Test
    public void test_loadAll_NullValue() throws Exception {
        final Cache<Integer, Integer> cache = createAndStartCache();
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
            if (!allowNullValue) {
                fail("should have thrown an exception - null value");
            } else {
                for (Integer key : keys) {
                    assertTrue(cache.containsKey(key));
                    assertNull(cache.get(key));
                }
            }
        } catch (ExecutionException e) {
            if (allowNullValue) {
                fail("should not have thrown an exception - null value");
            } else {
                assertTrue(e.getCause() instanceof NullPointerException);
            }
        }
    }

    @Test
    public void test_loadAll_1Found1Not() throws Exception {
        Cache<Integer, Integer> cache = createAndStartCache();
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
    public void test_loadAll_NoCacheLoader() throws Exception {
        Cache<Integer, Integer> cache = createAndStartCache();
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        try {
            assertNull(cache.loadAll(keys, null, null));
        } catch (NullPointerException e) {
            fail("should not have thrown an exception - with no cache loader should return null");
        }
    }

    @Test
    public void test_loadAll_DefaultCacheLoader() throws Exception {
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        keys.add(2);
        CacheLoader<Integer, Integer> clDefault = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createAndStartCache(null, clDefault);
        Future<Map<Integer, Integer>> future = cache.loadAll(keys, null, null);
        Map<Integer, Integer> map = future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
        assertEquals(keys.size(), map.size());
        for (Integer key : keys) {
            assertEquals(key, map.get(key));
            assertEquals(key, cache.get(key));
        }
    }

    @Test
    public void test_loadAll_BothCacheLoader() throws Exception {
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>();
        CacheLoader<Integer, Integer> clSpecific = new SimpleCacheLoader<Integer>();
        Cache<Integer, Integer> cache = createAndStartCache(null, clDefault);
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
    public void test_loadAll_ExceptionPropagation() throws Exception {
        final RuntimeException expectedException = new RuntimeException("expected");
        CacheLoader<Integer, Integer> clDefault = new MockCacheLoader<Integer, Integer>() {
            @Override
            public Map<Integer, Integer> loadAll(Collection<? extends Integer> keys, Object arg) {
                throw expectedException;
            }
        };
        Cache<Integer, Integer> cache = createAndStartCache(null, clDefault);
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        Future<Map<Integer, Integer>> future = cache.loadAll(keys, null, null);
        assertNotNull(future);
        try {
            future.get(FUTURE_WAIT_MILLIS, TimeUnit.MILLISECONDS);
            fail("expected exception: ");
        } catch (ExecutionException e) {
            assertEquals(expectedException, e.getCause());
        }
    }

    @Test
    public void test_getCacheStatistics() {
        Cache<Date, Integer> cache = createAndStartCache();
        //TODO: we may need more at some point
        assertNull(cache.getCacheStatistics());
    }

    @Test
    public void test_registerCacheEntryListener() {
        Cache<Date, Integer> cache = createCache();
        cache.registerCacheEntryListener(null, null);
        //TODO: more
    }

    @Test
    public void test_unregisterCacheEntryListener() {
        Cache<Date, Integer> cache = createCache();
        cache.unregisterCacheEntryListener(null);
        //TODO: more
    }

    @Test
    public void test_putAll_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.putAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_putAll_Null() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            cache.putAll(null);
            fail("should have thrown an exception - null map not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_putAll_NullKey() {
        Cache<Date, Integer> cache = createAndStartCache();
        Map<Date, Integer> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put(null, Integer.MAX_VALUE);
        try {
            cache.putAll(data);
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - null key not allowed");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - null key allowed");
            }
        }
        for (Map.Entry<Date, Integer> entry : data.entrySet()) {
            if (entry.getKey() != null) {
                if (!ignoreNullKeyOnRead) {
                    assertNull(cache.get(entry.getKey()));
                } else {
                    checkGetExpectation(entry.getValue(), cache, entry.getKey());
                }
            }
        }
    }

    @Test
    public void test_putAll_NullValue() {
        Cache<Date, Integer> cache = createAndStartCache();
        Map<Date, Integer> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put(new Date(), null);
        try {
            cache.putAll(data);
            if (!allowNullValue) {
                fail("should have thrown an exception - null value not allowed");
            }
        } catch (NullPointerException e) {
            if (allowNullValue) {
                fail("should not have thrown an exception - null value allowed");
            }
        }
        for (Map.Entry<Date, Integer> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                if (!allowNullValue) {
                    assertNull(cache.get(entry.getKey()));
                } else {
                    checkGetExpectation(entry.getValue(), cache, entry.getKey());
                }
            }
        }
    }

    @Test
    public void test_putAll() {
        Cache<Date, Integer> cache = createAndStartCache();
        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        for (Map.Entry<Date, Integer> entry : data.entrySet()) {
            checkGetExpectation(entry.getValue(), cache, entry.getKey());
        }
    }

    @Test
    public void test_putIfAbsent_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.putIfAbsent(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_putIfAbsent_NullKey() throws Exception {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.putIfAbsent(null, 1));
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - null key not allowed");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - null key allowed");
            }
        }
    }

    @Test
    public void test_putIfAbsent_NullValue() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertTrue(cache.putIfAbsent(new Date(), null));
            if (!allowNullValue) {
                fail("should have thrown an exception - null value not allowed");
            }
        } catch (NullPointerException e) {
            if (allowNullValue) {
                fail("should not have thrown an exception - null value allowed");
            }
        }
    }

    @Test
    public void test_putIfAbsent_Missing() {
        Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        assertTrue(cache.putIfAbsent(key, value));
        checkGetExpectation(value, cache, key);
    }

    @Test
    public void test_putIfAbsent_There() {
        Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        Long oldValue = value + 1;
        cache.put(key, oldValue);
        assertFalse(cache.putIfAbsent(key, value));
        checkGetExpectation(oldValue, cache, key);
    }

    @Test
    public void test_replace_3arg_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.replace(null, null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_replace_3arg_NullKey() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.replace(null, 1, 2));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_replace_3arg_NullValue1() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.replace(new Date(), null, 2));
            if (!allowNullValue) {
                fail("should have thrown an exception - null value not allowed");
            }
        } catch (NullPointerException e) {
            if (allowNullValue) {
                fail("should not have thrown an exception - null value allowed");
            }
        }
    }

    @Test
    public void test_replace_3arg_NullValue2() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.replace(new Date(), 1, null));
            if (!allowNullValue) {
                fail("should have thrown an exception - null value not allowed");
            }
        } catch (NullPointerException e) {
            if (allowNullValue) {
                fail("should not have thrown an exception - null value allowed");
            }
        }
    }

    @Test
    public void test_replace_3arg_Missing() {
        Cache<Date, Integer> cache = createAndStartCache();
        assertFalse(cache.replace(new Date(), 1, 2));
    }

    @Test
    public void test_replace_3arg_Different() {
        Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        Long desiredOldValue = value - 1;
        assertFalse(cache.replace(key, desiredOldValue, nextValue));
        assertEquals(value, cache.get(key));
    }

    @Test
    public void test_replace_3arg() throws Exception {
        Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, value, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void test_replace_2arg_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.replace(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_replace_2arg_NullKey() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.replace(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_replace_2arg_NullValue() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.replace(new Date(), null));
            if (!allowNullValue) {
                fail("should have thrown an exception - null value not allowed");
            }
        } catch (NullPointerException e) {
            if (allowNullValue) {
                fail("should not have thrown an exception - null value allowed");
            }
        }
    }

    @Test
    public void test_replace_2arg_Missing() throws Exception {
        Cache<Date, Integer> cache = createAndStartCache();
        assertFalse(cache.replace(new Date(), 1));
    }

    @Test
    public void test_replace_2arg() {
        Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void test_getAndReplace_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.getAndReplace(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_getAndReplace_NullKey() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertNull(cache.getAndReplace(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_getAndReplace_NullValue() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertNull(cache.getAndReplace(new Date(), null));
            if (!allowNullValue) {
                fail("should have thrown an exception - null value not allowed");
            }
        } catch (NullPointerException e) {
            if (allowNullValue) {
                fail("should not have thrown an exception - null value allowed");
            }
        }
    }

    @Test
    public void test_getAndReplace_Missing() {
        Cache<Date, Integer> cache = createAndStartCache();
        assertNull(cache.getAndReplace(new Date(), 1));
    }

    @Test
    public void test_getAndReplace() {
        Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertEquals(value, cache.getAndReplace(key, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void test_removeAll_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.removeAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_removeAll_1arg_Null() {
        Cache<Date, Integer> cache = createAndStartCache();
        try {
            cache.removeAll(null);
            fail("expected NPE");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_removeAll_1arg_NullKey() {
        Cache<Date, Integer> cache = createAndStartCache();
        ArrayList<Date> keys = new ArrayList<Date>();
        keys.add(null);

        try {
            cache.removeAll(keys);
            if (!ignoreNullKeyOnRead) {
                fail("null key");
            }
        } catch (NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("null key");
            }
        }
    }

    @Test
    public void test_removeAll_1arg() {
        Cache<Integer, Integer> cache = createAndStartCache();
        Map<Integer, Integer> data = new HashMap<Integer, Integer>();
        data.put(1, 1);
        data.put(2, 2);
        data.put(3, 3);
        cache.putAll(data);

        data.remove(2);
        cache.removeAll(data.keySet());
        assertFalse(cache.containsKey(1));
        assertEquals(new Integer(2), cache.get(2));
        assertFalse(cache.containsKey(3));
    }

    @Test
    public void test_removeAll() {
        Cache<Date, Integer> cache = createAndStartCache();
        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        cache.removeAll();
        for (Date key : data.keySet()) {
            assertFalse(cache.containsKey(key));
        }
    }

    @Test
    public void test_getConfiguration_Default() {
        Cache<Date, Integer> cache = createCache();
        CacheConfiguration config = cache.getConfiguration();
        // defaults
        assertFalse(config.isReadThrough());
        assertFalse(config.isWriteThrough());
        assertFalse(config.isStoreByValue());
        // is immutable
        try {
            config.setReadThrough(!config.isReadThrough());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
        try {
            config.setWriteThrough(!config.isWriteThrough());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
        try {
            config.setStoreByValue(!config.isStoreByValue());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
    }

    @Test
    public void test_getConfiguration() {
        CacheConfiguration defaultConfig = createCache().getConfiguration();
        CacheConfiguration expectedConfig = new RICacheConfiguration.Builder().
                setReadThrough(!defaultConfig.isReadThrough()).
                setWriteThrough(!defaultConfig.isWriteThrough()).
                setStoreByValue(!defaultConfig.isStoreByValue()).
                build();

        Cache<Date, Integer> cache = createCache(expectedConfig, null);
        CacheConfiguration config = cache.getConfiguration();
        // defaults
        assertEquals(expectedConfig.isReadThrough(), config.isReadThrough());
        assertEquals(expectedConfig.isWriteThrough(), config.isWriteThrough());
        assertEquals(expectedConfig.isStoreByValue(), config.isStoreByValue());
        // is immutable
        try {
            config.setReadThrough(!config.isReadThrough());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
        try {
            config.setWriteThrough(!config.isWriteThrough());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
        try {
            config.setStoreByValue(!config.isStoreByValue());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
    }

    @Test
    public void test_iterator_NotStarted() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.iterator();
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_iterator_Empty() {
        Cache<Date, Integer> cache = createAndStartCache();
        Iterator<Cache.Entry<Date, Integer>> iterator = cache.iterator();
        assertFalse(iterator.hasNext());
        try {
            iterator.remove();
            fail();
        } catch (IllegalStateException e) {
            //good
        }
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException e) {
            //good
        }
    }

    @Test
    public void test_iterator() {
        Cache<Date, Integer> cache = createAndStartCache();
        LinkedHashMap<Date, Integer> data = createData(3);
        cache.putAll(data);
        Iterator<Cache.Entry<Date, Integer>> iterator = cache.iterator();
        while (iterator.hasNext()) {
            Cache.Entry<Date, Integer> next = iterator.next();
            assertEquals(next.getValue(), data.get(next.getKey()));
            iterator.remove();
            data.remove(next.getKey());
        }
        assertTrue(data.isEmpty());
    }

    @Test
    public void test_initialise() {
        Cache<Date, Integer> cache = createCache();
        assertEquals(Status.UNITIALISED, cache.getStatus());
        cache.start();
        assertEquals(Status.STARTED, cache.getStatus());
    }

    @Test
    public void test_stopAndDispose() {
        Cache<Date, Integer> cache = createAndStartCache();
        cache.stop();
        assertEquals(Status.STOPPED, cache.getStatus());
    }

    //TODO: we already have basic tests
//    @Test
//    public void test_getStatus() {
//    }

    // ---------- utilities ----------

    protected boolean isIgnoreNullKeyOnRead() {
        return RICache.DEFAULT_IGNORE_NULL_KEY_ON_READ;
    }

    protected boolean isAllowNullValue() {
        return RICache.DEFAULT_ALLOW_NULL_VALUE;
    }

    /**
     * Creates a cache. Sub classes may override this to create the cache differently.
     *
     * @param config      the cache configuration
     * @param cacheLoader the default cache loader
     * @param <K>         the key type
     * @param <V>         the value type
     * @return a new cache
     */
    protected <K, V> Cache<K, V> createCache(CacheConfiguration config, CacheLoader<K, V> cacheLoader) {
        RICache.Builder<K, V> builder = new RICache.Builder<K, V>();
        if (config != null) {
            builder.setCacheConfiguration(config);
        }
        if (cacheLoader != null) {
            builder.setCacheLoader(cacheLoader);
        }
        return builder.
                setIgnoreNullKeyOnRead(ignoreNullKeyOnRead).
                setAllowNullValue(allowNullValue).
                build();
    }

    // ---------- utilities ----------

    private <K, V> Cache<K, V> createCache() {
        return createCache(null, null);
    }

    private <K, V> Cache<K, V> createAndStartCache() {
        return createAndStartCache(null, null);
    }

    private <K, V> Cache<K, V> createAndStartCache(CacheConfiguration config, CacheLoader<K, V> cacheLoader) {
        Cache<K, V> cache = createCache(config, cacheLoader);
        cache.start();
        return cache;
    }

    private LinkedHashMap<Date, Integer> createData(int count, long now) {
        LinkedHashMap<Date, Integer> map = new LinkedHashMap<Date, Integer>(count);
        for (int i = 0; i < count; i++) {
            map.put(new Date(now + i), i);
        }
        return map;
    }

    private <K, V> void checkGetExpectation(V expected, Cache<K, V> cache, K key) {
        //TODO: at some point we will not (only) store by reference
        assertSame(expected, cache.get(key));
    }

    private LinkedHashMap<Date, Integer> createData(int count) {
        return createData(count, System.currentTimeMillis());
    }

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

    private static class MockCacheLoader<K, V> implements CacheLoader<K, V> {
        public V load(K key, Object arg) {
            throw new UnsupportedOperationException();
        }

        public Map<K, V> loadAll(Collection<? extends K> keys, Object arg) {
            throw new UnsupportedOperationException();
        }
    }
}

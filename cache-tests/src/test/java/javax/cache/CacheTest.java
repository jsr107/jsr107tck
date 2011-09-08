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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.event.CacheEntryReadListener;
import javax.cache.event.NotificationScope;
import javax.cache.util.ExcludeListExcluder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for Cache.
 * <p/>
 * When it matters whether the cache is stored by reference or by value, see {@link CacheStoreByValueTest} and
 * {@link CacheStoreByReferenceTest}.
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheTest extends TestSupport {
    Cache<String, Long> cache;

    @Before
    public void startUp() {
        cache = getCacheManager().<String, Long>createCacheBuilder(CACHE_NAME).build();
    }

    @After
    public void tearDown() {
        if (cache.getStatus() == Status.STARTED) {
            cache.removeAll();
        }
    }

    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule = new ExcludeListExcluder(this.getClass());

    @Test
    public void getCacheName() {
        assertEquals(CACHE_NAME, cache.getName());
    }

    @Test
    public void get_NotStarted() {
        cache.stop();
        try {
            cache.get(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void get_NullKey() {
        try {
            assertNull(cache.get(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void get_NotExisting() {
        String existingKey = "key1";
        Long existingValue = 1L;
        cache.put(existingKey, existingValue);

        String key1 = existingKey + "XXX";
        assertNull(cache.get(key1));
    }

    @Test
    public void get_Existing_ByValue() {
        String existingKey = "key1";
        Long existingValue = 1L;
        cache.put(existingKey, existingValue);
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void put_NotStarted() {
        cache.stop();
        try {
            cache.put(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void put_NullKey() throws Exception {
        try {
            cache.put(null, 1L);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void put_NullValue() throws Exception {
        try {
            cache.put("key", null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }


    @Test
    public void getAndPut_NotStarted() {
        cache.stop();
        try {
            cache.getAndPut(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void getAndPut_NullKey() throws Exception {
        try {
            cache.getAndPut(null, 1L);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndPut_NullValue() throws Exception {
        try {
            cache.getAndPut("key", null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndPut() {
        String key = "key";
        Long value = 1L;
        assertNull(cache.getAndPut(key, value));
        assertEquals(value, cache.get(key));
    }

    @Test
    public void remove_NotStarted() {
        cache.stop();
        try {
            cache.remove(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void remove_NullKey() throws Exception {
        try {
            assertFalse(cache.remove(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void remove_NotExistent() throws Exception {
        String existingKey = "key1";
        Long existingValue = 1L;
        cache.put(existingKey, existingValue);

        String keyNotExisting = existingKey + "XXX";
        assertFalse(cache.remove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void remove_EqualButNotSameKey() {
        long now = System.currentTimeMillis();

        String key1 = "key1";
        Long value1 = 1L;
        cache.put(key1, value1);

        String key2 = "key2";
        Long value2 = value1 + 1;
        cache.put(key2, value2);

        String key3 = new String(key1);
        assertNotSame(key1, key3);
        assertTrue(cache.remove(key3));
        assertNull(cache.get(key1));
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void getAndRemove_NotStarted() {
        cache.stop();
        try {
            cache.getAndRemove(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void getAndRemove_NullKey() throws Exception {
        try {
            assertNull(cache.getAndRemove(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void getAndRemove_NotExistent() throws Exception {
        final String existingKey = "key1";
        final Long existingValue = 1L;
        cache.put(existingKey, existingValue);

        final String keyNotExisting = existingKey + "XXX";
        assertNull(cache.getAndRemove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void getAndRemove_EqualButNotSameKey() {
        final String key1 = "key1";
        final Long value1 = 1L;
        cache.put(key1, value1);

        final String key2 = "key2";
        final Long value2 = value1 + 1;
        cache.put(key2, value2);

        String key3 = new String(key1);
        assertNotSame(key3, key1);
        assertEquals(value1, cache.getAndRemove(key3));
        assertNull(cache.get(key1));
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void getAll_NotStarted() {
        cache.stop();
        try {
            cache.getAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void getAll_Null() {
        try {
            cache.getAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAll_NullKey() {
        ArrayList<String> keys = new ArrayList<String>();
        keys.add("k1");
        keys.add(null);
        keys.add("k2");
        try {
            cache.getAll(keys);
            fail("should have thrown an exception - null key in keys not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void getAll() {
        ArrayList<String> keysInCache = new ArrayList<String>();
        keysInCache.add("1");
        keysInCache.add("2");
        for (String k : keysInCache) {
            cache.put(k, Long.valueOf(k));
        }

        ArrayList<String> keysToGet = new ArrayList<String>();
        keysToGet.add("k2");
        keysToGet.add("k3");

        Map<String, Long> map = cache.getAll(keysToGet);
        assertEquals(keysToGet.size(), map.size());
        for (String key : keysToGet) {
            assertTrue(map.containsKey(key));
            if (keysInCache.contains(key)) {
                assertEquals(cache.get(key), map.get(key));
                assertEquals(Integer.valueOf(key), map.get(key));
            } else {
                assertFalse(cache.containsKey(key));
                assertNull(map.get(key));
            }
        }
    }

    @Test
    public void containsKey_NotStarted() {
        cache.stop();
        try {
            cache.containsKey(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void containsKey_Null() {
        try {
            assertFalse(cache.containsKey(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void containsKey() {
        Map<String, Long> data = createData(3);
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            assertFalse(cache.containsKey(entry.getKey()));
            cache.put(entry.getKey(), entry.getValue());
            assertTrue(cache.containsKey(entry.getKey()));
        }
        for (String key : data.keySet()) {
            assertTrue(cache.containsKey(key));
        }
    }

    @Test
    public void load_NotStarted() {
        cache.stop();
        try {
            cache.load(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void getStatistics() {
        assertNull(cache.getStatistics());
    }

    @Test
    public void getStatistics_NotEnabled() {
        cache.getConfiguration().setStatisticsEnabled(false);
        assertNull(cache.getStatistics());
    }

    @Test
    public void getStatistics_Enabled() {
        cache.getConfiguration().setStatisticsEnabled(true);
        assertNotNull(cache.getStatistics());
    }

    @Test
    public void registerCacheEntryListener() {
        CacheEntryReadListener<String, Long> listener = new MyCacheEntryListener<String, Long>();
        cache.registerCacheEntryListener(listener, NotificationScope.LOCAL, false);
        //TODO: more
        //todo prevent null listener
    }


    @Test
    public void unregisterCacheEntryListener() {
        CacheEntryReadListener<String, Long> listener = new MyCacheEntryListener<String, Long>();
        cache.registerCacheEntryListener(listener, NotificationScope.LOCAL, false);
        cache.unregisterCacheEntryListener(null);
        cache.unregisterCacheEntryListener(listener);

        //TODO: more
    }

    @Test
    public void putAll_NotStarted() {
        cache.stop();
        try {
            cache.putAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void putAll_Null() {
        try {
            cache.putAll(null);
            fail("should have thrown an exception - null map not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void putAll_NullKey() {
        Map<String, Long> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put(null, Long.MAX_VALUE);
        try {
            cache.putAll(data);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            if (entry.getKey() != null) {
                assertNull(cache.get(entry.getKey()));
            }
        }
    }

    @Test
    public void putAll_NullValue() {
        Map<String, Long> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put("key" + System.currentTimeMillis(), null);
        try {
            cache.putAll(data);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void putAll() {
        Map<String, Long> data = createData(3);
        cache.putAll(data);
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            assertEquals(entry.getValue(), cache.get(entry.getKey()));
        }
    }

    @Test
    public void putIfAbsent_NotStarted() {
        cache.stop();
        try {
            cache.putIfAbsent(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void putIfAbsent_NullKey() throws Exception {
        try {
            assertFalse(cache.putIfAbsent(null, 1L));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void putIfAbsent_NullValue() {
        try {
            cache.putIfAbsent("a", null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }


    @Test
    public void replace_3arg_NotStarted() {
        cache.stop();
        try {
            cache.replace(null, null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_NullKey() {
        try {
            assertFalse(cache.replace(null, 1L, 2L));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_NullValue1() {
        try {
            assertFalse(cache.replace("key", null, 2L));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_NullValue2() {
        try {
            assertFalse(cache.replace("key", 1L, null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_Missing() {
        String key = "key";
        assertFalse(cache.replace(key, 1L, 2L));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_3arg_Different() {
        String key = "key";
        Long value = 1L;
        cache.put(key, value);
        Long nextValue = value + 1;
        Long desiredOldValue = value - 1;
        assertFalse(cache.replace(key, desiredOldValue, nextValue));
        assertEquals(value, cache.get(key));
    }

    @Test
    public void replace_2arg_NotStarted() {
        cache.stop();
        try {
            cache.replace(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void replace_2arg_NullKey() {
        try {
            assertFalse(cache.replace(null, 1L));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_2arg_NullValue() {
        try {
            assertFalse(cache.replace("key", null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_2arg_Missing() throws Exception {
        String key = "key";
        assertFalse(cache.replace(key, 1L));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_2arg() {
        String key = "key";
        Long value = 1L;
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void getAndReplace_NotStarted() {
        cache.stop();
        try {
            cache.getAndReplace(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void getAndReplace_NullKey() {
        try {
            assertNull(cache.getAndReplace(null, 1L));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndReplace_NullValue() {
        try {
            assertNull(cache.getAndReplace("key", null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndReplace_Missing() {
        String key = "key";
        assertNull(cache.getAndReplace(key, 1L));
        assertFalse(cache.containsKey(key));
    }


    @Test
    public void removeAll_NotStarted() {
        cache.stop();
        try {
            cache.removeAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void removeAll_1arg_Null() {
        try {
            cache.removeAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void removeAll_1arg_NullKey() {
        ArrayList<String> keys = new ArrayList<String>();
        keys.add(null);

        try {
            cache.removeAll(keys);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void removeAll_1arg() {
        Map<String, Long> data = new HashMap<String, Long>();
        data.put("key1", 1L);
        data.put("key2", 2L);
        data.put("key3", 3L);
        cache.putAll(data);

        data.remove("key2");
        cache.removeAll(data.keySet());
        assertFalse(cache.containsKey("key1"));
        assertEquals(new Long(2), cache.get("key2"));
        assertFalse(cache.containsKey("key3"));
    }

    @Test
    public void removeAll() {
        Map<String, Long> data = createData(3);
        cache.putAll(data);
        cache.removeAll();
        for (String key : data.keySet()) {
            assertFalse(cache.containsKey(key));
        }
    }

    @Test
    public void getConfiguration_Mutation() {
        CacheConfiguration config = cache.getConfiguration();

        boolean enabled = config.isStatisticsEnabled();
        assertEquals(enabled, config.isStatisticsEnabled());
        config.setStatisticsEnabled(!enabled);
        assertEquals(!enabled, config.isStatisticsEnabled());
        assertEquals(!enabled, cache.getConfiguration().isStatisticsEnabled());
    }

    @Test
    public void iterator_NotStarted() {
        cache.stop();
        try {
            cache.iterator();
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void iterator_Empty() {
        Iterator<Cache.Entry<String, Long>> iterator = cache.iterator();
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
    public void simpleAPI() {
        String cacheName = "sampleCache";
        CacheManager cacheManager = getCacheManager();
        Cache<String, Integer> cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            cache = cacheManager.<String, Integer>createCacheBuilder(cacheName).build();
        }
        String key = "key";
        Integer value1 = 1;
        cache.put(key, value1);
        Integer value2 = cache.get(key);
        assertEquals(value1, value2);
    }

    @Test
    public void iterator() {
        LinkedHashMap<String, Long> data = createData(3);
        cache.putAll(data);
        Iterator<Cache.Entry<String, Long>> iterator = cache.iterator();
        while (iterator.hasNext()) {
            Cache.Entry<String, Long> next = iterator.next();
            assertEquals(next.getValue(), data.get(next.getKey()));
            iterator.remove();
            data.remove(next.getKey());
        }
        assertTrue(data.isEmpty());
    }

    @Test
    public void initialise() {
        assertEquals(Status.STARTED, cache.getStatus());
    }

    @Test
    public void stop() {
        cache.stop();
        assertEquals(Status.STOPPED, cache.getStatus());
    }

    // ---------- utilities ----------

    private LinkedHashMap<String, Long> createData(int count, long now) {
        LinkedHashMap<String, Long> map = new LinkedHashMap<String, Long>(count);
        for (int i = 0; i < count; i++) {
            map.put("key" + (now + i), now + i);
        }
        return map;
    }

    private LinkedHashMap<String, Long> createData(int count) {
        return createData(count, System.currentTimeMillis());
    }

    /**
     * Test listener
     * @param <K>
     * @param <V>
     */
    static class MyCacheEntryListener<K, V> implements CacheEntryReadListener<K, V> {


        /**
         * Called after the entry has been read. If no entry existed for the key the event is not called.
         * This method is not called if a batch operation was performed.
         *
         * @param entry The entry just read.
         * @see #onReadAll(Iterable)
         */
        @Override
        public void onRead(Cache.Entry<K, V> entry) {
            //noop
        }

        /**
         * Called after the entries have been read. Only entries which existed in the cache are passed in.
         *
         * @param entries The entry just read.
         */
        @Override
        public void onReadAll(Iterable<Cache.Entry<K, V>> entries) {
            //noop
        }

        /**
         * @return the notification scope for this listener
         */
        @Override
        public NotificationScope getNotificationScope() {
            return NotificationScope.LOCAL;
        }
    }
}

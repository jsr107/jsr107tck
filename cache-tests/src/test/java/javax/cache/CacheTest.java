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
import sun.util.resources.LocaleNames_ko;

import javax.cache.event.CacheEntryReadListener;
import javax.cache.event.NotificationScope;
import javax.cache.util.ExcludeListExcluder;
import java.util.ArrayList;
import java.util.Date;
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
    /**
     * the default test cache name
     */
    private static final String CACHE_NAME = CacheTest.class.getName();

    Cache<Long, String> cache;

    @Before
    public void setUp() {
        cache = getCacheManager().<Long, String>createCacheBuilder(CACHE_NAME).build();
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
        Long existingKey = System.currentTimeMillis();
        String existingValue = "value" + existingKey;
        cache.put(existingKey, existingValue);

        Long key1 = existingKey + 1;
        assertNull(cache.get(key1));
    }

    @Test
    public void get_Existing() {
        Long existingKey = System.currentTimeMillis();
        String existingValue = "value" + existingKey;
        cache.put(existingKey, existingValue);
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void get_Existing_NotSameKey() {
        Long existingKey = System.currentTimeMillis();
        String existingValue = "value" + existingKey;
        cache.put(existingKey, existingValue);
        assertEquals(existingValue, cache.get(new Long(existingKey)));
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
            cache.put(null, "");
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void put_NullValue() throws Exception {
        try {
            cache.put(1L, null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void put_Existing_NotSameKey() throws Exception {
        Long key1 = System.currentTimeMillis();
        String value1 = "value" + key1;
        cache.put(key1, value1);
        Long key2 = new Long(key1);
        String value2 = "value" + key2;
        cache.put(key2, value2);
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void put_Existing_DifferentKey() throws Exception {
        Long key1 = System.currentTimeMillis();
        String value1 = "value" + key1;
        cache.put(key1, value1);
        Long key2 = key1 + 1;
        String value2 = "value" + key2;
        cache.put(key2, value2);
        assertEquals(value2, cache.get(key2));
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
            cache.getAndPut(null, "");
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndPut_NullValue() throws Exception {
        try {
            cache.getAndPut(1L, null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndPut_NotThere() {
        Long existingKey = System.currentTimeMillis();
        String existingValue = "value" + existingKey;
        assertNull(cache.getAndPut(existingKey, existingValue));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void getAndPut_Existing() throws Exception {
        Long existingKey = System.currentTimeMillis();
        String value1 = "value1";
        cache.getAndPut(existingKey, value1);
        String value2 = "value2";
        assertEquals(value1, cache.getAndPut(existingKey, value2));
        assertEquals(value2, cache.get(existingKey));
    }

    @Test
    public void getAndPut_Existing_NonSameKey() throws Exception {
        Long key1 = System.currentTimeMillis();
        String value1 = "value1";
        assertNull(cache.getAndPut(key1, value1));
        Long key2 = new Long(key1);
        String value2 = "value2";
        assertEquals(value1, cache.getAndPut(key2, value2));
        assertEquals(value2, cache.get(key1));
        assertEquals(value2, cache.get(key2));
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
        Long existingKey = System.currentTimeMillis();
        String existingValue = "value" + existingKey;
        cache.put(existingKey, existingValue);

        Long keyNotExisting = existingKey + 1;
        assertFalse(cache.remove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void remove_Existing() {
        Long key1 = System.currentTimeMillis();
        String value1 = "value" + key1;
        cache.put(key1, value1);

        Long key2 = key1 + 1;
        String value2 = "value" + key2;
        cache.put(key2, value2);

        assertTrue(cache.remove(key1));
        assertFalse(cache.containsKey(key1));
        assertNull(cache.get(key1));
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void remove_EqualButNotSameKey() {
        Long key1 = System.currentTimeMillis();
        String value1 = "value" + key1;
        cache.put(key1, value1);

        Long key2 = key1 + 1;
        String value2 = "value" + key2;
        cache.put(key2, value2);

        Long key3 = new Long(key1);
        assertNotSame(key1, key3);
        assertTrue(cache.remove(key3));
        assertFalse(cache.containsKey(key1));
        assertNull(cache.get(key1));
        assertFalse(cache.containsKey(key3));
        assertNull(cache.get(key3));
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void remove_2arg_NotStarted() {
        cache.stop();
        try {
            cache.remove(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void remove_2arg_NullKey() {
        try {
            cache.remove(null, "");
            fail("should have thrown an exception - null key");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void remove_2arg_NullValue() {
        try {
            cache.remove(1L, null);
            fail("should have thrown an exception - null value");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void remove_2arg_NotThere() {
        Long key = System.currentTimeMillis();
        assertFalse(cache.remove(key, ""));
    }

    @Test
    public void remove_2arg_Existing_SameValue() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        assertTrue(cache.remove(key, value));
    }

    @Test
    public void remove_2arg_Existing_EqualValue() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        assertTrue(cache.remove(key, new String(value)));
    }

    @Test
    public void remove_2arg_Existing_EqualKey() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        assertTrue(cache.remove(new Long(key), value));
    }

    @Test
    public void remove_2arg_Existing_EqualKey_EqualValue() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        assertTrue(cache.remove(new Long(key), new String(value)));
    }

    @Test
    public void remove_2arg_Existing_Different() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        assertFalse(cache.remove(key, value + 1));
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
        Long existingKey = System.currentTimeMillis();
        String existingValue = "value" + existingKey;
        cache.put(existingKey, existingValue);

        Long keyNotExisting = existingKey + 1;
        assertNull(cache.getAndRemove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void getAndRemove_Existing() {
        Long key1 = System.currentTimeMillis();
        String value1 = "value" + key1;
        cache.put(key1, value1);

        Long key2 = key1 + 1;
        String value2 = "value" + key2;
        cache.put(key2, value2);

        assertEquals(value1, cache.getAndRemove(key1));
        assertFalse(cache.containsKey(key1));
        assertNull(cache.get(key1));
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void getAndRemove_EqualButNotSameKey() {
        Long key1 = System.currentTimeMillis();
        String value1 = "value" + key1;
        cache.put(key1, value1);

        Long key2 = key1 + 1;
        String value2 = "value" + key2;
        cache.put(key2, value2);

        Long key3 = new Long(key1);
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
        ArrayList<Long> keys = new ArrayList<Long>();
        keys.add(1L);
        keys.add(null);
        keys.add(2L);
        try {
            cache.getAll(keys);
            fail("should have thrown an exception - null key in keys not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void getAll() {
        ArrayList<Long> keysInCache = new ArrayList<Long>();
        keysInCache.add(1L);
        keysInCache.add(2L);
        for (Long k : keysInCache) {
            cache.put(k, "value" + k);
        }

        ArrayList<Long> keysToGet = new ArrayList<Long>();
        keysToGet.add(2L);
        keysToGet.add(3L);

        Map<Long, String> map = cache.getAll(keysToGet);
        assertEquals(keysToGet.size(), map.size());
        for (Long key : keysToGet) {
            assertTrue(map.containsKey(key));
            if (keysInCache.contains(key)) {
                assertEquals(cache.get(key), map.get(key));
                assertEquals("value" + key, map.get(key));
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
        Map<Long, String> data = createData(3);
        for (Map.Entry<Long, String> entry : data.entrySet()) {
            assertFalse(cache.containsKey(entry.getKey()));
            cache.put(entry.getKey(), entry.getValue());
            assertTrue(cache.containsKey(entry.getKey()));
        }
        for (Long key : data.keySet()) {
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
        CacheEntryReadListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
        cache.registerCacheEntryListener(listener, NotificationScope.LOCAL, false);
        //TODO: more
        //todo prevent null listener
    }


    @Test
    public void unregisterCacheEntryListener() {
        CacheEntryReadListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
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
        Map<Long, String> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put(null, "");
        try {
            cache.putAll(data);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
        for (Map.Entry<Long, String> entry : data.entrySet()) {
            if (entry.getKey() != null) {
                assertNull(cache.get(entry.getKey()));
            }
        }
    }

    @Test
    public void putAll_NullValue() {
        Map<Long, String> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put(System.currentTimeMillis(), null);
        try {
            cache.putAll(data);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void putAll() {
        Map<Long, String> data = createData(3);
        cache.putAll(data);
        for (Map.Entry<Long, String> entry : data.entrySet()) {
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
            assertFalse(cache.putIfAbsent(null, ""));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void putIfAbsent_NullValue() {
        try {
            cache.putIfAbsent(1L, null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void putIfAbsent_Missing() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        assertTrue(cache.putIfAbsent(key, value));
        assertEquals(value, cache.get(key));
    }

    @Test
    public void putIfAbsent_There() {
        Long key = System.currentTimeMillis();
        String value = "valueA" + key;
        String oldValue = "valueB" + key;
        cache.put(key, oldValue);
        assertFalse(cache.putIfAbsent(key, value));
        assertEquals(oldValue, cache.get(key));
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
            assertFalse(cache.replace(null, "1", "2"));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_NullValue1() {
        try {
            assertFalse(cache.replace(1L, null, "2"));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_NullValue2() {
        try {
            assertFalse(cache.replace(1L, "1", null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_Missing() {
        Long key = System.currentTimeMillis();
        assertFalse(cache.replace(key, "1", "2"));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_3arg() throws Exception {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        String nextValue = "value" + key + 1;
        assertTrue(cache.replace(key, value, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void replace_3arg_Equal() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        String nextValue = "value" + key + 1;
        assertTrue(cache.replace(new Long(key), new String(value), new String(nextValue)));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void replace_3arg_Different() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        String nextValue = "valueN" + key;
        String desiredOldValue = "valueB" + key;
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
            assertFalse(cache.replace(null, ""));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_2arg_NullValue() {
        try {
            assertFalse(cache.replace(1L, null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_2arg_Missing() throws Exception {
        Long key = System.currentTimeMillis();
        assertFalse(cache.replace(key, ""));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_2arg() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        String nextValue = "valueA" + key;
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
            assertNull(cache.getAndReplace(null, ""));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndReplace_NullValue() {
        try {
            assertNull(cache.getAndReplace(1L, null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndReplace_Missing() {
        Long key = System.currentTimeMillis();
        assertNull(cache.getAndReplace(key, ""));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void getAndReplace() {
        Long key = System.currentTimeMillis();
        String value = "value" + key;
        cache.put(key, value);
        String nextValue = "valueB" + key;
        assertEquals(value, cache.getAndReplace(key, nextValue));
        assertEquals(nextValue, cache.get(key));
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
        ArrayList<Long> keys = new ArrayList<Long>();
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
        Map<Long, String> data = createData(3);
        cache.putAll(data);

        Iterator<Map.Entry<Long, String>> it = data.entrySet().iterator();
        it.next();
        Map.Entry removedEntry = it.next();
        it.remove();

        cache.removeAll(data.keySet());
        for (Long key : data.keySet()) {
            assertFalse(cache.containsKey(key));
        }
        assertEquals(removedEntry.getValue(), cache.get(removedEntry.getKey()));
    }

    @Test
    public void removeAll() {
        Map<Long, String> data = createData(3);
        cache.putAll(data);
        cache.removeAll();
        for (Long key : data.keySet()) {
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
        Iterator<Cache.Entry<Long, String>> iterator = cache.iterator();
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
    public void iterator() {
        LinkedHashMap<Long, String> data = createData(3);
        cache.putAll(data);
        Iterator<Cache.Entry<Long, String>> iterator = cache.iterator();
        while (iterator.hasNext()) {
            Cache.Entry<Long, String> next = iterator.next();
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

    private LinkedHashMap<Long, String> createData(int count, long now) {
        LinkedHashMap<Long, String> map = new LinkedHashMap<Long, String>(count);
        for (int i = 0; i < count; i++) {
            Long key = now + i;
            map.put(key, "value" + key);
        }
        return map;
    }

    private LinkedHashMap<Long, String> createData(int count) {
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

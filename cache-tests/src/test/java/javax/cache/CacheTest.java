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
 * Unit test for Cache.
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheTest extends TestSupport {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule = new ExcludeListExcluder(this.getClass());

    @Test
    public void getCacheName() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
        assertEquals(CACHE_NAME, cache.getName());
    }

    @Test
    public void get_NotStarted() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertNull(cache.get(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void get_NotExisting() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();

        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        String key1 = existingKey + "XXX";
        assertNull(cache.get(key1));
    }

    @Test
    public void get_Existing_ByValue() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();

        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        checkGetExpectation(existingValue, cache, existingKey);
    }

    @Test
    public void get_ExistingWithEqualButNonSameKey_ByValue() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();

        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        Date newKey = new Date(now);
        assertNotSame(existingKey, newKey);
        checkGetExpectation(existingValue, cache, newKey);
    }

    @Test
    public void test_ExistingWithMutableKey_ByValue() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();

        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Date key2 = new Date(now);
        Integer existingValue = 1;
        cache.put(key1, existingValue);
        long later = now + 5;
        key1.setTime(later);
        assertNull(cache.get(key1));
        checkGetExpectation(existingValue, cache, key2);
    }

    @Test
    public void put_NotStarted() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            cache.put(null, 1);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void put_NullValue() throws Exception {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            cache.put("key", null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void put_ExistingWithEqualButNonSameKey_ByValue() throws Exception {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();

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
    public void put_Mutable_ByValue() {
        Cache<Integer, Date> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Integer, Date>createCacheBuilder(CACHE_NAME).build();
        long time1 = System.currentTimeMillis();
        Date value1 = new Date(time1);
        Integer key = 1;
        cache.put(key, value1);
        long time2 = time1 + 5;
        value1.setTime(time2);
        Date value2 = cache.get(key);
        assertNotSame(value1, value2);
        assertEquals(time2, value1.getTime());
        assertEquals(time1, value2.getTime());
    }

    @Test
    public void getAndPut_NotStarted() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            cache.getAndPut(null, 1);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndPut_NullValue() throws Exception {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            cache.getAndPut("key", null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndPut_ExistingWithEqualButNonSameKey_ByValue() throws Exception {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();

        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Integer value1 = 1;
        assertNull(cache.getAndPut(key1, value1));
        Date key2 = new Date(now);
        Integer value2 = value1 + 1;
        assertEquals(value1, cache.getAndPut(key2, value2));
        checkGetExpectation(value2, cache, key2);
    }

    @Test
    public void getAndPut_Mutable_ByValue() {
        Cache<Long, Date> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Long, Date>createCacheBuilder(CACHE_NAME).build();

        long key = System.currentTimeMillis();
        Date value = new Date(key);
        Date valueOriginal = new Date(key);
        assertNull(cache.getAndPut(key, value));
        value.setTime(key + 1);
        assertEquals(valueOriginal, cache.get(key));
    }

    @Test
    public void remove_NotStarted() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertFalse(cache.remove(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void remove_NotExistent() throws Exception {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();

        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        String keyNotExisting = existingKey + "XXX";
        assertFalse(cache.remove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void remove_EqualButNotSameKey() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();

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
    public void getAndRemove_NotStarted() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
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
        final Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertNull(cache.getAndRemove(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void getAndRemove_NotExistent() throws Exception {
        final Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();

        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String keyNotExisting = existingKey + "XXX";
        assertNull(cache.getAndRemove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void getAndRemove_EqualButNotSameKey() {
        final Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
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
    public void getAndRemove_ByValue() {
        final Cache<Long, Date> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Long, Date>createCacheBuilder(CACHE_NAME).build();

        final Long key = System.currentTimeMillis();
        final Date value = new Date(key);
        final Date valueOriginal = new Date(key);
        cache.put(key, value);
        value.setTime(key + 1);

        assertEquals(valueOriginal, cache.getAndRemove(key));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void getAll_NotStarted() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            cache.getAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAll_NullKey() {
        Cache<Integer, String> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Integer, String>createCacheBuilder(CACHE_NAME).build();
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        keys.add(null);
        keys.add(2);
        try {
            cache.getAll(keys);
            fail("should have thrown an exception - null key in keys not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void getAll() {
        Cache<Integer, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Integer, Integer>createCacheBuilder(CACHE_NAME).build();

        ArrayList<Integer> keysInCache = new ArrayList<Integer>();
        keysInCache.add(1);
        keysInCache.add(2);
        for (Integer k : keysInCache) {
            cache.put(k, k);
        }

        ArrayList<Integer> keysToGet = new ArrayList<Integer>();
        keysToGet.add(2);
        keysToGet.add(3);

        Map<Integer, Integer> map = cache.getAll(keysToGet);
        assertEquals(keysToGet.size(), map.size());
        for (Integer key : keysToGet) {
            assertTrue(map.containsKey(key));
            if (keysInCache.contains(key)) {
                assertEquals(cache.get(key), map.get(key));
                assertEquals(key, map.get(key));
            } else {
                assertFalse(cache.containsKey(key));
                assertNull(map.get(key));
            }
        }
    }

    @Test
    public void containsKey_NotStarted() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertFalse(cache.containsKey(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void containsKey() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
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
    public void load_NotStarted() {
        Cache<Integer, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Integer, Integer>createCacheBuilder(CACHE_NAME).build();
        cache.stop();
        try {
            cache.load(null, null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void getCacheStatistics() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        assertNull(cache.getStatistics());
    }

    @Test
    public void getStatistics_NotEnabled() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        CacheConfiguration config = cacheManager.createCacheConfiguration();
        config.setStatisticsEnabled(false);
        Cache<Date, Integer> cache = cacheManager.<Date, Integer>createCacheBuilder(CACHE_NAME).
                setCacheConfiguration(config).build();
        assertNull(cache.getStatistics());
    }

    /**
     * Inline example of creating a cache
     */
    @Test
    public void createCacheWithConfiguration() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        CacheConfiguration cacheConfiguration = cacheManager.createCacheConfiguration();
        cacheConfiguration.setReadThrough(false);
        Cache testCache = cacheManager.createCacheBuilder(CACHE_NAME).setCacheConfiguration(cacheConfiguration).build();
        assertNotNull(testCache);
    }

    @Test
    public void getStatistics_Enabled() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        CacheConfiguration config = cacheManager.createCacheConfiguration();
        config.setStatisticsEnabled(true);
        Cache<Date, Integer> cache = cacheManager.<Date, Integer>createCacheBuilder(CACHE_NAME)
                .setCacheConfiguration(config).build();
        assertNotNull(cache.getStatistics());
    }

    @Test
    public void registerCacheEntryListener() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        CacheEntryReadListener<Date, Integer> listener = new MyCacheEntryListener<Date, Integer>();
        cache.registerCacheEntryListener(listener, NotificationScope.LOCAL, false);
        //TODO: more
        //todo prevent null listener
    }





    @Test
    public void unregisterCacheEntryListener() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        CacheEntryReadListener<Date, Integer> listener = new MyCacheEntryListener<Date, Integer>();
        cache.registerCacheEntryListener(listener, NotificationScope.LOCAL, false);
        cache.unregisterCacheEntryListener(null);
        cache.unregisterCacheEntryListener(listener);

        //TODO: more
    }

    @Test
    public void putAll_NotStarted() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            cache.putAll(null);
            fail("should have thrown an exception - null map not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void putAll_NullKey() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        Map<Date, Integer> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put(null, Integer.MAX_VALUE);
        try {
            cache.putAll(data);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
        for (Map.Entry<Date, Integer> entry : data.entrySet()) {
            if (entry.getKey() != null) {
                assertNull(cache.get(entry.getKey()));
            }
        }
    }

    @Test
    public void putAll_NullValue() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        Map<Date, Integer> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put(new Date(), null);
        try {
            cache.putAll(data);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void putAll_ByValue() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        for (Map.Entry<Date, Integer> entry : data.entrySet()) {
            checkGetExpectation(entry.getValue(), cache, entry.getKey());
        }
    }

    @Test
    public void putIfAbsent_NotStarted() {
        Cache<String, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <String, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertFalse(cache.putIfAbsent(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void putIfAbsent_NullValue() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            cache.putIfAbsent(new Date(), null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void putIfAbsent_Missing_ByValue() {
        Cache<Date, Long> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Long>createCacheBuilder(CACHE_NAME).build();

        Date key = new Date();
        Long value = key.getTime();
        assertTrue(cache.putIfAbsent(key, value));
        checkGetExpectation(value, cache, key);
    }

    @Test
    public void putIfAbsent_There_ByValue() {
        Cache<Date, Long> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Long>createCacheBuilder(CACHE_NAME).build();

        Date key = new Date();
        Long value = key.getTime();
        Long oldValue = value + 1;
        cache.put(key, oldValue);
        assertFalse(cache.putIfAbsent(key, value));
        checkGetExpectation(oldValue, cache, key);
    }

    @Test
    public void replace_3arg_NotStarted() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertFalse(cache.replace(null, 1, 2));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_NullValue1() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertFalse(cache.replace(new Date(), null, 2));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_NullValue2() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertFalse(cache.replace(new Date(), 1, null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_Missing() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        Date key = new Date();
        assertFalse(cache.replace(key, 1, 2));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_3arg_Different() {
        Cache<Date, Long> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Long>createCacheBuilder(CACHE_NAME).build();

        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        Long desiredOldValue = value - 1;
        assertFalse(cache.replace(key, desiredOldValue, nextValue));
        assertEquals(value, cache.get(key));
    }

    @Test
    public void replace_3arg_ByValue() throws Exception {
        Cache<Date, Long> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Long>createCacheBuilder(CACHE_NAME).build();

        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, value, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void replace_2arg_NotStarted() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertFalse(cache.replace(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_2arg_NullValue() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertFalse(cache.replace(new Date(), null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_2arg_Missing() throws Exception {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        Date key = new Date();
        assertFalse(cache.replace(key, 1));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_2arg() {
        Cache<Date, Long> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Long>createCacheBuilder(CACHE_NAME).build();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void getAndReplace_NotStarted() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertNull(cache.getAndReplace(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndReplace_NullValue() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            assertNull(cache.getAndReplace(new Date(), null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndReplace_Missing() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        Date key = new Date();
        assertNull(cache.getAndReplace(key, 1));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void getAndReplace_ByValue() {
        Cache<Long, Date> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Long, Date>createCacheBuilder(CACHE_NAME).build();

        Long key = System.currentTimeMillis();
        Date value = new Date(key);
        Date valueOriginal = new Date(key);
        cache.put(key, value);
        Date nextValue = new Date(key + 1);
        value.setTime(key + 5);
        assertEquals(valueOriginal, cache.getAndReplace(key, nextValue));
        checkGetExpectation(nextValue, cache, key);
    }

    @Test
    public void removeAll_NotStarted() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        try {
            cache.removeAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void removeAll_1arg_NullKey() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        ArrayList<Date> keys = new ArrayList<Date>();
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
        Cache<Integer, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Integer, Integer>createCacheBuilder(CACHE_NAME).build();
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
    public void removeAll() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        cache.removeAll();
        for (Date key : data.keySet()) {
            assertFalse(cache.containsKey(key));
        }
    }

    @Test
    public void getConfiguration_Default() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        Cache<Date, Integer> cache = cacheManager.<Date, Integer>createCacheBuilder(CACHE_NAME).build();
        CacheConfiguration config = cache.getConfiguration();
        // defaults
        CacheConfiguration defaultConfig = cacheManager.createCacheConfiguration();
        assertEquals(defaultConfig, config);
    }

    @Test
    public void getConfiguration_SuppliedInConstructor() {
        String cacheName = CACHE_NAME + "XXX";
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        CacheConfiguration defaultConfig = cacheManager.createCacheConfiguration();
        CacheConfiguration expectedConfig = cacheManager.createCacheConfiguration();
        expectedConfig.setReadThrough(!defaultConfig.isReadThrough());
        expectedConfig.setWriteThrough(!defaultConfig.isWriteThrough());
        expectedConfig.setStoreByValue(!defaultConfig.isStoreByValue());
        expectedConfig.setTransactionEnabled(!defaultConfig.isTransactionEnabled());

        Cache<Date, Integer> cache = cacheManager.
                <Date, Integer>createCacheBuilder(cacheName).
                setCacheConfiguration(expectedConfig).
                build();

        CacheConfiguration config = cache.getConfiguration();
        // defaults
        assertEquals(expectedConfig.isReadThrough(), config.isReadThrough());
        assertEquals(expectedConfig.isWriteThrough(), config.isWriteThrough());
        assertEquals(expectedConfig.isStoreByValue(), config.isStoreByValue());
        assertEquals(expectedConfig.isTransactionEnabled(), config.isTransactionEnabled());
        try {
            config.setStoreByValue(!config.isStoreByValue());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
        try {
            config.setTransactionEnabled(!config.isTransactionEnabled());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
    }

    @Test
    public void getConfiguration_Mutation() {
        String cacheName = CACHE_NAME + "YYY";

        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(cacheName).
                build();

        CacheConfiguration config = cache.getConfiguration();

        try {
            config.setStoreByValue(!config.isStoreByValue());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
        try {
            config.setTransactionEnabled(!config.isTransactionEnabled());
            fail("immutable");
        } catch (UnsupportedOperationException e) {
            //good
        }
        boolean enabled = config.isStatisticsEnabled();
        assertEquals(enabled, config.isStatisticsEnabled());
        config.setStatisticsEnabled(!enabled);
        assertEquals(!enabled, config.isStatisticsEnabled());
        assertEquals(!enabled, cache.getConfiguration().isStatisticsEnabled());
    }

    @Test
    public void iterator_NotStarted() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
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
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
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
    public void simpleAPI() {
        String cacheName = "sampleCache";
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        Cache<Integer, Date> cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            cache = cacheManager.<Integer,Date>createCacheBuilder(cacheName).build();
        }
        Date value1 = new Date();
        Integer key = 1;
        cache.put(key, value1);
        Date value2 = cache.get(key);
        assertEquals(value1, value2);
    }

    @Test
    public void iterator() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
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
    public void initialise() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        assertEquals(CacheStatus.STARTED, cache.getStatus());
    }

    @Test
    public void stop() {
        Cache<Date, Integer> cache = CacheManagerFactory.INSTANCE.getCacheManager().
                <Date, Integer>createCacheBuilder(CACHE_NAME).build();
        cache.stop();
        assertEquals(CacheStatus.STOPPED, cache.getStatus());
    }

    // ---------- utilities ----------

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

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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for Cache.
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 */
public class CacheTest extends TestSupport {

    @Test
    public void getCacheName() {
        Cache<String, Integer> cache = createCache();
        assertEquals(CACHE_NAME, cache.getCacheName());
    }

    @Test
    public void get_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<String, Integer> cache = createCache();
        try {
            assertNull(cache.get(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void get_NotExisting() {
        Cache<String, Integer> cache = createCache();

        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        String key1 = existingKey + "XXX";
        assertNull(cache.get(key1));
    }

    @Test
    public void get_Existing_ByValue() {
        Cache<String, Integer> cache = createByValueCache();

        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        checkGetExpectation(existingValue, cache, existingKey);
    }

    @Test
    public void get_Existing_ByReference() {
        Cache<String, Integer> cache = createByReferenceCache();
        if (cache == null) return;

        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        checkGetExpectation(existingValue, cache, existingKey);
    }

    @Test
    public void get_ExistingWithEqualButNonSameKey_ByValue() {
        Cache<Date, Integer> cache = createByValueCache();

        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        Date newKey = new Date(now);
        assertNotSame(existingKey, newKey);
        checkGetExpectation(existingValue, cache, newKey);
    }

    @Test
    public void get_ExistingWithEqualButNonSameKey_ByReference() {
        Cache<Date, Integer> cache = createByReferenceCache();
        if (cache == null) return;

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
        Cache<Date, Integer> cache = createByValueCache();

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

    //TODO how do we handle mutable keys? @Test
    public void test_ExistingWithMutableKey_ByReference() {
        Cache<Date, Integer> cache = createByReferenceCache();
        if (cache == null) return;

        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Date key2 = new Date(now);
        Integer existingValue = 1;
        cache.put(key1, existingValue);
        long later = now + 5;
        key1.setTime(later);
        checkGetExpectation(existingValue, cache, key1);
        assertNull(cache.get(key2));
    }

    @Test
    public void put_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<String, Integer> cache = createCache();
        try {
            cache.put(null, 1);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void put_NullValue() throws Exception {
        Cache<String, Integer> cache = createCache();
        try {
            cache.put("key", null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void put_ExistingWithEqualButNonSameKey_ByValue() throws Exception {
        Cache<Date, Integer> cache = createByValueCache();

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
    public void put_ExistingWithEqualButNonSameKey_ByReference() throws Exception {
        Cache<Date, Integer> cache = createByReferenceCache();
        if (cache == null) return;

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
    public void put_Mutable_ByReference() {
        Cache<Integer, Date> cache = createByReferenceCache();
        if (cache == null) return;

        long now = System.currentTimeMillis();
        Date value1 = new Date(now);
        Integer key = 1;
        cache.put(key, value1);
        Date value2 = cache.get(key);
        assertSame(value1, value2);
    }

    @Test
    public void put_Mutable_ByValue() {
        Cache<Integer, Date> cache = createByValueCache();
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
        Cache<String, Integer> cache = createCache();
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
        Cache<String, Integer> cache = createCache();
        try {
            cache.getAndPut(null, 1);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndPut_NullValue() throws Exception {
        Cache<String, Integer> cache = createCache();
        try {
            cache.getAndPut("key", null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndPut_ExistingWithEqualButNonSameKey_ByValue() throws Exception {
        Cache<Date, Integer> cache = createByValueCache();

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
    public void getAndPut_ExistingWithEqualButNonSameKey_ByReference() throws Exception {
        Cache<Date, Integer> cache = createByReferenceCache();
        if (cache == null) return;

        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Integer value1 = 1;
        assertNull(cache.getAndPut(key1, value1));
        Date key2 = new Date(now);
        Integer value2 = value1 + 1;
        assertSame(value1, cache.getAndPut(key2, value2));
        checkGetExpectation(value2, cache, key2);
    }

    @Test
    public void getAndPut_Mutable_ByReference() {
        Cache<Long, Date> cache = createByReferenceCache();
        if (cache == null) return;

        long key = System.currentTimeMillis();
        Date value = new Date(key);
        assertNull(cache.getAndPut(key, value));
        assertSame(value, cache.get(key));
    }

    @Test
    public void getAndPut_Mutable_ByValue() {
        Cache<Long, Date> cache = createByValueCache();

        long key = System.currentTimeMillis();
        Date value = new Date(key);
        Date valueOriginal = new Date(key);
        assertNull(cache.getAndPut(key, value));
        value.setTime(key + 1);
        assertEquals(valueOriginal, cache.get(key));
    }

    @Test
    public void remove_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<String, Integer> cache = createCache();
        try {
            assertFalse(cache.remove(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void remove_NotExistent() throws Exception {
        Cache<String, Integer> cache = createCache();

        String existingKey = "key1";
        Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        String keyNotExisting = existingKey + "XXX";
        assertFalse(cache.remove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void remove_EqualButNotSameKey() {
        Cache<Date, Integer> cache = createCache();

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
        Cache<String, Integer> cache = createCache();
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
        final Cache<String, Integer> cache = createCache();
        try {
            assertNull(cache.getAndRemove(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void getAndRemove_NotExistent() throws Exception {
        final Cache<String, Integer> cache = createCache();


        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String keyNotExisting = existingKey + "XXX";
        assertNull(cache.getAndRemove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void getAndRemove_EqualButNotSameKey() {
        final Cache<Date, Integer> cache = createCache();
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
    public void getAndRemove_ByReference() {
        final Cache<Long, Date> cache = createByReferenceCache();
        if (cache == null) return;

        final Long key = System.currentTimeMillis();
        final Date value = new Date(key);
        cache.put(key, value);
        value.setTime(key + 1);

        assertSame(value, cache.getAndRemove(key));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void getAndRemove_ByValue() {
        final Cache<Long, Date> cache = createByValueCache();

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
        Cache<String, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        try {
            cache.getAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAll_NullKey() {
        Cache<Integer, String> cache = createCache();
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
        Cache<Integer, Integer> cache = createCache();

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
        Cache<String, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        try {
            assertFalse(cache.containsKey(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void containsKey() {
        Cache<Date, Integer> cache = createCache();
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
        Cache<Integer, Integer> cache = createCache();
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
        CacheConfiguration config = createCacheConfiguration();
        config.setStatisticsEnabled(true);
        Cache<Date, Integer> cache = createCache();
        assertNull(cache.getCacheStatistics());
    }

    @Test
    public void registerCacheEntryListener() {
        Cache<Date, Integer> cache = createCache();
        cache.registerCacheEntryListener(null, null);
        //TODO: more
    }

    @Test
    public void unregisterCacheEntryListener() {
        Cache<Date, Integer> cache = createCache();
        cache.unregisterCacheEntryListener(null);
        //TODO: more
    }

    @Test
    public void putAll_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        try {
            cache.putAll(null);
            fail("should have thrown an exception - null map not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void putAll_NullKey() {
        Cache<Date, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createByValueCache();
        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        for (Map.Entry<Date, Integer> entry : data.entrySet()) {
            checkGetExpectation(entry.getValue(), cache, entry.getKey());
        }
    }

    @Test
    public void putAll_ByReference() {
        Cache<Date, Integer> cache = createByReferenceCache();
        if (cache == null) return;

        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        for (Map.Entry<Date, Integer> entry : data.entrySet()) {
            checkGetExpectation(entry.getValue(), cache, entry.getKey());
        }
    }

    @Test
    public void putIfAbsent_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        try {
            assertFalse(cache.putIfAbsent(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void putIfAbsent_NullValue() {
        Cache<Date, Integer> cache = createCache();
        try {
            cache.putIfAbsent(new Date(), null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void putIfAbsent_Missing_ByValue() {
        Cache<Date, Long> cache = createByValueCache();

        Date key = new Date();
        Long value = key.getTime();
        assertTrue(cache.putIfAbsent(key, value));
        checkGetExpectation(value, cache, key);
    }

    @Test
    public void putIfAbsent_Missing_ByReference() {
        Cache<Date, Long> cache = createByReferenceCache();
        if (cache == null) return;

        Date key = new Date();
        Long value = key.getTime();
        assertTrue(cache.putIfAbsent(key, value));
        checkGetExpectation(value, cache, key);
    }

    @Test
    public void putIfAbsent_There_ByValue() {
        Cache<Date, Long> cache = createByValueCache();

        Date key = new Date();
        Long value = key.getTime();
        Long oldValue = value + 1;
        cache.put(key, oldValue);
        assertFalse(cache.putIfAbsent(key, value));
        checkGetExpectation(oldValue, cache, key);
    }

    @Test
    public void putIfAbsent_There_ByReference() {
        Cache<Date, Long> cache = createByReferenceCache();
        if (cache == null) return;

        Date key = new Date();
        Long value = key.getTime();
        Long oldValue = value + 1;
        cache.put(key, oldValue);
        assertFalse(cache.putIfAbsent(key, value));
        checkGetExpectation(oldValue, cache, key);
    }

    @Test
    public void replace_3arg_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        try {
            assertFalse(cache.replace(null, 1, 2));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_NullValue1() {
        Cache<Date, Integer> cache = createCache();
        try {
            assertFalse(cache.replace(new Date(), null, 2));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_NullValue2() {
        Cache<Date, Integer> cache = createCache();
        try {
            assertFalse(cache.replace(new Date(), 1, null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_3arg_Missing() {
        Cache<Date, Integer> cache = createCache();
        Date key = new Date();
        assertFalse(cache.replace(key, 1, 2));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_3arg_Different() {
        Cache<Date, Long> cache = createCache();

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
        Cache<Date, Long> cache = createByValueCache();

        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, value, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void replace_3arg_ByReference() throws Exception {
        Cache<Date, Long> cache = createByReferenceCache();
        if (cache == null) return;

        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, value, nextValue));
        assertSame(nextValue, cache.get(key));
    }

    @Test
    public void replace_2arg_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        try {
            assertFalse(cache.replace(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_2arg_NullValue() {
        Cache<Date, Integer> cache = createCache();
        try {
            assertFalse(cache.replace(new Date(), null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void replace_2arg_Missing() throws Exception {
        Cache<Date, Integer> cache = createCache();
        Date key = new Date();
        assertFalse(cache.replace(key, 1));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void replace_2arg() {
        Cache<Date, Long> cache = createCache();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void getAndReplace_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        try {
            assertNull(cache.getAndReplace(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndReplace_NullValue() {
        Cache<Date, Integer> cache = createCache();
        try {
            assertNull(cache.getAndReplace(new Date(), null));
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAndReplace_Missing() {
        Cache<Date, Integer> cache = createCache();
        Date key = new Date();
        assertNull(cache.getAndReplace(key, 1));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void getAndReplace_ByValue() {
        Cache<Long, Date> cache = createByValueCache();

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
    public void getAndReplace_ByReference() {
        Cache<Long, Date> cache = createByReferenceCache();
        if (cache == null) return;

        Long key = System.currentTimeMillis();
        Date value = new Date(key);
        cache.put(key, value);
        Date nextValue = new Date(key + 1);
        assertSame(value, cache.getAndReplace(key, nextValue));
        checkGetExpectation(nextValue, cache, key);
    }

    @Test
    public void removeAll_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        try {
            cache.removeAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void removeAll_1arg_NullKey() {
        Cache<Date, Integer> cache = createCache();
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
        Cache<Integer, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        cache.removeAll();
        for (Date key : data.keySet()) {
            assertFalse(cache.containsKey(key));
        }
    }

    @Test
    public void getConfiguration_Default() {
        Cache<Date, Integer> cache = createCache();
        CacheConfiguration config = cache.getConfiguration();
        // defaults
        CacheConfiguration defaultConfig = createCacheConfiguration();
        assertEquals(defaultConfig, config);
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
    public void getConfiguration() {
        String cacheName = CACHE_NAME + "XXX";
        CacheConfiguration defaultConfig = createCacheConfiguration();
        CacheConfiguration expectedConfig = createCacheConfiguration();
        expectedConfig.setReadThrough(!defaultConfig.isReadThrough());
        expectedConfig.setWriteThrough(!defaultConfig.isWriteThrough());
        expectedConfig.setStoreByValue(!defaultConfig.isStoreByValue());

        Cache<Date, Integer> cache = getCacheManager().
                <Date, Integer>createCacheBuilder(cacheName).
                setCacheConfiguration(expectedConfig).
                build();

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
    public void iterator_NotStarted() {
        Cache<String, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
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
    public void iterator() {
        Cache<Date, Integer> cache = createCache();
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
        Cache<Date, Integer> cache = createCache();
        assertEquals(Status.STARTED, cache.getStatus());
    }

    @Test
    public void stop() {
        Cache<Date, Integer> cache = createCache();
        cache.stop();
        assertEquals(Status.STOPPED, cache.getStatus());
    }

    //TODO: we already have basic tests
//    @Test
//    public void getStatus() {
//    }

    // ---------- utilities ----------

    private <A, B> Cache<A, B> createByValueCache() {
        CacheConfiguration config = createCacheConfiguration();
        config.setStoreByValue(true);
        return createCache(config);
    }

    private <A, B> Cache<A, B> createByReferenceCache() {
        try {
            CacheConfiguration config = createCacheConfiguration();
            config.setStoreByValue(false);
            return createCache(config);
        } catch (InvalidConfigurationException e) {
            logger.log(Level.INFO, "===== cache does not support store by reference: " + e.getMessage());
            return null;
        }
    }

    private LinkedHashMap<Date, Integer> createData(int count, long now) {
        LinkedHashMap<Date, Integer> map = new LinkedHashMap<Date, Integer>(count);
        for (int i = 0; i < count; i++) {
            map.put(new Date(now + i), i);
        }
        return map;
    }

    private LinkedHashMap<Date, Integer> createData(int count) {
        return createData(count, System.currentTimeMillis());
    }

    private <K, V> void checkGetExpectation(V expected, Cache<K, V> cache, K key) {
        if (cache.getConfiguration().isStoreByValue()) {
            assertEquals(expected, cache.get(key));
        } else {
            assertSame(expected, cache.get(key));
        }
    }
}

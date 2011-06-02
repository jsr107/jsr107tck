package javax.cache;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javax.cache.implementation.RICache;
import javax.cache.implementation.RICacheConfiguration;
import java.util.*;

/**
 * Unit test for simple App.
 *
 * These are very basic tests
 *
 * @author Yannis Cosmadopoulos
 */
public class CacheTest {
    private boolean ignoreNullKeyOnRead;
    private boolean allowNullValue;
    private static final boolean DEFAULT_IGNORE_NULL_KEY_ON_READ = true;
    private static final boolean DEFAULT_ALLOW_NULL_VALUE = true;

    @Before
    public void setUp() {
        ignoreNullKeyOnRead = isIgnoreNullKeyOnRead();
        allowNullValue = isAllowNullValue();
    }

    @Test
    public void test_get_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.get(null);
            fail("should have thrown an exception - cache not started");
        } catch(IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_get_NullKey() {
        final Cache<String, Integer> cache = createAndStartCache();
        try {
            assertNull(cache.get(null));
            if (!ignoreNullKeyOnRead) {
                fail("should have thrown an exception - null key not allowed");
            }
        } catch(NullPointerException e) {
            if (ignoreNullKeyOnRead) {
                fail("should not have thrown an exception - null key allowed");
            }
        }
    }

    @Test
    public void test_get_NotExisting() {
        final Cache<String, Integer> cache = createAndStartCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String key1 = existingKey + "XXX";
        assertNull(cache.get(key1));
    }

    @Test
    public void test_get_Existing() {
        final Cache<String, Integer> cache = createAndStartCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        checkGetExpectation(existingValue, cache, existingKey);
    }

    @Test
    public void test_get_ExistingWithEqualButNonSameKey() {
        final Cache<Date, Integer> cache = createAndStartCache();
        final long now = System.currentTimeMillis();
        final Date existingKey = new Date(now);
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        final Date newKey = new Date(now);
        assertNotSame(existingKey, newKey);
        checkGetExpectation(existingValue, cache, newKey);
    }

    @Test
    public void test_put_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.put(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_put_NullKey() throws  Exception{
        final Cache<String, Integer> cache = createAndStartCache();
        try {
            cache.put(null, 1);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_put_NullValue() throws Exception{
        final Cache<String, Integer> cache = createAndStartCache();
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
    public void test_put_ExistingWithEqualButNonSameKey() throws Exception{
        final Cache<Date, Integer> cache = createAndStartCache();
        final long now = System.currentTimeMillis();
        final Date key1 = new Date(now);
        final Integer value1 = 1;
        cache.put(key1, value1);
        final Date key2 = new Date(now);
        final Integer value2 = value1 + 1;
        cache.put(key2, value2);
        checkGetExpectation(value2, cache, key2);
    }

    @Test
    public void test_remove_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.remove(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_remove_NullKey() throws Exception{
        final Cache<String, Integer> cache = createAndStartCache();
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
    public void test_remove_NotExistent() throws Exception{
        final Cache<String, Integer> cache = createAndStartCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String keyNotExisting = existingKey + "XXX";
        assertFalse(cache.remove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void test_remove_EqualButNotSameKey() {
        final Cache<Date, Integer> cache = createAndStartCache();
        final long now = System.currentTimeMillis();

        final Date key1 = new Date(now);
        final Integer value1 = 1;
        cache.put(key1, value1);

        final Date key2 = new Date(now + 1);
        final Integer value2 = value1 + 1;
        cache.put(key2, value2);

        assertTrue(cache.remove(key1.clone()));
        assertNull(cache.get(key1));
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void test_getAndRemove_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.getAndRemove(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_getAndRemove_NullKey() throws Exception{
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
    public void test_getAndRemove_NotExistent() throws Exception{
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
        final Cache<String, Integer> cache = createCache();
        try {
            cache.getAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_getAll_Null() {
        final Cache<Date, Integer> cache = createAndStartCache();
        try {
            cache.getAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_getAll_NullKey() {
        final Cache<Integer, String> cache = createAndStartCache();
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
        final Cache<Integer, Integer> cache = createAndStartCache();

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
        final Cache<String, Integer> cache = createCache();
        try {
            cache.containsKey(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_containsKey_Null() {
        final Cache<Date, Integer> cache = createAndStartCache();
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
        final Cache<Date, Integer> cache = createAndStartCache();
        Map<Date, Integer> data = createData(3);
        for (Map.Entry<Date,Integer> entry : data.entrySet()) {
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
        final Cache<String, Integer> cache = createCache();
        try {
            cache.load(null, null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_load() {
        final Cache<Date, Integer> cache = createAndStartCache();
        cache.load(null, null, null);
    }

    @Test
    public void test_loadAll_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.loadAll(null, null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_loadAll() {
        final Cache<Date, Integer> cache = createAndStartCache();
        cache.loadAll(null, null, null);
    }

    @Test
    public void test_getCacheStatistics() {
        final Cache<Date, Integer> cache = createAndStartCache();
        //TODO: we may need more at some point
        assertNull(cache.getCacheStatistics());
    }

    @Test
    public void test_registerCacheEntryListener() {
        final Cache<Date, Integer> cache = createCache();
        cache.registerCacheEntryListener(null, null);
        //TODO: more
    }

    @Test
    public void test_unregisterCacheEntryListener() {
        final Cache<Date, Integer> cache = createCache();
        cache.unregisterCacheEntryListener(null);
        //TODO: more
    }

    @Test
    public void test_putAll_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.putAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_putAll_Null() {
        final Cache<Date, Integer> cache = createAndStartCache();
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
        for (Map.Entry<Date,Integer> entry : data.entrySet()) {
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
        final Cache<Date, Integer> cache = createAndStartCache();
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
        for (Map.Entry<Date,Integer> entry : data.entrySet()) {
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
        final Cache<Date, Integer> cache = createAndStartCache();
        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        for (Map.Entry<Date,Integer> entry : data.entrySet()) {
            checkGetExpectation(entry.getValue(), cache, entry.getKey());
        }
    }

    @Test
    public void test_putIfAbsent_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.putIfAbsent(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_putIfAbsent_NullKey() throws Exception{
        final Cache<Date, Integer> cache = createAndStartCache();
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
        final Cache<Date, Integer> cache = createAndStartCache();
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
        final Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        assertTrue(cache.putIfAbsent(key, value));
        checkGetExpectation(value, cache, key);
    }

    @Test
    public void test_putIfAbsent_There() {
        final Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        Long oldValue = value+1;
        cache.put(key, oldValue);
        assertFalse(cache.putIfAbsent(key, value));
        checkGetExpectation(oldValue, cache, key);
    }

    @Test
    public void test_replace_3arg_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.replace(null, null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_replace_3arg_NullKey() {
        final Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.replace(null, 1, 2));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_replace_3arg_NullValue1() {
        final Cache<Date, Integer> cache = createAndStartCache();
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
        final Cache<Date, Integer> cache = createAndStartCache();
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
        final Cache<Date, Integer> cache = createAndStartCache();
        assertFalse(cache.replace(new Date(), 1, 2));
    }

    @Test
    public void test_replace_3arg_Different() {
        final Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        Long desiredOldValue = value - 1;
        assertFalse(cache.replace(key, desiredOldValue, nextValue));
        assertEquals(value, cache.get(key));
    }

    @Test
    public void test_replace_3arg() throws Exception{
        final Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, value, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void test_replace_2arg_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.replace(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_replace_2arg_NullKey() {
        final Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertFalse(cache.replace(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_replace_2arg_NullValue() {
        final Cache<Date, Integer> cache = createAndStartCache();
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
    public void test_replace_2arg_Missing() throws Exception{
        final Cache<Date, Integer> cache = createAndStartCache();
        assertFalse(cache.replace(new Date(), 1));
    }

    @Test
    public void test_replace_2arg() {
        final Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertTrue(cache.replace(key, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void test_getAndReplace_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.getAndReplace(null, null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_getAndReplace_NullKey() {
        final Cache<Date, Integer> cache = createAndStartCache();
        try {
            assertNull(cache.getAndReplace(null, 1));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_getAndReplace_NullValue() {
        final Cache<Date, Integer> cache = createAndStartCache();
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
        final Cache<Date, Integer> cache = createAndStartCache();
        assertNull(cache.getAndReplace(new Date(), 1));
    }

    @Test
    public void test_getAndReplace() {
        final Cache<Date, Long> cache = createAndStartCache();
        Date key = new Date();
        Long value = key.getTime();
        cache.put(key, value);
        Long nextValue = value + 1;
        assertEquals(value, cache.getAndReplace(key, nextValue));
        assertEquals(nextValue, cache.get(key));
    }

    @Test
    public void test_removeAll_NotStarted() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.removeAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_removeAll_1arg_Null() {
        final Cache<Date, Integer> cache = createAndStartCache();
        try {
            cache.removeAll(null);
            fail("expected NPE");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_removeAll_1arg_NullKey() {
        final Cache<Date, Integer> cache = createAndStartCache();
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
        final Cache<Integer, Integer> cache = createAndStartCache();
        Map<Integer, Integer> data = new HashMap<Integer, Integer>();
        data.put(1, 1);
        data.put(2, 2);
        data.put(3,3);
        cache.putAll(data);

        data.remove(2);
        cache.removeAll(data.keySet());
        assertFalse(cache.containsKey(1));
        assertEquals(new Integer(2), cache.get(2));
        assertFalse(cache.containsKey(3));
    }

    @Test
    public void test_removeAll() {
        final Cache<Date, Integer> cache = createAndStartCache();
        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        cache.removeAll();
        for (Date key : data.keySet()) {
            assertFalse(cache.containsKey(key));
        }
    }

    @Test
    public void test_getConfiguration_Default() {
        final Cache<Date, Integer> cache = createCache();
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
        final CacheConfiguration defaultConfig = createCache().getConfiguration();
        CacheConfiguration expectedConfig = new RICacheConfiguration.Builder().
                setReadThrough(!defaultConfig.isReadThrough()).
                setWriteThrough(!defaultConfig.isWriteThrough()).
                setStoreByValue(!defaultConfig.isStoreByValue()).
                build();

        final Cache<Date, Integer> cache = createCache(expectedConfig);
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
        final Cache<String, Integer> cache = createCache();
        try {
            cache.iterator();
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void test_iterator_Empty() {
        final Cache<Date, Integer> cache = createAndStartCache();
        Iterator<Cache.Entry<Date, Integer>> iterator = cache.iterator();
        assertFalse(iterator.hasNext());
        try {
            iterator.remove();
            fail();
        } catch(IllegalStateException e) {
            //good
        }
        try {
            iterator.next();
            fail();
        } catch(NoSuchElementException e) {
            //good
        }
    }

    @Test
    public void test_iterator() {
        final Cache<Date, Integer> cache = createAndStartCache();
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
        final Cache<Date, Integer> cache = createCache();
        assertEquals(Status.UNITIALISED, cache.getStatus());
        cache.initialise();
        assertEquals(Status.STARTED, cache.getStatus());
    }

    @Test
    public void test_stopAndDispose() {
        final Cache<Date, Integer> cache = createAndStartCache();
        cache.stopAndDispose();
        assertEquals(Status.STOPPED, cache.getStatus());
    }

   //TODO: we already have basic tests
//    @Test
//    public void test_getStatus() {
//    }

    @Test
    public void test_isReadThrough() {
        fail();
    }

    @Test
    public void test_setReadThrough() {
        fail();
    }

    @Test
    public void test_isWriteThrough() {
        fail();
    }

    @Test
    public void test_isStoreByValue() {
        fail();
    }

    @Test
    public void test_setStoreByValue() {
        fail();
    }

    // ---------- utilities ----------

    protected boolean isIgnoreNullKeyOnRead() {
        return DEFAULT_IGNORE_NULL_KEY_ON_READ;
    }

    protected boolean isAllowNullValue() {
        return DEFAULT_ALLOW_NULL_VALUE;
    }

    protected <K,V> Cache<K,V> createAndStartCache() {
        Cache<K,V> cache = createCache(null);
        cache.initialise();
        return cache;
    }

    protected <K,V> Cache<K,V> createCache() {
        return createCache(null);
    }

    // ---------- utilities ----------

    private <K,V> Cache<K,V> createCache(CacheConfiguration config) {
        RICache.Builder<K,V> builder = new RICache.Builder<K,V>();
        if (config != null) {
            builder.setCacheConfiguration(config);
        }
        return builder.
                setIgnoreNullKeyOnRead(ignoreNullKeyOnRead).
                setAllowNullValue(allowNullValue).
                build();
    }

    private LinkedHashMap<Date, Integer> createData(int count, long now) {
        LinkedHashMap<Date, Integer> map = new LinkedHashMap<Date, Integer>(count);
        for (int i=0; i<count; i++) {
            map.put(new Date(now + i), i);
        }
        return map;
    }

    private <K,V> void checkGetExpectation(V expected, Cache<K,V> cache, K key) {
        //TODO: at some point we will not (only) store by reference
        assertSame(expected, cache.get(key));
    }

    private LinkedHashMap<Date, Integer> createData(int count) {
        return createData(count, System.currentTimeMillis());
    }
}

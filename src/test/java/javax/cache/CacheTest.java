package javax.cache;

import static org.junit.Assert.*;
import org.junit.Test;

import javax.cache.implementation.RICache;
import javax.cache.implementation.RICacheConfiguration;
import java.util.*;

/**
 * Unit test for simple App.
 *
 * These are very basic tests
 */
public class CacheTest {
    @Test
    public void test_get_NullKey() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.get(null);
            fail("should have thrown an exception - null key not allowed");
        } catch(NullPointerException e) {
            // good
        }
    }

    @Test
    public void test_get_NotExisting() {
        final Cache<String, Integer> cache = createCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String key1 = existingKey + "XXX";
        assertNull(cache.get(key1));
    }

    @Test
    public void test_get_Existing() {
        final Cache<String, Integer> cache = createCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        checkGetExpectation(existingValue, cache, existingKey);
    }

    @Test
    public void test_get_ExistingWithEqualButNonSameKey() {
        final Cache<Date, Integer> cache = createCache();
        final long now = System.currentTimeMillis();
        final Date existingKey = new Date(now);
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        final Date newKey = new Date(now);
        assertNotSame(existingKey, newKey);
        checkGetExpectation(existingValue, cache, newKey);
    }

    @Test
    public void test_put_NullKey() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.put(null, 1);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_put_NullValue() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.put("key", null);
            fail("should have thrown an exception - null value not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_put_ExistingWithEqualButNonSameKey() {
        final Cache<Date, Integer> cache = createCache();
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
    public void test_remove_NullKey() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.remove(null);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_remove_NotExistent() {
        final Cache<String, Integer> cache = createCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String keyNotExisting = existingKey + "XXX";
        assertFalse(cache.remove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void test_remove_EqualButNotSameKey() {
        final Cache<Date, Integer> cache = createCache();
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
    public void test_getAll_Null() {
        final Cache<Date, Integer> cache = createCache();
        try {
            cache.getAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_getAll_NullKey() {
        final Cache<Integer, String> cache = createCache();
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.add(1);
        keys.add(null);
        keys.add(2);
        try {
            cache.getAll(keys);
            fail("should have thrown an exception - null key in keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_getAll() {
        final Cache<Integer, Integer> cache = createCache();

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
    public void test_containsKey_Null() {
        final Cache<Date, Integer> cache = createCache();
        try {
            cache.containsKey(null);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_containsKey() {
        final Cache<Date, Long> cache = createCache();
        final long now = System.currentTimeMillis();
        cache.put(new Date(now), now);
        assertTrue(cache.containsKey(new Date(now)));
        assertFalse(cache.containsKey(new Date(now+1)));
        assertEquals(new Long(now), cache.get(new Date(now)));
    }

//    @Test
//    public void test_load() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.load(null, null, null);
//    }
//
//    @Test
//    public void test_loadAll() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.load(null, null, null);
//    }
//
//    @Test
//    public void test_getCacheEntry() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.getCacheEntry(null);
//    }
//
//    @Test
//    public void test_addListener() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.addListener(null);
//    }
//
//    @Test
//    public void test_removeListener() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.removeListener(null);
//    }

    @Test
    public void test_putAll_Null() {
        final Cache<Date, Integer> cache = createCache();
        try {
            cache.putAll(null);
            fail("should have thrown an exception - null map not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void test_putAll_NullKey() {
        Cache<Date, Integer> cache = createCache();
        Map<Date, Integer> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put(null, Integer.MAX_VALUE);
        try {
            cache.putAll(data);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
        for (Map.Entry<Date,Integer> entry : data.entrySet()) {
            if (entry.getKey() != null) {
                assertNull(cache.get(entry.getKey()));
            }
        }
    }

    @Test
    public void test_putAll_NullValue() {
        final Cache<Date, Integer> cache = createCache();
        Map<Date, Integer> data = createData(3);
        // note: using LinkedHashMap, we have made an effort to ensure the null
        // be added after other "good" values.
        data.put(new Date(), null);
        try {
            cache.putAll(data);
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //good
        }
        for (Map.Entry<Date,Integer> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                assertNull(cache.get(entry.getKey()));
            }
        }
    }

    @Test
    public void test_putAll() {
        final Cache<Date, Integer> cache = createCache();
        Map<Date, Integer> data = createData(3);
        cache.putAll(data);
        for (Map.Entry<Date,Integer> entry : data.entrySet()) {
            checkGetExpectation(entry.getValue(), cache, entry.getKey());
        }
    }

//    @Test
//    public void test_putIfAbsent() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.putIfAbsent(null, null);
//    }
//
//    @Test
//    public void test_replace_3arg() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.replace(null, null, null);
//    }
//
//    @Test
//    public void test_replace_2arg() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.replace(null, null);
//    }
//
//    @Test
//    public void test_getAndReplace() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.getAndReplace(null, null);
//    }
//
//    @Test
//    public void test_removeAll_1arg() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.removeAll(null);
//    }
//
//    @Test
//    public void test_removeAll() {
//        final Cache<Date, Integer> cache = createCache();
//        cache.removeAll();
//    }

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

    // ---------- utilities ----------

    private LinkedHashMap<Date, Integer> createData(int count) {
        return createData(count, System.currentTimeMillis());
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

    protected <K,V> Cache<K,V> createCache() {
        return new RICache.Builder<K,V>().build();
    }

    private <K,V> Cache<K,V> createCache(CacheConfiguration config) {
        return new RICache.Builder<K,V>().setCacheConfiguration(config).build();
    }
}

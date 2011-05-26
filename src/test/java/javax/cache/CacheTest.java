package javax.cache;

import static org.junit.Assert.*;
import org.junit.Test;

import javax.cache.implementation.RICache;
import java.util.Date;

/**
 * Unit test for simple App.
 *
 * These are very basic tests
 */
public class CacheTest {
    @Test
    public void testGetWithNullKey() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.get(null);
            fail("should have thrown an exception - null key not allowed");
        } catch(IllegalArgumentException e) {
            // good
        }
    }

    @Test
    public void testGetNotExisting() {
        final Cache<String, Integer> cache = createCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String key1 = existingKey + "XXX";
        assertNull(cache.get(key1));
    }

    @Test
    public void testGetExisting() {
        final Cache<String, Integer> cache = createCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        //TODO: at some point we will not (only) store by reference
        assertSame(existingValue, cache.get(existingKey));
    }

    @Test
    public void testGetExistingWithEqualButNonSameKey() {
        final Cache<Date, Integer> cache = createCache();
        final long now = System.currentTimeMillis();
        final Date existingKey = new Date(now);
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        final Date newKey = new Date(now);
        assertNotSame(existingKey, newKey);
        //TODO: at some point we will not (only) store by reference
        assertSame(existingValue, cache.get(newKey));
    }

    @Test
    public void testPutNullKey() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.put(null, 1);
            fail("should have thrown an exception - null key not allowed");
        } catch (IllegalArgumentException e) {
            //good
        }
    }

    @Test
    public void testPutNullValue() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.put("key", null);
            fail("should have thrown an exception - null value not allowed");
        } catch (IllegalArgumentException e) {
            //good
        }
    }

    @Test
    public void testPutExistingWithEqualButNonSameKey() {
        final Cache<Date, Integer> cache = createCache();
        final long now = System.currentTimeMillis();
        final Date key1 = new Date(now);
        final Integer value1 = 1;
        cache.put(key1, value1);
        final Date key2 = new Date(now);
        final Integer value2 = value1 + 1;
        cache.put(key2, value2);
        //TODO: at some point we will not (only) store by reference
        assertSame(value2, cache.get(key2));
    }

    @Test
    public void testRemoveNullKey() {
        final Cache<String, Integer> cache = createCache();
        try {
            cache.remove(null);
            fail("should have thrown an exception - null key not allowed");
        } catch (IllegalArgumentException e) {
            //good
        }
    }

    @Test
    public void testRemoveNotExistent() {
        final Cache<String, Integer> cache = createCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String keyNotExisting = existingKey + "XXX";
        assertFalse(cache.remove(keyNotExisting));
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void testRemoveEqualButNotSameKey() {
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

    protected <K,V> Cache<K,V> createCache() {
        return new RICache<K,V>();
    }
}

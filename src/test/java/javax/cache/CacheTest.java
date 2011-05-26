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
    public void testCreateCache() {
        Cache<String, Integer> cache = createCache();
    }

    @Test
    public void testGetWithNullKey() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.get(null);
            fail("should have thrown an exception - null key not allowed");
        } catch(IllegalArgumentException e) {
            // good
        }
    }

    @Test
    public void testGetNotExisting() {
        Cache<String, Integer> cache = createCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);

        final String key1 = existingKey + "XXX";
        assertNull(cache.get(key1));
    }

    @Test
    public void testGetExisting() {
        Cache<String, Integer> cache = createCache();
        final String existingKey = "key1";
        final Integer existingValue = 1;
        cache.put(existingKey, existingValue);
        //TODO: at some point we will not (only) store by reference
        assertSame(existingValue, cache.get(existingKey));
    }

    @Test
    public void testGetExistingWithNonSameButEqualKey() {
        Cache<Date, Integer> cache = createCache();
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
        Cache<String, Integer> cache = createCache();
        try {
            cache.put(null, 1);
            fail("should have thrown an exception - null key not allowed");
        } catch (IllegalArgumentException e) {
            //good
        }
    }

    @Test
    public void testPutNullValue() {
        Cache<String, Integer> cache = createCache();
        try {
            cache.put("key", null);
            fail("should have thrown an exception - null key not allowed");
        } catch (IllegalArgumentException e) {
            //good
        }
    }

    protected <K,V> Cache<K,V> createCache() {
        return new RICache<K,V>();
    }
}

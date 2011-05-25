package javax.cache;


import org.junit.Test;

import javax.cache.implementation.RICache;

/**
 * Unit test for simple App.
 */
public class CacheTest {

    @Test
    public void testCreateCache() {
        Cache<String,Integer> cache = new RICache<String,Integer>();
    }

    protected <K,V> Cache<K,V> createCache() {
        return new RICache<K,V>();
    }
}

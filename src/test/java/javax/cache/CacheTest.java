package javax.cache;


import org.junit.Test;

import javax.cache.implementation.RICache;

/**
 * Unit test for simple App.
 */
public class CacheTest {

    @Test
    public void testCreateCache() {
        Cache cache = new RICache();
    }


}

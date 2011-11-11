package javax.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.util.ExcludeListExcluder;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Cache implementations must support storeByValue.
 * <p/>
 * Tests aspects where storeByValue makes a difference
 *
 * @author Yannis Cosmadopoulos
 * @author Greg Luck
 * @since 1.0
 */
public class StoreByValueTest extends CacheTestSupport<Date, Date> {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule = new ExcludeListExcluder(this.getClass());

    @Before
    public void setUp() {
        super.setUp();
    }

    @After
    public void teardown() {
        getCacheManager().removeCache(getTestCacheName());
        try {
            Caching.close();
        } catch (CachingShutdownException e) {
            //expected
        }
    }


    @Test
    public void get_Existing_MutateValue() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        cache.put(existingKey, existingValue);
        existingValue.setTime(now + 1);
        assertEquals(new Date(now), cache.get(existingKey));
    }

    @Test
    public void get_Existing_MutateKey() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        cache.put(existingKey, existingValue);
        existingKey.setTime(now + 1);
        assertEquals(new Date(now), cache.get(new Date(now)));
    }

    @Test
    public void get_DeclaredImmutable() {
        CacheManager cacheManager = getCacheManager();
        // Note: we lie - Date is mutable
        cacheManager.registerImmutableClass(Date.class);
        cacheManager.removeCache(getTestCacheName());
        Cache<Date, Date> cache = cacheManager.<Date, Date>createCacheBuilder(getTestCacheName()).build();

        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        cache.put(existingKey, existingValue);

        if (existingValue == cache.get(existingKey)) {
            // we can't actually do an assertion as impl may not store by ref
            LOG.info("Immutable was stored by reference");
        } else {
            assertEquals(existingValue, cache.get(existingKey));
        }
    }

    @Test
    public void getAndPut_NotThere() {
        if (cache == null) return;

        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        assertNull(cache.getAndPut(existingKey, existingValue));
        existingValue.setTime(now + 1);
        assertEquals(new Date(now), cache.get(existingKey));
    }

    @Test
    public void getAndPut_Existing_MutateValue() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date value1 = new Date(now);
        cache.getAndPut(existingKey, value1);
        Date value2 = new Date(now + 1);
        value1.setTime(now + 2);
        assertEquals(new Date(now), cache.getAndPut(existingKey, value2));
        value2.setTime(now + 3);
        assertEquals(new Date(now + 1), cache.get(existingKey));
    }

    @Test
    public void getAndPut_Existing_NonSameKey_MutateValue() throws Exception {
        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Date value1 = new Date(now);
        cache.getAndPut(key1, value1);
        value1.setTime(now + 1);
        Date key2 = new Date(now);
        Date value2 = new Date(now + 2);
        assertEquals(new Date(now), cache.getAndPut(key2, value2));
        value2.setTime(now + 3);
        assertEquals(new Date(now + 2), cache.get(key1));
        assertEquals(new Date(now + 2), cache.get(key2));
    }

    @Test
    public void getAndPut_Existing_NonSameKey_MutateKey() {
        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Date value1 = new Date(now);
        cache.getAndPut(key1, value1);
        key1.setTime(now + 1);
        Date key2 = new Date(now);
        Date value2 = new Date(now + 2);
        assertEquals(new Date(now), cache.getAndPut(key2, value2));
        assertEquals(new Date(now + 2), cache.get(key2));
    }
}

package javax.cache;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.util.ExcludeListExcluder;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Cache implemenations must support storeByValue.
 * <p/>
 * Tests aspects where storeByValue makes a difference
 *
 * @author Greg Luck
 * @since 1.0
 */
public class CacheStoreByValueTest extends TestSupport {

    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule = new ExcludeListExcluder(this.getClass());


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



}

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

import javax.cache.util.AllTestExcluder;
import javax.cache.util.ExcludeListExcluder;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for Cache.
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 */
public class CacheStoreByReferenceTest extends TestSupport {

    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule =
            CacheManagerFactory.INSTANCE.isSupported(OptionalFeature.STORE_BY_REFERENCE) ?
                    new ExcludeListExcluder(this.getClass()) :
                    new AllTestExcluder();

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
    public void putIfAbsent_Missing_ByReference() {
        Cache<Date, Long> cache = createByReferenceCache();
        if (cache == null) return;

        Date key = new Date();
        Long value = key.getTime();
        assertTrue(cache.putIfAbsent(key, value));
        checkGetExpectation(value, cache, key);
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

    // ---------- utilities ----------

    private <A, B> Cache<A, B> createByReferenceCache() {
        CacheManager cacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        CacheConfiguration config = cacheManager.createCacheConfiguration();
        config.setStoreByValue(false);
        Cache<A, B> cache = cacheManager.<A, B>createCacheBuilder(CACHE_NAME).setCacheConfiguration(config).build();
        return cache;
    }
}

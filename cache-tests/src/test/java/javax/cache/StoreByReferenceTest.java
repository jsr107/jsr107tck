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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.util.AllTestExcluder;
import javax.cache.util.ExcludeListExcluder;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Implementations can optionally support storeByReference.
 *
 * Tests aspects where storeByReference makes a difference
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @author Greg Luck
 * @since 1.0
 */
public class StoreByReferenceTest extends CacheTestSupport<Date, Date> {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule =
            CacheManagerFactory.isSupported(OptionalFeature.STORE_BY_REFERENCE) ?
                    new ExcludeListExcluder(this.getClass()) :
                    new AllTestExcluder();

    @Before
    public void setUp() {
        CacheManagerFactory.close();
        super.setUp();
    }

    @Test
    public void get_Existing() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        cache.put(existingKey, existingValue);
        // unnecessary since after we test for same (not equals), but "advertises" consequence
        existingValue.setTime(now + 1);
        assertSame(existingValue, cache.get(existingKey));
    }

    @Test
    public void get_Existing_NotSameKey() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        cache.put(existingKey, existingValue);
        // unnecessary since after we test for same (not equals), but "advertises" consequence
        existingValue.setTime(now + 1);
        assertSame(existingValue, cache.get(new Date(now)));
    }

    /**
     * We know that values can get mutated but so can keys!
     * Which causes lookups to fail.
     * In fact the entry get lost and cannot be retrieved.
     * This is also how Map behaves.
     * TODO: don't think we should dictate semantics for key mutation
     */
    @Test
    public void get_Existing_MutateKey() {
        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        LOG.info(key1.toString());
        Date key1OriginalValue = (Date) key1.clone();
        Date existingValue = new Date(now);
        cache.put(key1, existingValue);
        long later = now + 5000;
        assertTrue(cache.containsKey(key1));
        assertNotNull(cache.get(key1));

        //now mutate the key
        key1.setTime(later);
        LOG.info(key1.toString());
        assertFalse(cache.containsKey(key1));
        assertNull(cache.get(key1));

        //now test with the original key value
        assertFalse(cache.containsKey(key1OriginalValue));
        assertNull(cache.get(key1OriginalValue));

        //Entry is there but is irretrievable
        for (Cache.Entry<Date, Date> entry: cache){
            LOG.info(entry.getKey().toString());
        }

        //try to remove it, but it cannot be found!
        assertFalse(cache.remove(key1));
        assertFalse(cache.remove(key1OriginalValue));

    }

    @Test
    public void put_Existing_NotSameKey() throws Exception {
        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Date value1 = new Date(now);
        cache.put(key1, value1);
        Date key2 = new Date(now);
        Date value2 = new Date(now);
        cache.put(key2, value2);
        // unnecessary since after we test for same (not equals), but "advertises" consequence
        value2.setTime(now + 1);
        assertSame(value2, cache.get(key2));
    }

    @Test
    public void getAndPut_NotThere() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date existingValue = new Date(now);
        assertNull(cache.getAndPut(existingKey, existingValue));
        // unnecessary since after we test for same (not equals), but "advertises" consequence
        existingValue.setTime(now + 1);
        assertSame(existingValue, cache.get(existingKey));
    }

    @Test
    public void getAndPut_Existing() {
        long now = System.currentTimeMillis();
        Date existingKey = new Date(now);
        Date value1 = new Date(now);
        cache.getAndPut(existingKey, value1);
        Date value2 = new Date(now + 1);
        assertSame(value1, cache.getAndPut(existingKey, value2));
        assertSame(value2, cache.get(existingKey));
    }

    @Test
    public void getAndPut_Existing_NotSameKey() {
        long now = System.currentTimeMillis();
        Date key1 = new Date(now);
        Date value1 = new Date(now);
        cache.getAndPut(key1, value1);
        Date key2 = new Date(now);
        Date value2 = new Date(now + 1);
        assertSame(value1, cache.getAndPut(key2, value2));
        assertSame(value2, cache.get(key1));
        assertSame(value2, cache.get(key2));
    }

    @Test
    public void putAll() {
        Map<Date, Date> data = createDDData(3);
        cache.putAll(data);
        for (Map.Entry<Date, Date> entry : data.entrySet()) {
            assertSame(entry.getValue(), cache.get(entry.getKey()));
        }
    }

    @Test
    public void putIfAbsent_Missing() {
        long now = System.currentTimeMillis();
        Date key = new Date(now);
        Date value = new Date(now);
        assertTrue(cache.putIfAbsent(key, value));
        assertSame(value, cache.get(key));
    }

    @Test
    public void putIfAbsent_There() {
        long now = System.currentTimeMillis();
        Date key = new Date(now);
        Date value = new Date(now);
        Date oldValue = new Date(now + 1);
        cache.put(key, oldValue);
        assertFalse(cache.putIfAbsent(key, value));
        assertSame(oldValue, cache.get(key));
    }

    @Test
    public void replace_3arg() throws Exception {
        long now = System.currentTimeMillis();
        Date key = new Date(now);
        Date value = new Date(now);
        cache.put(key, value);
        Date nextValue = new Date(now + 1);
        assertTrue(cache.replace(key, value, nextValue));
        assertSame(nextValue, cache.get(key));
    }

    @Test
    public void getAndReplace() {
        long now = System.currentTimeMillis();
        Date key = new Date(now);
        Date value = new Date(now);
        cache.put(key, value);
        Date nextValue = new Date(now + 1);
        assertSame(value, cache.getAndReplace(key, nextValue));
        assertSame(nextValue, cache.get(key));
    }

    // ---------- utilities ----------

    protected <A, B> CacheBuilder<A, B> extraSetup(CacheBuilder<A, B> builder) {
        return super.extraSetup(builder).setStoreByValue(false);
    }
}

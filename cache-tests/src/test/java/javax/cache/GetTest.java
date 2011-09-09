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

import javax.cache.util.ExcludeListExcluder;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for Cache.
 * <p/>
 * Testing
 * <pre>
 * V get(Object key) throws CacheException;
 * Map<K, V> getAll(Collection<? extends K> keys) throws CacheException;
 * </pre>
 * <p/>
 * When it matters whether the cache is stored by reference or by value, see {@link StoreByValueTest} and
 * {@link StoreByReferenceTest}.
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class GetTest extends CacheTestSupport<Long, String> {

    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule = new ExcludeListExcluder(this.getClass());

    @Test
    public void get_NotStarted() {
        cache.stop();
        try {
            cache.get(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void get_NullKey() {
        try {
            assertNull(cache.get(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void get_NotExisting() {
        Long existingKey = System.currentTimeMillis();
        String existingValue = "value" + existingKey;
        cache.put(existingKey, existingValue);

        Long key1 = existingKey + 1;
        assertNull(cache.get(key1));
    }

    @Test
    public void get_Existing() {
        Long existingKey = System.currentTimeMillis();
        String existingValue = "value" + existingKey;
        cache.put(existingKey, existingValue);
        assertEquals(existingValue, cache.get(existingKey));
    }

    @Test
    public void get_Existing_NotSameKey() {
        Long existingKey = System.currentTimeMillis();
        String existingValue = "value" + existingKey;
        cache.put(existingKey, existingValue);
        assertEquals(existingValue, cache.get(new Long(existingKey)));
    }

    @Test
    public void getAll_NotStarted() {
        cache.stop();
        try {
            cache.getAll(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void getAll_Null() {
        try {
            cache.getAll(null);
            fail("should have thrown an exception - null keys not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void getAll_NullKey() {
        ArrayList<Long> keys = new ArrayList<Long>();
        keys.add(1L);
        keys.add(null);
        keys.add(2L);
        try {
            cache.getAll(keys);
            fail("should have thrown an exception - null key in keys not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void getAll() {
        ArrayList<Long> keysInCache = new ArrayList<Long>();
        keysInCache.add(1L);
        keysInCache.add(2L);
        for (Long k : keysInCache) {
            cache.put(k, "value" + k);
        }

        ArrayList<Long> keysToGet = new ArrayList<Long>();
        keysToGet.add(2L);
        keysToGet.add(3L);

        Map<Long, String> map = cache.getAll(keysToGet);
        assertEquals(keysToGet.size(), map.size());
        for (Long key : keysToGet) {
            assertTrue(map.containsKey(key));
            if (keysInCache.contains(key)) {
                assertEquals(cache.get(key), map.get(key));
                assertEquals("value" + key, map.get(key));
            } else {
                assertFalse(cache.containsKey(key));
                assertNull(map.get(key));
            }
        }
    }
}

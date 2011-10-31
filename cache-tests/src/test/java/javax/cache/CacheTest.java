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

import domain.Beagle;
import domain.Identifier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.event.CacheEntryReadListener;
import javax.cache.event.NotificationScope;
import javax.cache.util.ExcludeListExcluder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for Cache.
 * <p/>
 * When it matters whether the cache is stored by reference or by value,
 * see {@link StoreByValueTest} and
 * {@link StoreByReferenceTest}.
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheTest extends CacheTestSupport<Long, String> {

    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule = new ExcludeListExcluder(this.getClass()) {

        /* (non-Javadoc)
         * @see javax.cache.util.ExcludeListExcluder#isExcluded(java.lang.String)
         */
        @Override
        protected boolean isExcluded(String methodName) {
            if ("testUnwrap".equals(methodName) && getUnwrapClass(CacheManager.class) == null) {
                return true;
            }

            return super.isExcluded(methodName);
        }
    };

    @Test
    public void simpleAPI() {
        String cacheName = "sampleCache";
        CacheManager cacheManager = getCacheManager();
        Cache<String, Integer> cache = cacheManager.getCache(cacheName);
        cache = cacheManager.<String, Integer>createCacheBuilder(cacheName).setStoreByValue(false).build();

        String key = "key";
        Integer value1 = 1;
        cache.put(key, value1);
        Integer value2 = cache.get(key);
        assertEquals(value1, value2);
    }

    @Test
    public void genericsTest() {

        String cacheName = "genericsCache";
        CacheManager cacheManager = getCacheManager();
        Cache<Identifier, Beagle> cacheGeneric = cacheManager.getCache(cacheName);
        cacheGeneric = cacheManager.<Identifier, Beagle>createCacheBuilder(cacheName).setStoreByValue(false).build();
        Beagle pistachio = new Beagle();
        cacheGeneric.put(new Identifier("Pistachio"), pistachio);


        Cache cacheNonGeneric = cacheManager.getCache(cacheName);
        Object value = cacheNonGeneric.get(new Identifier("Pistachio"));
        assertNotNull(value);


    }


    @Test
    public void getCacheName() {
        assertEquals(getTestCacheName(), cache.getName());
    }

    @Test
    public void containsKey_NotStarted() {
        cache.stop();
        try {
            cache.containsKey(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void containsKey_Null() {
        try {
            assertFalse(cache.containsKey(null));
            fail("should have thrown an exception - null key not allowed");
        } catch (NullPointerException e) {
            //expected
        }
    }

    @Test
    public void containsKey() {
        Map<Long, String> data = createLSData(3);
        for (Map.Entry<Long, String> entry : data.entrySet()) {
            assertFalse(cache.containsKey(entry.getKey()));
            cache.put(entry.getKey(), entry.getValue());
            assertTrue(cache.containsKey(entry.getKey()));
        }
        for (Long key : data.keySet()) {
            assertTrue(cache.containsKey(key));
        }
    }

    @Test
    public void load_NotStarted() {
        cache.stop();
        try {
            cache.load(null);
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void getStatistics() {
        assertNull(cache.getStatistics());
    }

    @Test
    public void getStatistics_NotEnabled() {
        cache.getConfiguration().setStatisticsEnabled(false);
        assertNull(cache.getStatistics());
    }

    @Test
    public void getStatistics_Enabled() {
        cache.getConfiguration().setStatisticsEnabled(true);
        assertNotNull(cache.getStatistics());
    }

    @Test
    public void registerCacheEntryListener() {
        CacheEntryReadListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
        cache.registerCacheEntryListener(listener, NotificationScope.LOCAL, false);
        //TODO: more
        //todo prevent null listener
    }


    @Test
    public void unregisterCacheEntryListener() {
        CacheEntryReadListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
        cache.registerCacheEntryListener(listener, NotificationScope.LOCAL, false);
        cache.unregisterCacheEntryListener(null);
        cache.unregisterCacheEntryListener(listener);
        //TODO: more
    }

    @Test
    public void getConfiguration_Mutation() {
        CacheConfiguration config = cache.getConfiguration();

        boolean enabled = config.isStatisticsEnabled();
        assertEquals(enabled, config.isStatisticsEnabled());
        config.setStatisticsEnabled(!enabled);
        assertEquals(!enabled, config.isStatisticsEnabled());
        assertEquals(!enabled, cache.getConfiguration().isStatisticsEnabled());
    }

    @Test
    public void iterator_NotStarted() {
        cache.stop();
        try {
            cache.iterator();
            fail("should have thrown an exception - cache not started");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void iterator_Empty() {
        Iterator<Cache.Entry<Long, String>> iterator = cache.iterator();
        assertFalse(iterator.hasNext());
        try {
            iterator.remove();
            fail();
        } catch (IllegalStateException e) {
            //good
        }
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException e) {
            //good
        }
    }

    @Test
    public void iterator() {
        LinkedHashMap<Long, String> data = createLSData(3);
        cache.putAll(data);
        Iterator<Cache.Entry<Long, String>> iterator = cache.iterator();
        while (iterator.hasNext()) {
            Cache.Entry<Long, String> next = iterator.next();
            assertEquals(next.getValue(), data.get(next.getKey()));
            iterator.remove();
            data.remove(next.getKey());
        }
        assertTrue(data.isEmpty());
    }

    @Test
    public void initialise() {
        Assert.assertEquals(Status.STARTED, cache.getStatus());
    }

    @Test
    public void stop() {
        cache.stop();
        assertEquals(Status.STOPPED, cache.getStatus());
    }

    @Test
    public void testUnwrap() {
        //Assumes rule will exclude this test when no unwrapClass is specified
        final Class<?> unwrapClass = getUnwrapClass(Cache.class);
        final Object unwrappedCache = cache.unwrap(unwrapClass);

        assertTrue(unwrapClass.isAssignableFrom(unwrappedCache.getClass()));
    }

    // ---------- utilities ----------

    /**
     * Test listener
     *
     * @param <K>
     * @param <V>
     */
    static class MyCacheEntryListener<K, V> implements CacheEntryReadListener<K, V> {


        /**
         * Called after the entry has been read. If no entry existed for the key the event is not called.
         * This method is not called if a batch operation was performed.
         *
         * @param entry The entry just read.
         * @see #onReadAll(Iterable)
         */
        @Override
        public void onRead(Cache.Entry<K, V> entry) {
            //noop
        }

        /**
         * Called after the entries have been read. Only entries which existed in the cache are passed in.
         *
         * @param entries The entry just read.
         */
        @Override
        public void onReadAll(Iterable<Cache.Entry<K, V>> entries) {
            //noop
        }

        /**
         * @return the notification scope for this listener
         */
        @Override
        public NotificationScope getNotificationScope() {
            return NotificationScope.LOCAL;
        }
    }
}

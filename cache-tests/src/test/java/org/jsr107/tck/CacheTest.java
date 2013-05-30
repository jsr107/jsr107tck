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

package org.jsr107.tck;

import manager.CacheNameOnEachMethodBlogManagerImpl;
import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.Configuration;
import javax.cache.MutableConfiguration;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.transaction.IsolationLevel;
import javax.cache.transaction.Mode;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for Cache.
 * <p/>
 * When it matters whether the cache is stored by reference or by value,
 * see {@link StoreByValueTest} and {@link StoreByReferenceTest}.
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

    @Override
    protected MutableConfiguration<Long, String> newMutableConfiguration() {
        return new MutableConfiguration<Long, String>(Long.class, String.class);
    }

    @Test
    public void sameConfiguration() {
        Configuration<Integer, Integer> config1 = new MutableConfiguration<Integer, Integer>();
        Configuration<Integer, Integer> config2 = new MutableConfiguration<Integer, Integer>();
        assertEquals(config1, config2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsUsingStoreByReference() {
        String cacheName = "transactional-by-reference";
        Configuration<Integer, Integer> config = new MutableConfiguration<Integer, Integer>()
            .setStoreByValue(false)
            .setTransactions(IsolationLevel.READ_COMMITTED, Mode.LOCAL)
            .setTransactionsEnabled(true);

        CacheManager cacheManager = getCacheManager();
        cacheManager.configureCache(cacheName, config);

        fail("Should not be able to configure a transaction with a store-by-reference cache");
    }

    @Test
    public void simpleAPI() {
        Long key = 1L;
        String value1 = "key";

        cache.put(key, value1);

        String value2 = cache.get(key);

        assertEquals(value1, value2);
    }

    @Test()
    public void clearTest() {
        Long key = 1L;
        String value1 = "key";

        cache.put(key, value1);

        String value2 = cache.get(key);

        assertEquals(value1, value2);

        cache.clear();
        assertNull(cache.get(key));
    }

//TODO: move these into a new type-based test class
//    /**
//     * All these work with get(Object)
//     */
//    @Test
//    public void genericsTest() {
//
//        String cacheName = "genericsCache";
//        CacheManager cacheManager = getCacheManager();
//        Cache<Identifier, Beagle> cacheGeneric = cacheManager.getCache(cacheName);
//        cacheGeneric = cacheManager.configureCache(cacheName, new MutableConfiguration<Identifier, Beagle>());
//        Beagle pistachio = new Beagle();
//        cacheGeneric.put(new Identifier("Pistachio"), pistachio);
//        //Illegal with change to get(K)
//        //Object value = cacheGeneric.get(new Identifier2("Pistachio"));
//
//        Cache cacheNonGeneric = cacheManager.getCache(cacheName);
//        //Illegal with change to get(K)
//        //value = cacheNonGeneric.get(new Identifier2("Pistachio"));
//        //assertNotNull(value);
//    }
//
//    @Test
//    public void hashcode() {
//        Identifier identifier = new Identifier("Pistachio");
//        System.out.println(identifier.hashCode());
//        System.out.println("Pistachio".hashCode());
//    }

    @Test
    public void getCacheName() {
        assertEquals(getTestCacheName(), cache.getName());
    }

    @Test
    public void containsKey_Closed() {
        cache.close();
        try {
            cache.containsKey(null);
            fail("should have thrown an exception - cache closed");
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
            assertFalse("before put", cache.containsKey(entry.getKey()));
            cache.put(entry.getKey(), entry.getValue());
            assertTrue("after put", cache.containsKey(entry.getKey()));
        }
        for (Long key : data.keySet()) {
            assertTrue("finally", cache.containsKey(key));
        }
    }

    @Test
    public void load_Closed() {
        cache.close();
        try {
            cache.loadAll(null, true, null);
            fail("should have thrown an exception - cache closed");
        } catch (IllegalStateException e) {
            //good
        }
    }

    @Test
    public void iterator_Closed() {
        cache.close();
        try {
            cache.iterator();
            fail("should have thrown an exception - cache closed");
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
        try {
            cache.getCacheManager();
        } catch (IllegalStateException e) {
            fail("Should be able to access the CacheManager for a new Cache");
        }
    }

    @Test
    public void close() {
        cache.close();

        try {
            cache.get(1L);
            fail("Should not be able to use a closed Cache");
        } catch (IllegalStateException e) {
            //SKIP: everything is ok if we can't access the CacheManager
        }
    }

    @Test
    public void testUnwrap() {
        //Assumes rule will exclude this test when no unwrapClass is specified
        final Class<?> unwrapClass = getUnwrapClass(Cache.class);
        final Object unwrappedCache = cache.unwrap(unwrapClass);

        assertTrue(unwrapClass.isAssignableFrom(unwrappedCache.getClass()));
    }

    @Test
    public void testGetCacheManager() throws Exception {
        String cacheName = "SampleCache";

        URI uri = Caching.getCachingProvider().getDefaultURI();

        ClassLoader cl1 = Thread.currentThread().getContextClassLoader();
        ClassLoader cl2 = URLClassLoader.newInstance(new URL[]{}, cl1);

        CacheManager cacheManager1 = Caching.getCachingProvider().getCacheManager(uri, cl1);
        CacheManager cacheManager2 = Caching.getCachingProvider().getCacheManager(uri, cl2);
        assertNotSame(cacheManager1, cacheManager2);

        Cache cache1 = cacheManager1.configureCache(cacheName, new MutableConfiguration());
        Cache cache2 = cacheManager2.configureCache(cacheName, new MutableConfiguration());

        assertSame(cacheManager1, cache1.getCacheManager());
        assertSame(cacheManager2, cache2.getCacheManager());
    }

    /**
     * todo this just illustrates how easily we could discover a runtime annotation. Remove eventually.
     */
    @Test
    public void testAnnotations() {
        Object value = new CacheNameOnEachMethodBlogManagerImpl();
        boolean foundRemoveAllAnnotation = false;
        for (Method m : value.getClass().getMethods()) {
            if (m.isAnnotationPresent(CacheRemoveAll.class)) {
                System.out.println(m.getName());
                foundRemoveAllAnnotation = true;
            }
        }
       assertTrue(foundRemoveAllAnnotation);
    }

}

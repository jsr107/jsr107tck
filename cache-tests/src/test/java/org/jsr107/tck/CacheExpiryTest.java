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

import java.util.HashMap;

import javax.cache.Cache;
import javax.cache.CacheEntryExpiryPolicy;
import javax.cache.Cache.Entry;
import javax.cache.CacheConfiguration.Duration;

import org.jsr107.tck.util.ExcludeListExcluder;
import org.jsr107.tck.util.TCKCacheConfiguration;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for expiring cache entries with {@link CacheEntryExpiryPolicy}s.
 * 
 * @author Brian Oliver
 */
public class CacheExpiryTest extends TestSupport {

    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());


    @After
    public void cleanup() {
        for (Cache<?, ?> cache : getCacheManager().getCaches()) {
            getCacheManager().removeCache(cache.getName());
        }
    }
    
    /**
     * Ensure that a cache using a {@link CacheEntryExpiryPolicy} configured to 
     * return a {@link Duration#ZERO} for newly created entries will immediately 
     * expire said entries.
     */
    @Test
    public void expire_whenCreated() {
        TCKCacheConfiguration<Integer, Integer> config = new TCKCacheConfiguration<Integer, Integer>();
        config.setCacheEntryExpiryPolicy(new ParameterizedExpiryPolicy<Integer, Integer>(Duration.ZERO, null, null));
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), config);

        cache.put(1, 1);
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));

        cache.put(1, 1);
        assertFalse(cache.remove(1));

        cache.put(1, 1);
        assertFalse(cache.remove(1, 1));
        
        cache.getAndPut(1, 1);
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));
        
        cache.putIfAbsent(1, 1);
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));
        
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(1, 1);
        cache.putAll(map);
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));

        cache.put(1, 1);
        assertFalse(cache.iterator().hasNext());
    }
    
    /**
     * Ensure that a cache using a {@link CacheEntryExpiryPolicy} configured to 
     * return a {@link Duration#ZERO} after accessing entries will immediately 
     * expire said entries.
     */
    @Test
    public void expire_whenAccessed() {
        TCKCacheConfiguration<Integer, Integer> config = new TCKCacheConfiguration<Integer, Integer>();
        config.setCacheEntryExpiryPolicy(new ParameterizedExpiryPolicy<Integer, Integer>(Duration.ETERNAL, Duration.ZERO, null));
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), config);

        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.get(1));
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));

        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.get(1));
        assertFalse(cache.containsKey(1));
        assertNull(cache.getAndReplace(1, 2));

        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.get(1));
        assertFalse(cache.containsKey(1));
        assertNull(cache.getAndRemove(1));

        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.get(1));
        assertFalse(cache.remove(1));

        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.get(1));
        assertFalse(cache.remove(1, 1));
        
        cache.getAndPut(1, 1);
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.get(1));
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));
        
        cache.getAndPut(1, 1);
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.getAndPut(1, 1));
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.get(1));
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));

        cache.putIfAbsent(1, 1);
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.get(1));
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));
        
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(1, 1);
        cache.putAll(map);
        assertTrue(cache.containsKey(1));
        assertNotNull(cache.get(1));
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));

        cache.put(1, 1);
        assertTrue(cache.iterator().hasNext());
        assertEquals((Integer)1, cache.iterator().next().getValue());
        assertFalse(cache.iterator().hasNext());
    }
    
    /**
     * Ensure that a cache using a {@link CacheEntryExpiryPolicy} configured to 
     * return a {@link Duration#ZERO} after modifying entries will immediately 
     * expire said entries.
     */
    @Test
    public void expire_whenModified() {
        TCKCacheConfiguration<Integer, Integer> config = new TCKCacheConfiguration<Integer, Integer>();
        config.setCacheEntryExpiryPolicy(new ParameterizedExpiryPolicy<Integer, Integer>(Duration.ETERNAL, null, Duration.ZERO));
        
        Cache<Integer, Integer> cache = getCacheManager().configureCache(getTestCacheName(), config);

        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertTrue(cache.containsKey(1));
        assertEquals((Integer)1, cache.get(1));
        assertEquals((Integer)1, cache.get(1));
        cache.put(1, 2);
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));

        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertEquals((Integer)1, cache.get(1));
        cache.put(1, 2);
        assertFalse(cache.remove(1));

        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertEquals((Integer)1, cache.get(1));
        cache.put(1, 2);
        assertFalse(cache.remove(1, 2));
        
        cache.getAndPut(1, 1);
        assertTrue(cache.containsKey(1));
        assertEquals((Integer)1, cache.get(1));
        cache.put(1, 2);
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));

        cache.getAndPut(1, 1);
        assertTrue(cache.containsKey(1));
        assertEquals((Integer)1, cache.getAndPut(1, 2));
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));

        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertEquals((Integer)1, cache.get(1));
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(1, 2);
        cache.putAll(map);
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));
        
        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertEquals((Integer)1, cache.get(1));
        cache.replace(1, 2);
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));
        
        cache.put(1, 1);
        assertTrue(cache.containsKey(1));
        assertEquals((Integer)1, cache.get(1));
        cache.replace(1, 1, 2);
        assertFalse(cache.containsKey(1));
        assertNull(cache.get(1));

        cache.put(1, 1);
        assertTrue(cache.iterator().hasNext());
        assertEquals((Integer)1, cache.iterator().next().getValue());
        assertTrue(cache.containsKey(1));
        assertEquals((Integer)1, cache.iterator().next().getValue());
        cache.put(1, 2);
        assertFalse(cache.iterator().hasNext());
    }
    
    /**
     * A {@link CacheEntryExpiryPolicy} that updates the expiry time based on 
     * defined parameters.
     */
    public static class ParameterizedExpiryPolicy<K, V> implements CacheEntryExpiryPolicy<K, V> {
        
        /**
         * The {@link Duration} after which a Cache Entry will expire when created.
         */
        private Duration createdExpiryDuration;
        
        /**
         * The {@link Duration} after which a Cache Entry will expire when accessed.
         * (when <code>null</code> the current expiry duration will be used) 
         */
        private Duration accessedExpiryDuration;
        
        /**
         * The {@link Duration} after which a Cache Entry will expire when modified.
         * (when <code>null</code> the current expiry duration will be used) 
         */
        private Duration modifiedExpiryDuration;
        
        /**
         * Constructs an {@link ParameterizedExpiryPolicy}.
         * 
         * @param createdExpiryDuration  the {@link Duration} to expire when an entry is created 
         *                                  (must not be <code>null</code>)
         * @param accessedExpiryDuration the {@link Duration} to expire when an entry is accessed
         *                                  (<code>null</code> means don't change the expiry)
         * @param modifiedExpiryDuration the {@link Duration} to expire when an entry is modified
         *                                  (<code>null</code> means don't change the expiry)
         */
        public ParameterizedExpiryPolicy(Duration createdExpiryDuration, 
                                         Duration accessedExpiryDuration, 
                                         Duration modifiedExpiryDuration) {
            if (createdExpiryDuration == null) {
                throw new NullPointerException("createdExpiryDuration can't be null");
            }
                
            this.createdExpiryDuration = createdExpiryDuration;
            this.accessedExpiryDuration = accessedExpiryDuration;
            this.modifiedExpiryDuration = modifiedExpiryDuration;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Duration getTTLForCreatedEntry(Entry<K, V> entry) {
            return createdExpiryDuration;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Duration getTTLForAccessedEntry(Entry<K, V> entry, Duration duration) {
            return accessedExpiryDuration == null ? duration : accessedExpiryDuration;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Duration getTTLForModifiedEntry(Entry<K, V> entry, Duration duration) {
            return modifiedExpiryDuration == null ? duration : modifiedExpiryDuration;
        }
    }
}

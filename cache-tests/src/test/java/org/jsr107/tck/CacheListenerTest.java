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

import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.CacheManager;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryReadListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for Cache Listeners.
 * <p/>
 * @author Greg Luck
 * @since 1.0
 */
public class CacheListenerTest extends CacheTestSupport<Long, String> {

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


    /**
     * Null listeners are not allowed
     */
    @Test
    public void registerNullCacheEntryListener() {

        try {
            cache.registerCacheEntryListener(null, false, null, true);
        } catch (CacheEntryListenerException e) {
            //expected
        }
    }

    /**
     * Check the listener is getting reads
     */
    @Test
    public void testCacheEntryListener() {
        MyCacheEntryListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
        cache.registerCacheEntryListener(listener, false, null, true);
        assertEquals(0, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());

        cache.put(1l, "dog");
        assertEquals(1, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());

        cache.get(1l);
        assertEquals(1, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(1, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());


    }


    @Test
    public void unregisterCacheEntryListener() {
        CacheEntryReadListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
        cache.registerCacheEntryListener(listener, false, null, true);
        assertFalse(cache.unregisterCacheEntryListener(null));
        assertTrue(cache.unregisterCacheEntryListener(listener));
        assertFalse(cache.unregisterCacheEntryListener(listener));
    }




    /**
     * Test listener
     *
     * @param <K>
     * @param <V>
     */
    static class MyCacheEntryListener<K, V> implements CacheEntryReadListener<K, V>, CacheEntryCreatedListener<K, V>,
            CacheEntryUpdatedListener<K, V>, CacheEntryExpiredListener<K, V>, CacheEntryRemovedListener<K, V> {

        AtomicInteger reads = new AtomicInteger();
        AtomicInteger created = new AtomicInteger();
        AtomicInteger updated = new AtomicInteger();
        AtomicInteger removed = new AtomicInteger();
        AtomicInteger expired = new AtomicInteger();

        ArrayList<CacheEntryEvent<K,V>> entries = new ArrayList<CacheEntryEvent<K, V>>();

        public int getReads() {
            return reads.get();
        }

        public int getCreated() {
            return created.get();
        }

        public int getUpdated() {
            return updated.get();
        }

        public int getRemoved() {
            return removed.get();
        }

        public int getExpired() {
            return expired.get();
        }

        public ArrayList<CacheEntryEvent<K, V>> getEntries() {
            return entries;
        }

        @Override
        public void onCreated(Iterable iterable) throws CacheEntryListenerException {
            created.incrementAndGet();
        }

        @Override
        public void onExpired(Iterable iterable) throws CacheEntryListenerException {
            expired.incrementAndGet();
        }

        @Override
        public void onRemoved(Iterable iterable) throws CacheEntryListenerException {
            removed.incrementAndGet();
        }

        @Override
        public void onUpdated(Iterable iterable) throws CacheEntryListenerException {
            updated.incrementAndGet();
        }

        @Override
        public void onRead(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
            reads.incrementAndGet();
        }
    }
}

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

import javax.cache.Cache;
import javax.cache.Cache.MutableEntry;
import javax.cache.CacheManager;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryReadListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for Cache Listeners.
 * <p/>
 * @author Greg Luck
 * @author Brian Oliver
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

        cache.put(1l, "Sooty");
        assertEquals(1, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());


        Map<Long, String> entries = new HashMap<Long, String>();
        entries.put(2l, "Lucky");
        entries.put(3l, "Prince");
        cache.putAll(entries);
        assertEquals(3, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());

        cache.put(1l, "Sooty");
        assertEquals(3, listener.getCreated());
        assertEquals(1, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());

        cache.putAll(entries);
        assertEquals(3, listener.getCreated());
        assertEquals(3, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());

        cache.getAndPut(4l, "Cody");
        assertEquals(4, listener.getCreated());
        assertEquals(3, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());

        cache.getAndPut(4l, "Cody");
        assertEquals(4, listener.getCreated());
        assertEquals(4, listener.getUpdated());
        assertEquals(1, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());

        String value = cache.get(1l);
        assertEquals(4, listener.getCreated());
        assertEquals(4, listener.getUpdated());
        assertEquals(2, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());
        
        String result = cache.invokeEntryProcessor(1l, new Cache.EntryProcessor<Long, String, String>() {
            @Override
            public String process(MutableEntry<Long, String> entry) {
                return entry.getValue();
            }
        });
        assertEquals(value, result);
        assertEquals(4, listener.getCreated());
        assertEquals(4, listener.getUpdated());
        assertEquals(3, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());        
        
        result = cache.invokeEntryProcessor(1l, new Cache.EntryProcessor<Long, String, String>() {
            @Override
            public String process(MutableEntry<Long, String> entry) {
                entry.setValue("Zoot");
                return entry.getValue();
            }
        });
        assertEquals("Zoot", result);
        assertEquals(4, listener.getCreated());
        assertEquals(5, listener.getUpdated());
        assertEquals(3, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());        
        
        result = cache.invokeEntryProcessor(1l, new Cache.EntryProcessor<Long, String, String>() {
            @Override
            public String process(MutableEntry<Long, String> entry) {
                entry.remove();
                return entry.getValue();
            }
        });
        assertNull(result);
        assertEquals(4, listener.getCreated());
        assertEquals(5, listener.getUpdated());
        assertEquals(3, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(1, listener.getRemoved());        
        
        result = cache.invokeEntryProcessor(1l, new Cache.EntryProcessor<Long, String, String>() {
            @Override
            public String process(MutableEntry<Long, String> entry) {
                entry.setValue("Moose");
                return entry.getValue();
            }
        });
        assertEquals("Moose", result);
        assertEquals(5, listener.getCreated());
        assertEquals(5, listener.getUpdated());
        assertEquals(3, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(1, listener.getRemoved());        
    }


    @Test
    public void unregisterCacheEntryListener() {
        CacheEntryReadListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
        cache.registerCacheEntryListener(listener, false, null, true);
        assertFalse(cache.unregisterCacheEntryListener(null));
        assertTrue(cache.unregisterCacheEntryListener(listener));
        assertFalse(cache.unregisterCacheEntryListener(listener));
    }

    
    @Test
    public void testFilteredListener() {
        MyCacheEntryListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
        
        CacheEntryEventFilter<Long, String> filter = new CacheEntryEventFilter<Long, String>() {
            @Override
            public boolean evaluate(
                    CacheEntryEvent<? extends Long, ? extends String> event)
                    throws CacheEntryListenerException {
                return event.getValue().contains("a") ||
                       event.getValue().contains("e") ||
                       event.getValue().contains("i") ||
                       event.getValue().contains("o") ||
                       event.getValue().contains("u");
            }
        };
        cache.registerCacheEntryListener(listener, false, filter, true);

        assertEquals(0, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());

        cache.put(1l, "Sooty");
        assertEquals(1, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());

        Map<Long, String> entries = new HashMap<Long, String>();
        entries.put(2l, "Lucky");
        entries.put(3l, "Bryn");
        cache.putAll(entries);
        assertEquals(2, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());
        
        cache.put(1l, "Zyn");
        assertEquals(2, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(0, listener.getRemoved());
        
        cache.remove(2l);
        assertEquals(2, listener.getCreated());
        assertEquals(0, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(1, listener.getRemoved());
        
        cache.replace(1l, "Fred");
        assertEquals(2, listener.getCreated());
        assertEquals(1, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(1, listener.getRemoved());
        
        cache.replace(3l, "Bryn", "Sooty");
        assertEquals(2, listener.getCreated());
        assertEquals(2, listener.getUpdated());
        assertEquals(0, listener.getReads());
        assertEquals(0, listener.getExpired());
        assertEquals(1, listener.getRemoved());
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
        public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends K, ? extends V> event : events) {
                created.incrementAndGet();
            }
        }

        @Override
        public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends K, ? extends V> event : events) {
                expired.incrementAndGet();
            }
        }

        @Override
        public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends K, ? extends V> event : events) {
                removed.incrementAndGet();
            }
        }

        @Override
        public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends K, ? extends V> event : events) {
                updated.incrementAndGet();
            }
        }

        @Override
        public void onRead(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) throws CacheEntryListenerException {
            for (CacheEntryEvent<? extends K, ? extends V> event : events) {
                reads.incrementAndGet();
            }
        }
    }
}

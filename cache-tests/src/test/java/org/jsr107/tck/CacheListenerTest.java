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

import domain.Beagle;
import domain.Identifier;
import manager.CacheNameOnEachMethodBlogManagerImpl;
import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.Cache;
import javax.cache.CacheConfiguration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.Status;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryReadListener;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    public void registerCacheEntryListener() {
        MyCacheEntryListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
        cache.registerCacheEntryListener(listener, false, null, true);
        cache.put(1l, "dog");
        cache.get(1l);
        assertEquals(1, listener.getReads());
    }


    @Test
    public void unregisterCacheEntryListener() {
        CacheEntryReadListener<Long, String> listener = new MyCacheEntryListener<Long, String>();
        cache.registerCacheEntryListener(listener, false, null, true);
        assertFalse(cache.unregisterCacheEntryListener(null));
        assertTrue(cache.unregisterCacheEntryListener(listener));
        assertFalse(cache.unregisterCacheEntryListener(listener));
    }



    // ---------- utilities ----------

    /**
     * Test listener
     *
     * @param <K>
     * @param <V>
     */
    static class MyCacheEntryListener<K, V> implements CacheEntryReadListener<K, V> {

        AtomicInteger reads = new AtomicInteger();

        ArrayList<CacheEntryEvent<K,V>> entries = new ArrayList<CacheEntryEvent<K, V>>();


        /**
         * Called after the entry has been read. If no entry existed for the key the event is not called.
         * This method is not called if a batch operation was performed.
         *
         * @param event The event just read.
         */
        @Override
        public void entryRead(CacheEntryEvent<? extends K, ? extends V> event) {
            reads.incrementAndGet();
        }

        public int getReads() {
            return reads.get();
        }

        public ArrayList<CacheEntryEvent<K, V>> getEntries() {
            return entries;
        }
    }
}

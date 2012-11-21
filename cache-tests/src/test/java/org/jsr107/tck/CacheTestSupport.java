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

import java.util.Date;
import java.util.LinkedHashMap;

import javax.cache.Cache;

import org.jsr107.tck.util.TCKCacheConfiguration;
import org.junit.After;
import org.junit.Before;

/**
 * Unit test support base class
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public abstract class CacheTestSupport<K,V> extends TestSupport {

    protected Cache<K,V> cache;

    @Before
    public void setUp() {
        cache = getCacheManager().configureCache(getTestCacheName(), extraSetup(new TCKCacheConfiguration<K, V>()));
    }

    @After
    public void teardown() {
        getCacheManager().removeCache(getTestCacheName());
    }

    protected <A, B> TCKCacheConfiguration<A, B> extraSetup(TCKCacheConfiguration<A, B> configuration) {
        return configuration;
    }

    private LinkedHashMap<Long, String> createLSData(int count, long now) {
        LinkedHashMap<Long, String> map = new LinkedHashMap<Long, String>(count);
        for (int i = 0; i < count; i++) {
            Long key = now + i;
            map.put(key, "value" + key);
        }
        return map;
    }

    private LinkedHashMap<Date, Date> createDDData(int count, long now) {
        LinkedHashMap<Date, Date> map = new LinkedHashMap<Date, Date>(count);
        for (int i = 0; i < count; i++) {
            map.put(new Date(now + i), new Date(now + 1000 + i));
        }
        return map;
    }

    protected LinkedHashMap<Date, Date> createDDData(int count) {
        return createDDData(count, System.currentTimeMillis());
    }

    protected LinkedHashMap<Long, String> createLSData(int count) {
        return createLSData(count, System.currentTimeMillis());
    }
}

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

import org.junit.Test;

import javax.cache.implementation.RICacheConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Unit tests for CacheBuilder
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.7
 */
public class CacheBuilderTest {
    private static final String CACHE_NAME = "foo";

    @Test
    public void createCache_1NullCacheName() {
        CacheBuilder builder = getCacheBuilder();
        try {
            builder.createCache(null);
            fail("should have thrown an exception - null cache name not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void createCache_1NameOK() {
        CacheBuilder builder = getCacheBuilder();
        String name = CACHE_NAME;
        Cache cache = builder.createCache(name);
        assertEquals(name, cache.getCacheName());
    }

    @Test
    public void createCache_1StatusOK() {
        CacheBuilder builder = getCacheBuilder();
        Cache cache = builder.createCache(CACHE_NAME);
        assertSame(Status.UNITIALISED, cache.getStatus());
    }

    @Test
    public void createCache_2NullCacheName() {
        CacheBuilder builder = getCacheBuilder();
        try {
            builder.createCache(null, getCacheConfiguration());
            fail("should have thrown an exception - null cache name not allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void createCache_2NullCacheConfiguration() {
        CacheBuilder builder = getCacheBuilder();
        try {
            builder.createCache(CACHE_NAME, null);
            fail("should have thrown an exception - null cache configurationnot allowed");
        } catch (NullPointerException e) {
            //good
        }
    }

    @Test
    public void createCache_2NameOK() {
        CacheBuilder builder = getCacheBuilder();
        String name = CACHE_NAME;
        Cache cache = builder.createCache(name, getCacheConfiguration());
        assertEquals(name, cache.getCacheName());
    }

    @Test
    public void createCache_2StatusOK() {
        CacheBuilder builder = getCacheBuilder();
        Cache cache = builder.createCache(CACHE_NAME, getCacheConfiguration());
        assertSame(Status.UNITIALISED, cache.getStatus());
    }

    // ---------- utilities ----------

    private CacheBuilder getCacheBuilder() {
        return TestInstanceFactory.getInstance().getCacheBuilder();
    }

    private CacheConfiguration getCacheConfiguration() {
        return new RICacheConfiguration.Builder().build();
    }
}

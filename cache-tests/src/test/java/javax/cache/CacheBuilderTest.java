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

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.transaction.IsolationLevel;
import javax.cache.transaction.Mode;
import javax.cache.util.ExcludeListExcluder;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for CacheBuilder
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheBuilderTest {
    protected static final String CACHE_NAME = "testCache";

    @After
    public void cleanup() {
        Caching.getCacheManager().removeCache(CACHE_NAME);
    }

    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

    @Test
    public void setCacheLoader_Null() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        CacheLoader<Integer, String> cl = null;
        try {
            builder.setCacheLoader(cl);
            fail();
        } catch (NullPointerException e) {
            // good
        }
    }

    @Test
    public void setReadThrough_NoCacheLoader() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        builder.setReadThrough(true);
        try {
            builder.build(); // cache loader is null
            fail();
        } catch (InvalidConfigurationException e) {
            // good
        }
    }

    @Test
    public void setCacheWriter_Null() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        CacheWriter<Integer, String> cw = null;
        try {
            builder.setCacheWriter(cw);
            fail();
        } catch (NullPointerException e) {
            // good
        }
    }

    @Test
    public void setWriteThrough_NoCacheWriter() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        builder.setWriteThrough(true);
        try {
            builder.build(); // cache writer is null
            fail();
        } catch (InvalidConfigurationException e) {
            // good
        }


    }

    @Test
    public void testValidCacheNames() {

        try {
            Caching.getCacheManager().createCacheBuilder(null).build();
        } catch (NullPointerException e) {
            //expected
        }


        try {
            Caching.getCacheManager().createCacheBuilder("").build();
        } catch (IllegalArgumentException e) {
            //expected
        }

        try {
            Caching.getCacheManager().createCacheBuilder(" ").build();
        } catch (IllegalArgumentException e) {
            //expected
        }


        try {
            Caching.getCacheManager().createCacheBuilder("    ").build();
        } catch (IllegalArgumentException e) {
            //expected
        }

        //all the whitespace characters except carriage return.
        try {
            Caching.getCacheManager().createCacheBuilder("\u0009\u000B\u000C\u001D\u001E\u001F").build();
        } catch (IllegalArgumentException e) {
            //expected
        }

        //and now a valid one
        Caching.getCacheManager().createCacheBuilder("Greg Luck's Cache").build();
        Caching.getCacheManager().createCacheBuilder("G").build();
        Caching.getCacheManager().createCacheBuilder(" G ").build();


    }


    @Test
    public void setTransactionEnabled() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        IsolationLevel isolationLevel = null;
        Mode mode = null;
        if (isSupported(OptionalFeature.TRANSACTIONS)) {
            Cache<Integer, String> cache = builder.setTransactionEnabled(isolationLevel, mode).build();
            assertTrue(cache.getConfiguration().isTransactionEnabled());
        } else {
            try {
                builder.setTransactionEnabled(isolationLevel, mode);
                fail("expected InvalidConfigurationException");
            } catch (InvalidConfigurationException e) {
                //
            }
        }
    }

    @Test
    public void setStoreByValue_false() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        if (isSupported(OptionalFeature.STORE_BY_REFERENCE)) {
            Cache<Integer, String> cache = builder.setStoreByValue(false).build();
            assertFalse(cache.getConfiguration().isStoreByValue());
        } else {
            try {
                builder.setStoreByValue(false);
                fail();
            } catch (InvalidConfigurationException e) {
                //
            }
        }
    }

    @Test
    public void setExpiry_null_good() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        try {
            builder.setExpiry(null, CacheConfiguration.Duration.ETERNAL);
            fail();
        } catch(NullPointerException e) {
            //
        }
    }

    @Test
    public void setExpiry_good_null() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        try {
            builder.setExpiry(CacheConfiguration.ExpiryType.MODIFIED, null);
            fail();
        } catch(NullPointerException e) {
            //
        }
    }

    @Test
    public void getExpiry_default() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        CacheConfiguration configuration = builder.build().getConfiguration();
        for (CacheConfiguration.ExpiryType type : CacheConfiguration.ExpiryType.values()) {
            assertEquals(CacheConfiguration.Duration.ETERNAL, configuration.getExpiry(type));
        }
    }

    @Test
    public void setExpiry_accessed() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        CacheConfiguration.ExpiryType type = CacheConfiguration.ExpiryType.ACCESSED;
        CacheConfiguration.Duration duration = new CacheConfiguration.Duration(TimeUnit.MINUTES, 4L);
        builder.setExpiry(type, duration);
        CacheConfiguration configuration = builder.build().getConfiguration();
        assertEquals(duration, configuration.getExpiry(type));
        assertEquals(CacheConfiguration.Duration.ETERNAL, configuration.getExpiry(CacheConfiguration.ExpiryType.MODIFIED));
    }

    @Test
    public void setExpiry_modified() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        CacheConfiguration.ExpiryType type = CacheConfiguration.ExpiryType.MODIFIED;
        CacheConfiguration.Duration duration = new CacheConfiguration.Duration(TimeUnit.HOURS, 4L);
        builder.setExpiry(type, duration);
        CacheConfiguration configuration = builder.build().getConfiguration();
        assertEquals(duration, configuration.getExpiry(type));
        assertEquals(CacheConfiguration.Duration.ETERNAL, configuration.getExpiry(CacheConfiguration.ExpiryType.ACCESSED));
    }

    @Test
    public void setExpiry_both() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        CacheConfiguration.Duration[] durations = new CacheConfiguration.Duration[CacheConfiguration.ExpiryType.values().length];
        for (CacheConfiguration.ExpiryType type : CacheConfiguration.ExpiryType.values()) {
            CacheConfiguration.Duration duration = new CacheConfiguration.Duration(TimeUnit.DAYS, 4L + type.ordinal());
            builder.setExpiry(type, duration);
            durations[type.ordinal()] = duration;
        }
        CacheConfiguration configuration = builder.build().getConfiguration();
        for (CacheConfiguration.ExpiryType type : CacheConfiguration.ExpiryType.values()) {
            assertEquals(durations[type.ordinal()], configuration.getExpiry(type));
        }
    }

    // ---------- utilities ----------

    protected <K, V> CacheBuilder<K, V> getCacheBuilder() {
        return Caching.getCacheManager().createCacheBuilder(CACHE_NAME);
    }

    protected boolean isSupported(OptionalFeature feature) {
        return Caching.isSupported(feature);
    }
}

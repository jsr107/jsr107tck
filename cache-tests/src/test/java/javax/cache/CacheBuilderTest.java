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
    public void setTransactionEnabled_true() {
        CacheBuilder<Integer, String> builder = getCacheBuilder();
        if (isSupported(OptionalFeature.JTA)) {
            Cache<Integer, String> cache = builder.setTransactionEnabled(true).build();
            assertTrue(cache.getConfiguration().isTransactionEnabled());
        } else {
            try {
                builder.setTransactionEnabled(true);
                fail();
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

    // ---------- utilities ----------

    protected <K, V> CacheBuilder<K, V> getCacheBuilder() {
        return Caching.getCacheManager().createCacheBuilder(CACHE_NAME);
    }

    protected boolean isSupported(OptionalFeature feature) {
        return Caching.isSupported(feature);
    }
}

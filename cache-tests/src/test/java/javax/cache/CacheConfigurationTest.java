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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.util.ExcludeListExcluder;
import javax.xml.datatype.Duration;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for CacheBuilder
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheConfigurationTest {
    private static final String CACHE_NAME = "testCache";

    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

    @Before
    public void startUp() {
        Caching.close();
    }

    @Test
    public void checkDefaults() {
        CacheConfiguration config = getCacheConfiguration(CACHE_NAME + "0");
        assertFalse(config.isReadThrough());
        assertFalse(config.isWriteThrough());
        assertFalse(config.isStatisticsEnabled());
        assertEquals(CacheConfiguration.Duration.ETERNAL, config.getExpiry());
        assertEquals(CacheConfiguration.Size.UNLIMITED, config.getSize());
        assertTrue(config.isStoreByValue());
    }

    @Test
    public void notSame() {
        CacheConfiguration config1 = getCacheConfiguration(CACHE_NAME + "1");
        CacheConfiguration config2 = getCacheConfiguration(CACHE_NAME + "2");
        assertNotSame(config1, config2);
    }

    @Test
    public void equals() {
        CacheConfiguration config1 = getCacheConfiguration(CACHE_NAME + "1");
        CacheConfiguration config2 = getCacheConfiguration(CACHE_NAME + "2");
        assertEquals(config1, config2);
    }

    @Test
    public void equalsNotEquals() {
        CacheConfiguration config1 = getCacheConfiguration(CACHE_NAME + "1");
        config1.setStatisticsEnabled(!config1.isStatisticsEnabled());
        CacheConfiguration config2 = getCacheConfiguration(CACHE_NAME + "2");
        assertFalse(config1.equals(config2));
    }

    @Test
    public void setStatisticsEnabled() {
        CacheConfiguration config = getCacheConfiguration(CACHE_NAME);
        boolean flag = config.isStatisticsEnabled();
        config.setStatisticsEnabled(!flag);
        assertEquals(!flag, config.isStatisticsEnabled());
    }

    @Test
    public void setReadThrough() {
        CacheConfiguration config = getCacheConfiguration(CACHE_NAME);
        boolean flag = config.isReadThrough();
        config.setReadThrough(!flag);
        assertEquals(!flag, config.isReadThrough());
    }

    @Test
    public void setWriteThrough() {
        CacheConfiguration config = getCacheConfiguration(CACHE_NAME);
        boolean flag = config.isWriteThrough();
        config.setWriteThrough(!flag);
        assertEquals(!flag, config.isWriteThrough());
    }

    @Test
    public void setExpiry() {
        CacheConfiguration config = getCacheConfiguration(CACHE_NAME);
        CacheConfiguration.Duration duration = new CacheConfiguration.Duration(TimeUnit.MINUTES, 666);
        config.setExpiry(duration);
        CacheConfiguration.Duration duration1 = config.getExpiry();
        assertEquals(duration, duration1);
    }

    @Test
    public void setExpiry_null() {
        CacheConfiguration config = getCacheConfiguration(CACHE_NAME);
        try {
            config.setExpiry(null);
            fail();
        } catch(NullPointerException e) {
            //
        }
    }

    @Test
    public void setSize() {
        CacheConfiguration config = getCacheConfiguration(CACHE_NAME);
        CacheConfiguration.Size size = new CacheConfiguration.Size(CacheConfiguration.Size.Unit.GIGABYTES, 666);
        config.setSize(size);
        CacheConfiguration.Size size1 = config.getSize();
        assertEquals(size, size1);
    }

    @Test
    public void setSize_null() {
        CacheConfiguration config = getCacheConfiguration(CACHE_NAME);
        try {
            config.setSize(null);
            fail();
        } catch(NullPointerException e) {
            //
        }
    }

    // ---------- utilities ----------

    private CacheConfiguration getCacheConfiguration(String cacheName) {
        Cache cache = getCacheManager().getCache(cacheName);
        if (cache == null) {
            cache = getCacheManager().createCacheBuilder(cacheName).build();
        }
        return cache.getConfiguration();
    }

    private static CacheManager getCacheManager() {
        return Caching.getCacheManager();
    }
}

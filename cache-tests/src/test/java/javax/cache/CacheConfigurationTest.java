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
        for (CacheConfiguration.ExpiryType type : CacheConfiguration.ExpiryType.values()) {
            assertEquals(CacheConfiguration.Duration.ETERNAL, config.getExpiry(type));
        }
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
    public void DurationEquals() {
        CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(TimeUnit.DAYS, 2);
        CacheConfiguration.Duration duration2 = new CacheConfiguration.Duration(TimeUnit.DAYS, 2);
        assertEquals(duration1, duration2);
    }


    @Test
    public void durationNotEqualsAmount() {
        CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(TimeUnit.DAYS, 2);
        CacheConfiguration.Duration duration2 = new CacheConfiguration.Duration(TimeUnit.DAYS, 3);
        assertFalse(duration1.equals(duration2));
        assertFalse(duration1.hashCode() == duration2.hashCode());
    }

    @Test
    public void durationNotEqualsUnit() {
        CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(TimeUnit.DAYS, 2);
        CacheConfiguration.Duration duration2 = new CacheConfiguration.Duration(TimeUnit.MINUTES, 2);
        assertFalse(duration1.equals(duration2));
        assertFalse(duration1.hashCode() == duration2.hashCode());

    }

    /**
     * Checks that equals() is semantically meaningful.
     *
     * Also verifies the second requirement in the contract of hashcode:
     * * <li>If two objects are equal according to the <tt>equals(Object)</tt>
     *     method, then calling the <code>hashCode</code> method on each of
     *     the two objects must produce the same integer result.
     */
    @Test
    public void durationEqualsWhenSemanticallyEqualsButExpressedDifferentUnits() {
        CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(TimeUnit.SECONDS, 120);
        CacheConfiguration.Duration duration2 = new CacheConfiguration.Duration(TimeUnit.MINUTES, 2);
        assertEquals(duration1, duration2);
        assertEquals(duration1.hashCode(), duration2.hashCode());
    }

    @Test
    public void durationEqualsWhenSemanticallyEqualsButExpressedDifferentUnitsHashCode() {
        CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(TimeUnit.SECONDS, 120);
        CacheConfiguration.Duration duration2 = new CacheConfiguration.Duration(TimeUnit.MINUTES, 2);
        assertEquals(duration1, duration2);
        assertEquals(duration1.hashCode(), duration2.hashCode());
    }


    @Test
    public void durationNotEqualsUnitEquals() {
        long time = 2;
        CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(TimeUnit.HOURS, 2);
        time *= 60;
        CacheConfiguration.Duration duration2 = new CacheConfiguration.Duration(TimeUnit.MINUTES, 120);
        assertEquals(duration1, duration2);
        time *= 60;
        duration2 = new CacheConfiguration.Duration(TimeUnit.SECONDS, time);
        assertEquals(duration1, duration2);
        time *= 1000;
        duration2 = new CacheConfiguration.Duration(TimeUnit.MILLISECONDS, time);
        assertEquals(duration1, duration2);
    }


    @Test
    public void DurationExceptions() {
        try {
            CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(null, 2);
        } catch (NullPointerException e) {
            //expected
        }

        try {
            CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(TimeUnit.MINUTES, 0);
        } catch (NullPointerException e) {
            //expected
        }


        try {
            CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(TimeUnit.MICROSECONDS, 10);
        } catch (IllegalArgumentException e) {
            //expected
        }

        try {
            CacheConfiguration.Duration duration1 = new CacheConfiguration.Duration(TimeUnit.MILLISECONDS, -10);
        } catch (IllegalArgumentException e) {
            //expected
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

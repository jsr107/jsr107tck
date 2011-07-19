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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for CacheBuilder
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheConfigurationTest {

    @Test
    public void checkDefaults() {
        CacheConfiguration config = getCacheConfiguration();
        assertFalse(config.isReadThrough());
        assertFalse(config.isWriteThrough());
        assertFalse(config.isStatisticsEnabled());
        assertTrue(config.isStoreByValue());
    }

    @Test
    public void notSame() {
        assertNotSame(getCacheConfiguration(), getCacheConfiguration());
    }

    @Test
    public void equals() {
        assertEquals(getCacheConfiguration(), getCacheConfiguration());
    }

    @Test
    public void equalsNotEquals() {
        CacheConfiguration config1 = getCacheConfiguration();
        config1.setReadThrough(!config1.isReadThrough());
        CacheConfiguration config2 = getCacheConfiguration();
        assertFalse(config1.equals(config2));
    }

    @Test
    public void setReadThrough() {
        CacheConfiguration config = getCacheConfiguration();
        boolean flag = config.isReadThrough();
        config.setReadThrough(!flag);
        assertEquals(!flag, config.isReadThrough());
    }

    @Test
    public void setStoreByValue() {
        CacheConfiguration config = getCacheConfiguration();
        boolean flag = config.isStoreByValue();
        config.setStoreByValue(!flag);
        assertEquals(!flag, config.isStoreByValue());
    }

    @Test
    public void setWriteThrough() {
        CacheConfiguration config = getCacheConfiguration();
        boolean flag = config.isWriteThrough();
        config.setWriteThrough(!flag);
        assertEquals(!flag, config.isWriteThrough());
    }

    // ---------- utilities ----------

    private CacheConfiguration getCacheConfiguration() {
        return TestInstanceFactory.INSTANCE.createCacheConfiguration();
    }
}

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

import org.junit.Rule;
import org.junit.Test;

import javax.cache.util.TestExcluder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * @author Yannis Cosmadopoulos
 * @since 1.7
 */
public class CacheManagerFactoryTest {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public TestExcluder rule = new TestExcluder(this.getClass());

    /**
     * The CacheManagerFactory.getCacheManager always returns a singleton CacheManager
     */
    @Test
    public void getCacheManager() {
        CacheManager defaultCacheManager = CacheManagerFactory.INSTANCE.getCacheManager();
        assertNotNull(defaultCacheManager);
        assertSame(defaultCacheManager, CacheManagerFactory.INSTANCE.getCacheManager());
    }

    /**
     * Tests that we can create non singleton CacheManagers
     */
    @Test
    public void createCacheManager() {
        CacheManager newCacheManager = CacheManagerFactory.INSTANCE.createCacheManager();
        assertNotNull(newCacheManager);
        assertNotSame(newCacheManager, CacheManagerFactory.INSTANCE.createCacheManager());
    }

    @Test
    public void createCacheManager_notDefault() {
        assertNotSame(CacheManagerFactory.INSTANCE.getCacheManager(), CacheManagerFactory.INSTANCE.createCacheManager());
    }

    @Test
    public void dummyTest() {
        fail();
    }
}

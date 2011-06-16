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

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;

import javax.cache.implementation.RICacheManager;

/**
 * Unit tests for CacheManager
 * <p/>
 *
 * Implementers of Cache should subclass this test, overriding {@link #getCacheManager()}
 *
 * @author ycosmado
 * @since 1.0
 */
public class CacheManagerTest {


    /**
     * Checks that stop is called on all caches, even after 1 throws an exception
     * @param cache1 a mock cache that throws CacheException on stop
     * @param cache2 a mock cache
     */
    @Test
    public void shutdown(@Mocked final Cache cache1, @Mocked final Cache cache2) {
        CacheManager cacheManager = getCacheManager();
        cacheManager.addCache("c1", cache1);
        cacheManager.addCache("c2", cache2);
        new Expectations() {{
            cache1.stop(); times = 1; result = new CacheException("something bad stopping 1");
            cache2.stop(); times = 1; result = new CacheException("something bad stopping 2");
        }};
        cacheManager.shutdown();
    }

    /**
     * Sub classes should override this to get a different CacheManager
     *
     * @return a cache manager
     */
    protected CacheManager getCacheManager() {
        return RICacheManager.instance;
    }
}

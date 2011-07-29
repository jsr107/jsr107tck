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

import java.util.logging.Logger;

/**
 * Unit test support base class
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 */
class TestSupport {

    /**
     * the logger
     */
    protected final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * the default test cache name
     */
    protected static final String CACHE_NAME = "testCache";

    protected CacheConfiguration createCacheConfiguration() {
        return CacheManagerFactory.INSTANCE.createCacheConfiguration();
    }

    protected CacheManager getCacheManager() {
        return CacheManagerFactory.INSTANCE.getCacheManager();
    }

    protected CacheManager getCacheManager(String name) {
        return CacheManagerFactory.INSTANCE.getCacheManager(name);
    }

    protected <K, V> Cache<K, V> createCache() {
        return getCacheManager().
                <K, V>createCacheBuilder(CACHE_NAME).
                build();
    }

    protected <K, V> Cache<K, V> createCache(CacheLoader<K, V> cacheLoader) {
        return getCacheManager().
                <K, V>createCacheBuilder(CACHE_NAME).
                setCacheLoader(cacheLoader).
                build();
    }

    protected <K, V> Cache<K, V> createCache(CacheConfiguration config) {
        return getCacheManager().
                <K, V>createCacheBuilder(CACHE_NAME).
                setCacheConfiguration(config).
                build();
    }

    protected <K, V> Cache<K, V> createOrphanCache(String name) {
        return CacheManagerFactory.INSTANCE.createCache(name);
    }
}

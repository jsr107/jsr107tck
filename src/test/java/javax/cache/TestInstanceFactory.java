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

import javax.cache.implementation.RICache;
import javax.cache.implementation.RICacheBuilder;
import javax.cache.implementation.RICacheManager;

/**
 * Singleton factory for test instances.
 * By default the factory uses RI instances.
 * To customize for a different class, set the system property defined by {@link #FACTORY_CLASS}.
 * <p/>
 *
 * @author ycosmado
 * @since 1.0
 */
public class TestInstanceFactory implements InstanceFactory {
    /**
     * the singleton
     */
    private static final TestInstanceFactory INSTANCE = new TestInstanceFactory();

    private final InstanceFactory factory = createFactory();

    /**
     * name of the system property to set the factory class name
     */
    public static final String FACTORY_CLASS = "FactoryClass";

    public static final InstanceFactory getInstance() {
        return INSTANCE;
    }

    private InstanceFactory createFactory() {
        String className = System.getProperty(FACTORY_CLASS, RIInstanceFactory.class.getName());
        try {
            return (InstanceFactory) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CacheBuilder getCacheBuilder() {
        return factory.getCacheBuilder();
    }

    public CacheManager getCacheManager() {
        return factory.getCacheManager();
    }

    public <K, V> Cache<K, V> createCache(String cacheName, CacheConfiguration config, CacheLoader<K, V> cacheLoader) {
        return factory.createCache(cacheName, config, cacheLoader);
    }

    /**
     * factory for RI instances
     */
    public static class RIInstanceFactory implements InstanceFactory {
        public CacheBuilder getCacheBuilder() {
            return RICacheBuilder.instance;
        }

        public CacheManager getCacheManager() {
            return RICacheManager.instance;
        }

        public <K, V> Cache<K, V> createCache(String cacheName, CacheConfiguration config, CacheLoader<K, V> cacheLoader) {
            RICache.Builder<K, V> builder = new RICache.Builder<K, V>();
            if (config != null) {
                builder.setCacheConfiguration(config);
            }
            if (cacheLoader != null) {
                builder.setCacheLoader(cacheLoader);
            }
            if (cacheName != null) {
                builder.setCacheName(cacheName);
            }
            return builder.build();
        }
    }
}

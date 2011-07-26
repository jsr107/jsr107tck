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
import javax.cache.implementation.RICacheConfiguration;

/**
 * Singleton factory for test instances.
 * By default the factory uses RI instances.
 * To customize for a different class, set the system property defined by {@link #FACTORY_CLASS}.
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public enum TestInstanceFactory implements InstanceFactory {
    /**
     * the singleton
     */
    INSTANCE;

    private final InstanceFactory factory = createFactory();

    /**
     * name of the system property to set the factory class name
     */
    public static final String FACTORY_CLASS = "test.cache.FactoryClass";

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
    /**
     * {@inheritDoc}
     */
    public <K, V> Cache<K, V> createCache(String name) {
        return factory.createCache(name);
    }

    /**
     * {@inheritDoc}
     */
    public CacheConfiguration createCacheConfiguration() {
        return factory.createCacheConfiguration();
    }

    /**
     * factory for RI instances
     */
    public static class RIInstanceFactory implements InstanceFactory {

        /**
         * Will return an RI implementation.
         *
         * {@inheritDoc}
         */
        public <K, V> Cache<K, V> createCache(String name) {
            return new RICache.Builder<K, V>(name).build();
        }

        /**
         * Will return an RI implementation.
         *
         * {@inheritDoc}
         */
        public CacheConfiguration createCacheConfiguration() {
            return new RICacheConfiguration.Builder().build();
        }
    }
}

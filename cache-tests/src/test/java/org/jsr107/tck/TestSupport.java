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
package org.jsr107.tck;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Unit test support base class
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class TestSupport {

    /**
     * The logger
     */
    protected static final Logger LOG = Logger.getLogger(TestSupport.class.getName());

    private final Map<Class<?>, Class<?>> unwrapClasses = Collections.synchronizedMap(new HashMap<Class<?>, Class<?>>());
    private Properties unwrapProperties;

    protected CacheManager getCacheManager() {
        return Caching.getCachingProvider().getCacheManager();
    }

    protected String getTestCacheName() {
        return getClass().getName();
    }

    protected Class<?> getUnwrapClass(Class<?> unwrappableClass) {
        //contains check since null values are allowed
        if (this.unwrapClasses.containsKey(unwrappableClass)) {
            return this.unwrapClasses.get(unwrappableClass);
        }

        final Properties unwrapProperties = getUnwrapProperties();
        final String unwrapClassName = unwrapProperties.getProperty(unwrappableClass.getName());
        if (unwrapClassName == null || unwrapClassName.trim().length() == 0) {
            this.unwrapClasses.put(unwrappableClass, null);
            return null;
        }

        try {
            final Class<?> unwrapClass = Class.forName(unwrapClassName);
            this.unwrapClasses.put(unwrappableClass, unwrapClass);
            return unwrapClass;
        }
        catch (ClassNotFoundException e) {
            LOG.warning("Failed to load unwrap class " + unwrapClassName + " for unwrappable class: " + unwrappableClass);
            this.unwrapClasses.put(unwrappableClass, null);
            return null;
        }

    }

    private Properties getUnwrapProperties() {
        if (this.unwrapProperties != null) {
            return this.unwrapProperties;
        }

        final Properties unwrapProperties = new Properties();
        try {
            unwrapProperties.load(getClass().getResourceAsStream("/unwrap.properties"));
        }
        catch (IOException e) {
            LOG.warning("Failed to load unwrap.properties from classpath");
        }

        this.unwrapProperties = unwrapProperties;
        return unwrapProperties;
    }
}

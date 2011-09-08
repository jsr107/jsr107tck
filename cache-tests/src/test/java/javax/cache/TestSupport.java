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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Unit test support base class
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
class TestSupport {

    /**
     * The logger
     */
    protected static final Logger LOG = Logger.getLogger(TestSupport.class.getName());

    /**
     * the default test cache name
     */
    protected static final String CACHE_NAME = "testCache";

    protected CacheManager getCacheManager() {
        return CacheManagerFactory.getCacheManager();
    }

    protected CacheManager getCacheManager(String name) {
        return CacheManagerFactory.getCacheManager(name);
    }
}

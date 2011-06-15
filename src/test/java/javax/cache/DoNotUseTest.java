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

import junit.framework.Assert;
import org.junit.Test;

import javax.cache.implementation.RICache;

/**
 * Unit tests to demonstrate some open decisions on semantics w.r.t.
 * storing null in key/value of a cache
 *
 * The open issues are
 * <ol>
 *  <li>what is the behavior on attempting to read using key == null</li>
 *  <li>what is the behavior when storing a null key</li>
 *  <li>if storing a null value is allowed, what is the effect on get</li>
 * </ol>
 * The tests below should illustrate the options.
 * @author yannisc@gmail.com
 */
public class DoNotUseTest {

    /**
     * An attempt to get a value with key null causes a
     * NullPointerException
     */
    @Test
    public void testGetNullKeyV1(){
        Cache<Integer, String> cache =
                new RICache.Builder<Integer, String>().
                        build();
        cache.start();

        Integer key = null;
        try {
            cache.get(key);
            Assert.fail("get with key==null should throw NPE");
        } catch (NullPointerException e) {
            // NPE as expected
        }
    }

    /**
     * An attempt to put using a null key causes a
     * NullPointerException
     */
    @Test
    public void testPutNullKeyV1(){
        Cache<Integer, String> cache =
                new RICache.Builder<Integer, String>().
                        build();
        cache.start();

        Integer key = null;
        String value = "v1";
        try {
            cache.put(key, value);
            Assert.fail("put with key==null should throw NPE");
        } catch (NullPointerException e) {
            // NPE as expected
        }
    }

    /**
     * An attempt to put using a null value causes a
     * NullPointerException
     */
    @Test
    public void testPutNullValueV1(){
        Cache<Integer, String> cache =
                new RICache.Builder<Integer, String>().
                        build();
        cache.start();

        // we don't allow null in value
        Integer key = 1;
        try {
            cache.put(key, null);
            Assert.fail("put with value==null should throw NPE");
        } catch (NullPointerException e) {
            // NPE as expected
        }
        Assert.assertFalse(cache.containsKey(key));
        Assert.assertNull(cache.get(key));
    }
}

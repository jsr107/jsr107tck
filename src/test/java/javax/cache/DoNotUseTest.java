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
                        setIgnoreNullKeyOnRead(false).
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
     * An attempt to get a value with key null returns null.
     */
    @Test
    public void testGetNullKeyV2(){
        Cache<Integer, String> cache =
                new RICache.Builder<Integer, String>().
                        setIgnoreNullKeyOnRead(true).
                        build();
        cache.start();

        Integer key = null;
        Assert.assertNull(cache.get(key));
    }

    /**
     * An attempt to put using a null key causes a
     * NullPointerException
     */
    @Test
    public void testPutNullKeyV1(){
        Cache<Integer, String> cache =
                new RICache.Builder<Integer, String>().
                        setIgnoreNullKeyOnRead(true).
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
                        setAllowNullValue(false).
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

    /**
     * An attempt to put using a null value stores the null
     */
    @Test
    public void testPutNullValueV2(){
        Cache<Integer, String> cache =
                new RICache.Builder<Integer, String>().
                        setAllowNullValue(true).
                        build();
        cache.start();

        // We allow null in value
        Integer key = 1;
        cache.put(key, null);
        Assert.assertTrue(cache.containsKey(key));
        Assert.assertNull(cache.get(key));
        cache.remove(key);
        Assert.assertFalse(cache.containsKey(key));
        Assert.assertNull(cache.get(key));
        /*
         note: cache.get(key) == null can mean
         1) there is no entry with key
         2) there is an entry with key with value==null
        */
    }

    /**
     * An attempt to put using a null value stores the null.
     *
     * This solution changes get to return Cache.Entry<K, V> rather than K.
     * For this test I use a private method {@link #get(Object, Cache)} which
     * stands in for a Cache.get returning Entry.
     */
    @Test
    public void testPutNullValueV3(){
        Cache<Integer, String> cache =
                new RICache.Builder<Integer, String>().
                        setAllowNullValue(true).
                        build();
        cache.start();

        // We allow null in value, but get returns an entry
        Integer key = 1;
        cache.put(key, null);
        Assert.assertTrue(cache.containsKey(key));
        Cache.Entry<Integer, String> entry = get(key, cache);
        Assert.assertEquals(key, entry.getKey());
        Assert.assertNull(entry.getValue());
        cache.remove(key);
        Assert.assertFalse(cache.containsKey(key));
        Assert.assertNull(get(key, cache));
        /*
         note: cache.get(key) == null can only mean
         1) there is no entry with key
        */
    }

    private <K,V> Cache.Entry<K, V> get(final K key, Cache<K, V> cache) {
        if (!cache.containsKey(key)) {
            return null;
        } else {
            final V value = cache.get(key);
            return new Cache.Entry<K, V>() {
                public K getKey() {
                    return key;
                }

                public V getValue() {
                    return value;
                }
            };
        }
    }
}

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

package org.jsr107.tck.expiry;

import org.jsr107.tck.testutil.ExcludeListExcluder;
import org.jsr107.tck.testutil.TestSupport;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for expiring cache entries with {@link javax.cache.expiry.ExpiryPolicy}s.
 *
 * @author Brian Oliver
 */
public class CacheExpiryTest extends TestSupport {

  /**
   * Rule used to exclude tests
   */
  @Rule
  public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());


  @After
  public void cleanup() {
    for (String cacheName : getCacheManager().getCacheNames()) {
      getCacheManager().destroyCache(cacheName);
    }
  }

  /**
   * Ensure that a cache using a {@link javax.cache.expiry.ExpiryPolicy} configured to
   * return a {@link Duration#ZERO} for newly created entries will immediately
   * expire said entries.
   */
  @Test
  public void expire_whenCreated() {
    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<Integer, Integer>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new ParameterizedExpiryPolicy<Integer, Integer>(Duration.ZERO, null, null)));

    getCacheManager().createCache(getTestCacheName(), config);
    Cache<Integer, Integer> cache = getCacheManager().getCache(getTestCacheName());

    cache.put(1, 1);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.put(1, 1);
    assertFalse(cache.remove(1));

    cache.put(1, 1);
    assertFalse(cache.remove(1, 1));

    cache.getAndPut(1, 1);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.putIfAbsent(1, 1);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    map.put(1, 1);
    cache.putAll(map);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.put(1, 1);
    assertFalse(cache.iterator().hasNext());
  }

  /**
   * Ensure that a cache using a {@link javax.cache.expiry.ExpiryPolicy} configured to
   * return a {@link Duration#ZERO} after accessing entries will immediately
   * expire said entries.
   */
  @Test
  public void expire_whenAccessed() {
    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<Integer, Integer>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new ParameterizedExpiryPolicy<Integer, Integer>(Duration.ETERNAL, Duration.ZERO, null)));

    getCacheManager().createCache(getTestCacheName(), config);
    Cache<Integer, Integer> cache = getCacheManager().getCache(getTestCacheName());

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));
    assertFalse(cache.containsKey(1));
    assertNull(cache.getAndReplace(1, 2));

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));
    assertFalse(cache.containsKey(1));
    assertNull(cache.getAndRemove(1));

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));
    assertFalse(cache.remove(1));

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));
    assertFalse(cache.remove(1, 1));

    cache.getAndPut(1, 1);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.getAndPut(1, 1);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.getAndPut(1, 1));
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.putIfAbsent(1, 1);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    map.put(1, 1);
    cache.putAll(map);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.put(1, 1);
    Iterator<Entry<Integer, Integer>> iterator = cache.iterator();
    assertTrue(iterator.hasNext());
    assertEquals((Integer) 1, iterator.next().getValue());
    assertFalse(cache.iterator().hasNext());
  }

  /**
   * Ensure that a cache using a {@link javax.cache.expiry.ExpiryPolicy} configured to
   * return a {@link Duration#ZERO} after modifying entries will immediately
   * expire said entries.
   */
  @Test
  public void expire_whenModified() {
    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<Integer, Integer>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new ParameterizedExpiryPolicy<Integer, Integer>(Duration.ETERNAL, null, Duration.ZERO)));

    getCacheManager().createCache(getTestCacheName(), config);
    Cache<Integer, Integer> cache = getCacheManager().getCache(getTestCacheName());

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.get(1));
    assertEquals((Integer) 1, cache.get(1));
    cache.put(1, 2);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.get(1));
    cache.put(1, 2);
    assertFalse(cache.remove(1));

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.get(1));
    cache.put(1, 2);
    assertFalse(cache.remove(1, 2));

    cache.getAndPut(1, 1);
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.get(1));
    cache.put(1, 2);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.getAndPut(1, 1);
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.getAndPut(1, 2));
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.get(1));
    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    map.put(1, 2);
    cache.putAll(map);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.get(1));
    cache.replace(1, 2);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.get(1));
    cache.replace(1, 1, 2);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));

    cache.put(1, 1);
    assertTrue(cache.iterator().hasNext());
    assertEquals((Integer) 1, cache.iterator().next().getValue());
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.iterator().next().getValue());
    cache.put(1, 2);
    assertFalse(cache.iterator().hasNext());
  }

  /**
   * A {@link javax.cache.expiry.ExpiryPolicy} that updates the expiry time based on
   * defined parameters.
   */
  public static class ParameterizedExpiryPolicy<K, V> implements ExpiryPolicy<K, V>, Serializable {
    /**
     * The serialVersionUID required for {@link java.io.Serializable}.
     */
    public static final long serialVersionUID = 201306141148L;

    /**
     * The {@link Duration} after which a Cache Entry will expire when created.
     */
    private Duration createdExpiryDuration;

    /**
     * The {@link Duration} after which a Cache Entry will expire when accessed.
     * (when <code>null</code> the current expiry duration will be used)
     */
    private Duration accessedExpiryDuration;

    /**
     * The {@link Duration} after which a Cache Entry will expire when modified.
     * (when <code>null</code> the current expiry duration will be used)
     */
    private Duration modifiedExpiryDuration;

    /**
     * Constructs an {@link ParameterizedExpiryPolicy}.
     *
     * @param createdExpiryDuration  the {@link Duration} to expire when an entry is created
     *                               (must not be <code>null</code>)
     * @param accessedExpiryDuration the {@link Duration} to expire when an entry is accessed
     *                               (<code>null</code> means don't change the expiry)
     * @param modifiedExpiryDuration the {@link Duration} to expire when an entry is modified
     *                               (<code>null</code> means don't change the expiry)
     */
    public ParameterizedExpiryPolicy(Duration createdExpiryDuration,
                                     Duration accessedExpiryDuration,
                                     Duration modifiedExpiryDuration) {
      if (createdExpiryDuration == null) {
        throw new NullPointerException("createdExpiryDuration can't be null");
      }

      this.createdExpiryDuration = createdExpiryDuration;
      this.accessedExpiryDuration = accessedExpiryDuration;
      this.modifiedExpiryDuration = modifiedExpiryDuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getExpiryForCreatedEntry(Entry<? extends K, ? extends V> entry) {
      return createdExpiryDuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getExpiryForAccessedEntry(Entry<? extends K, ? extends V> entry) {
      return accessedExpiryDuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getExpiryForModifiedEntry(Entry<? extends K, ? extends V> entry) {
      return modifiedExpiryDuration;
    }
  }
}

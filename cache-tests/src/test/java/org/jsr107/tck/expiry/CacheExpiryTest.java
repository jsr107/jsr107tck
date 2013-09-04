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

import org.jsr107.tck.integration.CacheLoaderClient;
import org.jsr107.tck.integration.CacheLoaderServer;
import org.jsr107.tck.integration.RecordingCacheLoader;
import org.jsr107.tck.processor.AssertNotPresentEntryProcessor;
import org.jsr107.tck.processor.CombineEntryProcessor;
import org.jsr107.tck.processor.GetEntryProcessor;
import org.jsr107.tck.processor.SetEntryProcessor;
import org.jsr107.tck.testutil.ExcludeListExcluder;
import org.jsr107.tck.testutil.TestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.Cache.Entry;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.expiry.ModifiedExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;
import javax.cache.integration.CompletionListenerFuture;
import javax.cache.processor.EntryProcessor;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for expiring cache entries with {@link javax.cache.expiry.ExpiryPolicy}s.
 *
 * @author Brian Oliver
 * @author Joe Fialli
 */
public class CacheExpiryTest extends TestSupport {

  /**
   * Rule used to exclude tests
   */
  @Rule
  public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

  private ExpiryPolicyServer<Integer> expiryPolicyServer;
  private ExpiryPolicyClient<Integer> expiryPolicyClient;

  @Before
  public void setupBeforeEachTest() throws IOException {
    //establish and open a CacheLoaderServer to handle cache
    //cache loading requests from a CacheLoaderClient
    expiryPolicyServer = new ExpiryPolicyServer<Integer>(10005);
    expiryPolicyServer.open();

    //establish a ExpiryPolicyClient that a Cache can use for computing expiry policy
    //(via the ExpiryPolicyServer)
    expiryPolicyClient =
      new ExpiryPolicyClient<>(expiryPolicyServer.getInetAddress(), expiryPolicyServer.getPort());

  }


  @After
  public void cleanupAfterEachTest() {
    for (String cacheName : getCacheManager().getCacheNames()) {
      getCacheManager().destroyCache(cacheName);
    }
    expiryPolicyServer.close();
    expiryPolicyServer = null;
  }

  /**
   * Assert "The minimum allowed TimeUnit is TimeUnit.MILLISECONDS.
   */
  @Test(expected = IllegalArgumentException.class)
  public void microsecondsInvalidDuration() {
    Duration invalidDuration = new Duration(TimeUnit.MICROSECONDS, 0);
    assertTrue("expected IllegalArgumentException for TimeUnit below minimum of MILLISECONDS", invalidDuration == null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nanosecondsInvalidDuration() {
    Duration invalidDuration = new Duration(TimeUnit.NANOSECONDS, 0);
    assertTrue("expected IllegalArgumentException for TimeUnit below minimum of MILLISECONDS", invalidDuration == null);
  }

  /**
   * Ensure that a cache using a {@link javax.cache.expiry.ExpiryPolicy} configured to
   * return a {@link Duration#ZERO} for newly created entries will immediately
   * expire said entries.
   */
  private void expire_whenCreated(Factory<? extends ExpiryPolicy<Integer>> expiryPolicyFactory) {
    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<Integer, Integer>();
    config.setExpiryPolicyFactory(expiryPolicyFactory);

    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

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

  @Test
  public void expire_whenCreated_ParameterizedExpiryPolicy() {
    expire_whenCreated(FactoryBuilder.factoryOf(new ParameterizedExpiryPolicy<Integer>(Duration.ZERO, null, null)));
  }

  @Test
  public void expire_whenCreated_CreatedExpiryPolicy() {
    expire_whenCreated(FactoryBuilder.factoryOf(new CreatedExpiryPolicy<Integer>(Duration.ZERO)));
  }

  @Test
  public void expire_whenCreated_AccessedExpiryPolicy() {
    // since AccessedExpiryPolicy uses same duration for created and accessed, this policy will work same as
    // CreatedExpiryPolicy for this test.
    expire_whenCreated(FactoryBuilder.factoryOf(new AccessedExpiryPolicy<Integer>(Duration.ZERO)));
  }

  @Test
  public void expire_whenCreated_TouchedExpiryPolicy() {
    // since TouchedExpiryPolicy uses same duration for created and accessed, this policy will work same as
    // CreatedExpiryPolicy for this test.
    expire_whenCreated(FactoryBuilder.factoryOf(new TouchedExpiryPolicy<Integer>(Duration.ZERO)));
  }

  @Test
  public void expire_whenCreated_ModifiedExpiryPolicy() {
    // since TouchedExpiryPolicy uses same duration for created and accessed, this policy will work same as
    // CreatedExpiryPolicy for this test.
    expire_whenCreated(FactoryBuilder.factoryOf(new ModifiedExpiryPolicy<Integer>(Duration.ZERO)));
  }

  /**
   * Ensure that a cache using a {@link javax.cache.expiry.ExpiryPolicy} configured to
   * return a {@link Duration#ZERO} after accessing entries will immediately
   * expire said entries.
   */
  @Test
  public void expire_whenAccessed() {
    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<Integer, Integer>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new ParameterizedExpiryPolicy<Integer>(Duration.ETERNAL, Duration.ZERO, null)));

    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

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
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new ParameterizedExpiryPolicy<Integer>(Duration.ETERNAL, null, Duration.ZERO)));

    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

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
   * Assert following from all ExpiryPolicy methods javadoc:
   * Should an exception occur while determining the Duration, an implementation specific default Duration will be used.
   * <p/>
   * This test assumes that implementation specific duration would never be Duration.ZERO.
   */
  @Test
  public void testCreateExpiryPolicyThrowingRuntimeException() {
    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<Integer, Integer>();

    // allow creation, throw exception on access or modify
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new FaultyExpiryPolicy<Integer>(true, false, false)));

    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    // next line should cause an exception in call to getExpiryForCreatedEntry
    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    assertEquals((Integer) 1, cache.get(1));
  }

  /**
   * Assert following from all ExpiryPolicy methods javadoc:
   * Should an exception occur while determining the Duration, an implementation specific default Duration will be used.
   * <p/>
   * This test assumes that implementation specific duration would never be Duration.ZERO.
   */
  @Test
  public void testAccessExpiryPolicyThrowingRuntimeException() {
    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<Integer, Integer>();

    // allow creation, throw exception on access or modify
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new FaultyExpiryPolicy<Integer>(false, false, true)));

    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));
    // next line should cause an exception in call to getExpiryForAccessedEntry
    assertEquals((Integer) 1, cache.get(1));

    // modify is returing Duration.ZERO.  So make certain expired.
    cache.put(1, 2);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));
  }

  // Next set of tests verify table from jsr 107 spec on how each of the cache methods interact with a
  // configured ExpiryPolicy method getting called.
  // There is one test per row in table.

  @Test
  public void containsKeyShouldNotCallExpiryPolicyMethods() {

    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.containsKey(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));

    cache.containsKey(1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));
  }

  @Test
  public void getShouldCallGetExpiryForAccessedEntry() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.containsKey(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    // when getting a non-existent entry, getExpiryForAccessedEntry is not called.
    cache.get(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));

    // when getting an existing entry, getExpiryForAccessedEntry is called.
    cache.get(1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForAccessedEntry", recordedExpiryPolicyCallMap.get(1));
  }

  @Test
  public void getAllShouldCallGetExpiryForAccessedEntry() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);
    Set<Integer> keys = new HashSet<>();
    keys.add(1);
    keys.add(2);

    // when getting a non-existent entry, getExpiryForAccessedEntry is not called.
    cache.getAll(keys);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));
    assertFalse(recordedExpiryPolicyCallMap.containsKey(2));

    cache.put(1, 1);
    cache.put(2, 2);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));
    assertTrue(recordedExpiryPolicyCallMap.containsKey(2));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(2));

    // when getting an existing entry, getExpiryForAccessedEntry is called.
    cache.get(1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForAccessedEntry", recordedExpiryPolicyCallMap.get(1));
    cache.get(1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(2));
    assertEquals("called getExpiryForAccessedEntry", recordedExpiryPolicyCallMap.get(1));
  }

  @Test
  public void getAndPutShouldCallEitherCreatedOrModifiedExpiryPolicy() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.containsKey(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.getAndPut(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));

    cache.getAndPut(1, 2);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForModifiedEntry", recordedExpiryPolicyCallMap.get(1));
  }

  @Test
  public void getAndRemoveShouldNotCallExpiryPolicyMethods() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    // verify case when entry is non-existent
    cache.containsKey(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));
    cache.getAndRemove(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));


    // verify case when entry exist
    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));
    recordedExpiryPolicyCallMap.remove(1);


    int value = cache.getAndRemove(1);
    assertEquals(1, value);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));
  }

  @Test
  public void getAndReplaceShouldCallGetExpiryForModifiedEntry() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.containsKey(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.getAndReplace(1, 1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    recordedExpiryPolicyCallMap.remove(1);

    int oldValue = cache.getAndReplace(1, 2);
    assertEquals(1, oldValue);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForModifiedEntry", recordedExpiryPolicyCallMap.get(1));
  }

  // Skip negative to verify getCacheManager, getConfiguration or getName not calling getExpiryFor*.
  // They are not methods that access/mutate entries in cache.

  @Test
  public void iteratorNextShouldCallGetExpiryForAccessedEntry() {
    final String EXPIRY_CALLS_RECORDING_CACHE = "recordedExpiryPolicyCallMap";
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);
    Set<Integer> keys = new HashSet<>();
    keys.add(1);
    keys.add(2);

    cache.put(1, 1);
    cache.put(2, 2);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));
    assertTrue(recordedExpiryPolicyCallMap.containsKey(2));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(2));

    // when getting an existing entry, getExpiryForAccessedEntry is called.
    Iterator<Entry<Integer, Integer>> iter = cache.iterator();
    while (iter.hasNext()) {
      Entry<Integer, Integer> entry = iter.next();
      assertTrue(recordedExpiryPolicyCallMap.containsKey(entry.getKey()));
      assertEquals("called getExpiryForAccessedEntry", recordedExpiryPolicyCallMap.get(entry.getKey()));
    }
  }

  @Test
  public void loadAllWithReadThroughEnabledShouldCallGetExpiryForCreatedEntry() throws IOException, ExecutionException, InterruptedException {
    //establish and open a CacheLoaderServer to handle cache
    //cache loading requests from a CacheLoaderClient

    // this cacheLoader just returns the key as the value.
    RecordingCacheLoader<Integer> recordingCacheLoader = new RecordingCacheLoader<>();
    try (CacheLoaderServer<Integer, Integer> cacheLoaderServer = new CacheLoaderServer<>(10000, recordingCacheLoader)) {
      cacheLoaderServer.open();

      //establish a CacheLoaderClient that a Cache can use for loading entries
      //(via the CacheLoaderServer)
      CacheLoaderClient<Integer, Integer> cacheLoader =
        new CacheLoaderClient<>(cacheLoaderServer.getInetAddress(), cacheLoaderServer.getPort());

      RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
      expiryPolicyServer.setExpiryPolicy(expiryPolicy);
      Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

      MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
      config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
      config.setCacheLoaderFactory(FactoryBuilder.factoryOf(cacheLoader));
      config.setReadThrough(true);

      Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);


      final Integer INITIAL_KEY = 123;
      final Integer MAX_KEY_VALUE = INITIAL_KEY + 4;

      // set half of the keys so half of invokeAll will be modify and rest will be create.
      Set<Integer> keys = new HashSet<>();
      for (int key = INITIAL_KEY; key <= MAX_KEY_VALUE; key++) {
        keys.add(key);
      }

      // verify read-through of getValue of non-existent entries
      CompletionListenerFuture future = new CompletionListenerFuture();
      cache.loadAll(keys, false, future);

      //wait for the load to complete
      future.get();

      assertThat(future.isDone(), is(true));
      for (Integer key : keys) {
        assertThat(recordingCacheLoader.hasLoaded(key), is(true));
        assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(key));
        assertThat(cache.get(key), is(equalTo(key)));
      }
      assertThat(recordingCacheLoader.getLoadCount(), is(keys.size()));


      // verify read-through of getValue for existing entries AND replaceExistingValues is true.
      final boolean REPLACE_EXISTING_VALUES=true;
      future = new CompletionListenerFuture();
      cache.loadAll(keys, REPLACE_EXISTING_VALUES, future);

      //wait for the load to complete
      future.get();

      assertThat(future.isDone(), is(true));
      for (Integer key : keys) {
        assertThat(recordingCacheLoader.hasLoaded(key), is(true));
        assertEquals("called getExpiryForModifiedEntry", recordedExpiryPolicyCallMap.get(key));
        assertThat(cache.get(key), is(equalTo(key)));
      }
      assertThat(recordingCacheLoader.getLoadCount(), is(keys.size() * 2));
    }
  }


  @Test
  public void putShouldCallGetExpiry() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.containsKey(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForModifiedEntry", recordedExpiryPolicyCallMap.get(1));
  }

  @Test
  public void putAllShouldCallGetExpiry() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    Map<Integer, Integer> map = new HashMap<>();
    map.put(1, 1);
    map.put(2, 2);

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));

    cache.putAll(map);

    // verify modify
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForModifiedEntry", recordedExpiryPolicyCallMap.get(1));

    // verify created
    assertTrue(recordedExpiryPolicyCallMap.containsKey(2));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(2));
  }

  @Test
  public void putIfAbsentShouldCallGetExpiry() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.containsKey(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    boolean result = cache.putIfAbsent(1, 1);
    assertTrue(result);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));
    recordedExpiryPolicyCallMap.remove(1);

    result = cache.putIfAbsent(1, 2);
    assertFalse(result);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));
  }

  @Test
  public void removeEntryShouldNotCallExpiryPolicyMethods() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);


    boolean result = cache.remove(1);
    assertFalse(result);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));
    recordedExpiryPolicyCallMap.remove(1);

    result = cache.remove(1);
    assertTrue(result);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));
  }

  @Test
  public void removeSpecifiedEntryShouldNotCallExpiryPolicyMethods() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);


    boolean result = cache.remove(1, 1);
    assertFalse(result);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(1));
    recordedExpiryPolicyCallMap.remove(1);

    result = cache.remove(1, 1);
    assertTrue(result);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));
  }

  // skipping test for removeAll and removeAll specified keys for now. negative test of remove seems enough.

  @Test
  public void invokeSetValueShouldCallGetExpiry() {

    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);


    final Integer key = 123;
    final Integer setValue = 456;
    final Integer modifySetValue = 789;

    // verify create
    EntryProcessor processors[] =
      new EntryProcessor[]{
        new AssertNotPresentEntryProcessor(null),
        new SetEntryProcessor<Integer, Integer, Integer>(setValue),
        new GetEntryProcessor<Integer, Integer, Integer>()
      };
    Object[] result = (Object[]) cache.invoke(key, new CombineEntryProcessor(processors));
    assertEquals(result[1], setValue);
    assertEquals(result[2], setValue);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(key));

    // expiry called should be for create, not for the get or modify.
    // Operations get combined in entry processor and only net result should be expiryPolicy method called.
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(key));

    // verify modify
    Integer resultValue = cache.invoke(key, new SetEntryProcessor<Integer, Integer, Integer>(modifySetValue));
    assertEquals(modifySetValue, resultValue);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(key));
    assertEquals("called getExpiryForModifiedEntry", recordedExpiryPolicyCallMap.get(key));
  }


  @Test
  public void invokeMultiSetValueShouldCallGetExpiry() {

    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);


    final Integer key = 123;
    final Integer setValue = 456;
    final Integer modifySetValue = 789;

    // verify create
    EntryProcessor processors[] =
      new EntryProcessor[]{
        new AssertNotPresentEntryProcessor(null),
        new SetEntryProcessor<Integer, Integer, Integer>(111),
        new SetEntryProcessor<Integer, Integer, Integer>(setValue),
        new GetEntryProcessor<Integer, Integer, Integer>()
      };
    Object[] result = (Object[]) cache.invoke(key, new CombineEntryProcessor(processors));
    assertEquals(result[1], 111);
    assertEquals(result[2], setValue);
    assertEquals(result[3], setValue);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(key));

    // expiry called should be for create, not for the get or modify.
    // Operations get combined in entry processor and only net result should be expiryPolicy method called.
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(key));
  }

  @Test
  public void invokeGetValueShouldCallGetExpiry() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);


    final Integer key = 123;
    final Integer setValue = 456;

    // verify non-access to non-existent entry does not call getExpiryForAccessedEntry. no read-through scenario.
    Integer resultValue = cache.invoke(key, new GetEntryProcessor<Integer, Integer, Integer>());
    assertEquals(null, resultValue);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(key));

    // verify access to existing entry.
    resultValue = cache.invoke(key, new SetEntryProcessor<Integer, Integer, Integer>(setValue));
    assertEquals(resultValue, setValue);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(key));
    assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(key));
    recordedExpiryPolicyCallMap.clear();
    resultValue = cache.invoke(key, new GetEntryProcessor<Integer, Integer, Integer>());
    assertEquals(setValue, resultValue);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(key));
    assertEquals("called getExpiryForAccessedEntry", recordedExpiryPolicyCallMap.get(key));

  }

  @Test
  public void invokeGetValueWithReadThroughForNonExistentEntryShouldCallGetExpiryForCreatedEntry() throws IOException {

    //establish and open a CacheLoaderServer to handle cache
    //cache loading requests from a CacheLoaderClient

    // this cacheLoader just returns the key as the value.
    RecordingCacheLoader<Integer> recordingCacheLoader = new RecordingCacheLoader<>();
    try (CacheLoaderServer<Integer, Integer> cacheLoaderServer = new CacheLoaderServer<>(10000, recordingCacheLoader)) {
      cacheLoaderServer.open();

      //establish a CacheLoaderClient that a Cache can use for loading entries
      //(via the CacheLoaderServer)
      CacheLoaderClient<Integer, Integer> cacheLoader =
        new CacheLoaderClient<>(cacheLoaderServer.getInetAddress(), cacheLoaderServer.getPort());

      RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
      expiryPolicyServer.setExpiryPolicy(expiryPolicy);
      Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

      MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
      config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
      config.setCacheLoaderFactory(FactoryBuilder.factoryOf(cacheLoader));
      config.setReadThrough(true);
      Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);


      final Integer key = 123;
      final Integer recordingCacheLoaderValue = key;

      // verify create when read through is enabled and entry was non-existent in cache.
      Integer resultValue = cache.invoke(key, new GetEntryProcessor<Integer, Integer, Integer>());
      assertEquals(recordingCacheLoaderValue, resultValue);
      assertTrue(recordingCacheLoader.hasLoaded(key));
      assertTrue(recordedExpiryPolicyCallMap.containsKey(key));
      assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(key));
    }
  }

  @Test
  public void invokeAllSetValueShouldCallGetExpiry() {

    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);


    final Integer INITIAL_KEY = 123;
    final Integer MAX_KEY_VALUE = INITIAL_KEY + 4;
    final Integer setValue = 456;
    final Integer modifySetValue = 789;

    // set half of the keys so half of invokeAll will be modify and rest will be create.
    Set<Integer> keys = new HashSet<>();
    for (int key = INITIAL_KEY; key <= MAX_KEY_VALUE; key++) {
      keys.add(key);
      if (key <= MAX_KEY_VALUE - 2) {
        cache.put(key, setValue);
      }
    }

    // verify modify or create
    Map<Integer, Integer> resultMap = cache.invokeAll(keys, new SetEntryProcessor<Integer, Integer, Integer>(setValue));

    for (Map.Entry<Integer, Integer> entry : resultMap.entrySet()) {
      assertTrue(recordedExpiryPolicyCallMap.containsKey(entry.getKey()));
      if (entry.getKey() <= MAX_KEY_VALUE - 2) {

        // verify modify
        assertEquals("called getExpiryForModifiedEntry", recordedExpiryPolicyCallMap.get(entry.getKey()));
      } else {

        // verify create
        assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(entry.getKey()));
      }
    }
    recordedExpiryPolicyCallMap.clear();

    // verify accessed
    resultMap = cache.invokeAll(keys, new GetEntryProcessor<Integer, Integer, Integer>());
    for (Map.Entry<Integer, Integer> entry : resultMap.entrySet()) {
      assertTrue(recordedExpiryPolicyCallMap.containsKey(entry.getKey()));
      assertEquals("called getExpiryForAccessedEntry", recordedExpiryPolicyCallMap.get(entry.getKey()));
    }
  }

  @Test
  public void invokeAllReadThroughEnabledGetOnNonExistentEntry() throws IOException {
    //establish and open a CacheLoaderServer to handle cache
    //cache loading requests from a CacheLoaderClient

    // this cacheLoader just returns the key as the value.
    RecordingCacheLoader<Integer> recordingCacheLoader = new RecordingCacheLoader<>();
    try (CacheLoaderServer<Integer, Integer> cacheLoaderServer = new CacheLoaderServer<>(10000, recordingCacheLoader)) {
      cacheLoaderServer.open();

      //establish a CacheLoaderClient that a Cache can use for loading entries
      //(via the CacheLoaderServer)
      CacheLoaderClient<Integer, Integer> cacheLoader =
        new CacheLoaderClient<>(cacheLoaderServer.getInetAddress(), cacheLoaderServer.getPort());

      RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
      expiryPolicyServer.setExpiryPolicy(expiryPolicy);
      Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

      MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
      config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
      config.setCacheLoaderFactory(FactoryBuilder.factoryOf(cacheLoader));
      config.setReadThrough(true);

      Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);


      final Integer INITIAL_KEY = 123;
      final Integer MAX_KEY_VALUE = INITIAL_KEY + 4;


      // set half of the keys so half of invokeAll will be modify and rest will be create.
      Set<Integer> keys = new HashSet<>();
      for (int key = INITIAL_KEY; key <= MAX_KEY_VALUE; key++) {
        keys.add(key);
      }

      // verify read-through of getValue of non-existent entries
      Map<Integer, Integer> resultMap = cache.invokeAll(keys, new GetEntryProcessor<Integer, Integer, Integer>());

      for (Map.Entry<Integer, Integer> entry : resultMap.entrySet()) {
        assertTrue(recordedExpiryPolicyCallMap.containsKey(entry.getKey()));

        // verify read-through caused a create
        assertEquals("called getExpiryForCreatedEntry", recordedExpiryPolicyCallMap.get(entry.getKey()));
        assertEquals(entry.getKey(), cache.get(entry.getKey()));
      }
    }
  }

  @Test
  public void replaceShouldCallGetExpiryForModifiedEntry() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.containsKey(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    // verify case that replace does not occur so no expiry policy called
    boolean result = cache.replace(1, 1);
    assertFalse(result);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    recordedExpiryPolicyCallMap.remove(1);

    result = cache.replace(1, 2);
    assertTrue(result);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForModifiedEntry", recordedExpiryPolicyCallMap.get(1));
  }

  // optimized out test for unwrap method since it does not access/mutate an entry.

  @Test
  public void replaceSpecificShouldCallGetExpiry() {
    RecordingExpiryPolicy<Integer> expiryPolicy = new RecordingExpiryPolicy<>();
    expiryPolicyServer.setExpiryPolicy(expiryPolicy);
    Map<Integer, String> recordedExpiryPolicyCallMap = expiryPolicy.getCallMap();

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(expiryPolicyClient));
    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.containsKey(1);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    boolean result = cache.replace(1, 1, 2);
    assertFalse(result);
    assertFalse(recordedExpiryPolicyCallMap.containsKey(1));

    cache.put(1, 1);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    recordedExpiryPolicyCallMap.remove(1);

    // verify case when entry exist for key, but oldValue is incorrect. So replacement does not happen.
    // this counts as an access of entry referred to by key.
    result = cache.replace(1, 2, 5);
    assertFalse(result);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForAccessedEntry", recordedExpiryPolicyCallMap.get(1));

    // verify the modify case when replace does succeed.
    result = cache.replace(1, 1, 2);
    assertTrue(result);
    assertTrue(recordedExpiryPolicyCallMap.containsKey(1));
    assertEquals("called getExpiryForModifiedEntry", recordedExpiryPolicyCallMap.get(1));
  }


  @Test
  public void testModifiedExpiryPolicyThrowingRuntimeException() {
    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<Integer, Integer>();

    // allow creation, throw exception on access or modify
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new FaultyExpiryPolicy<Integer>(false, true, false)));

    Cache<Integer, Integer> cache = getCacheManager().createCache(getTestCacheName(), config);

    cache.put(1, 1);
    assertTrue(cache.containsKey(1));


    // modify should throw exception calling FaultyExpiryPolicy.getExpiryForModifiedEntry,
    // jcache implementation should handle and use an implementation default duration.
    cache.put(1, 2);
    assertTrue(cache.containsKey(1));
    assertNotNull(cache.get(1));

    cache.get(1);
    assertFalse(cache.containsKey(1));
    assertNull(cache.get(1));
  }

  public static class RecordingExpiryPolicy<K> implements ExpiryPolicy<K>, Serializable {
    private transient Map<K, String> callMap = new HashMap<>();

    Map<K, String> getCallMap() {
      return callMap;
    }

    @Override
    public Duration getExpiryForCreatedEntry(K key) {
      callMap.put(key, "called getExpiryForCreatedEntry");
      return Duration.ETERNAL;
    }

    @Override
    public Duration getExpiryForAccessedEntry(K key) {
      callMap.put(key, "called getExpiryForAccessedEntry");
      return null;
    }

    @Override
    public Duration getExpiryForModifiedEntry(K key) {
      callMap.put(key, "called getExpiryForModifiedEntry");
      return null;
    }
  }

  public static class FaultyExpiryPolicy<K> implements ExpiryPolicy<K>, Serializable {
    private boolean failOnCreate;
    private boolean failOnModify;
    private boolean failOnAccess;


    public FaultyExpiryPolicy(boolean failOnCreate, boolean failOnModify, boolean failOnAccess) {
      this.failOnCreate = failOnCreate;
      this.failOnModify = failOnModify;
      this.failOnAccess = failOnAccess;
    }

    @Override
    public Duration getExpiryForCreatedEntry(K key) {
      if (failOnCreate) {
        throw new UnsupportedOperationException("not implemented");
      }
      return Duration.ETERNAL;
    }

    @Override
    public Duration getExpiryForAccessedEntry(K key) {
      if (failOnAccess) {
        throw new UnsupportedOperationException("not implemented");
      }
      return Duration.ZERO;
    }

    @Override
    public Duration getExpiryForModifiedEntry(K key) {
      if (failOnModify) {
        throw new UnsupportedOperationException("not implemented");
      }
      return Duration.ZERO;
    }
  }


  /**
   * A {@link javax.cache.expiry.ExpiryPolicy} that updates the expiry time based on
   * defined parameters.
   */
  public static class ParameterizedExpiryPolicy<K> implements ExpiryPolicy<K>, Serializable {
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
     *
     * @param key
     */
    @Override
    public Duration getExpiryForCreatedEntry(K key) {
      return createdExpiryDuration;
    }

    /**
     * {@inheritDoc}
     *
     * @param key
     */
    @Override
    public Duration getExpiryForAccessedEntry(K key) {
      return accessedExpiryDuration;
    }

    /**
     * {@inheritDoc}
     *
     * @param key
     */
    @Override
    public Duration getExpiryForModifiedEntry(K key) {
      return modifiedExpiryDuration;
    }
  }
}

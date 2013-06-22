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
package org.jsr107.tck.integration;

import org.jsr107.tck.testutil.TestSupport;
import org.jsr107.tck.testutil.ExcludeListExcluder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.integration.CacheWriter;
import javax.cache.configuration.MutableConfiguration;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link javax.cache.integration.CacheWriter}s.
 *
 * @author Brian Oliver
 */
public class CacheWriterTest extends TestSupport {

  /**
   * Rule used to exclude tests
   */
  @Rule
  public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

  /**
   * The CacheWriter used for the tests.
   */
  private RecordingCacheWriter<Integer, String> cacheWriter;

  /**
   * The test Cache that will be configured to use the CacheWriter.
   */
  private Cache<Integer, String> cache;

  @Before
  public void setup() {
    cacheWriter = new RecordingCacheWriter<Integer, String>();

    MutableConfiguration<Integer, String> config = new MutableConfiguration<Integer, String>();
    config.setCacheWriterFactory(FactoryBuilder.factoryOf(cacheWriter));
    config.setWriteThrough(true);

    cache = getCacheManager().configureCache(getTestCacheName(), config);
  }

  @After
  public void cleanup() {
    for (String cacheName : getCacheManager().getCacheNames()) {
      getCacheManager().destroyCache(cacheName);
    }
  }

  @Test
  public void put_SingleEntry() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.put(1, "Gudday World");

    assertEquals(1, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());
    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Gudday World", cacheWriter.get(1));
  }

  @Test
  public void put_SingleEntryMultipleTimes() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.put(1, "Gudday World");
    cache.put(1, "Bonjour World");
    cache.put(1, "Hello World");

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());
    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Hello World", cacheWriter.get(1));
  }

  @Test
  public void put_DifferentEntries() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.put(1, "Gudday World");
    cache.put(2, "Bonjour World");
    cache.put(3, "Hello World");

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Gudday World", cacheWriter.get(1));

    assertTrue(cacheWriter.containsKey(2));
    assertEquals("Bonjour World", cacheWriter.get(2));

    assertTrue(cacheWriter.containsKey(3));
    assertEquals("Hello World", cacheWriter.get(3));
  }

  @Test
  public void getAndPut_SingleEntryMultipleTimes() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.getAndPut(1, "Gudday World");
    cache.getAndPut(1, "Bonjour World");
    cache.getAndPut(1, "Hello World");

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());
    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Hello World", cacheWriter.get(1));
  }

  @Test
  public void getAndPut_DifferentEntries() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.getAndPut(1, "Gudday World");
    cache.getAndPut(2, "Bonjour World");
    cache.getAndPut(3, "Hello World");

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Gudday World", cacheWriter.get(1));

    assertTrue(cacheWriter.containsKey(2));
    assertEquals("Bonjour World", cacheWriter.get(2));

    assertTrue(cacheWriter.containsKey(3));
    assertEquals("Hello World", cacheWriter.get(3));
  }

  @Test
  public void putIfAbsent_SingleEntryMultipleTimes() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.putIfAbsent(1, "Gudday World");
    cache.putIfAbsent(1, "Bonjour World");
    cache.putIfAbsent(1, "Hello World");

    assertEquals(1, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());
    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Gudday World", cacheWriter.get(1));
  }

  @Test
  public void replaceMatching_SingleEntryMultipleTimes() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.putIfAbsent(1, "Gudday World");
    cache.replace(1, "Gudday World", "Bonjour World");
    cache.replace(1, "Gudday World", "Hello World");
    cache.replace(1, "Bonjour World", "Hello World");

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());
    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Hello World", cacheWriter.get(1));
  }

  @Test
  public void replaceExisting_SingleEntryMultipleTimes() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.replace(1, "Gudday World");
    cache.putIfAbsent(1, "Gudday World");
    cache.replace(1, "Bonjour World");
    cache.replace(1, "Hello World");

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());
    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Hello World", cacheWriter.get(1));
  }

  @Test
  public void getAndReplace_SingleEntryMultipleTimes() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.getAndReplace(1, "Gudday World");
    cache.putIfAbsent(1, "Gudday World");
    cache.getAndReplace(1, "Bonjour World");
    cache.getAndReplace(1, "Hello World");

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());
    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Hello World", cacheWriter.get(1));
  }

  @Test
  public void invoke_CreateEntry() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.invokeEntryProcessor(1, new Cache.EntryProcessor<Integer, String, Void>() {
      @Override
      public Void process(Cache.MutableEntry<Integer, String> entry, Object... arguments) {
        entry.setValue("Gudday World");
        return null;
      }
    });

    assertEquals(1, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());
    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Gudday World", cacheWriter.get(1));
  }

  @Test
  public void invoke_UpdateEntry() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.put(1, "Gudday World");
    cache.invokeEntryProcessor(1, new Cache.EntryProcessor<Integer, String, Void>() {
      @Override
      public Void process(Cache.MutableEntry<Integer, String> entry, Object... arguments) {
        entry.setValue("Hello World");
        return null;
      }
    });

    assertEquals(2, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());
    assertTrue(cacheWriter.containsKey(1));
    assertEquals("Hello World", cacheWriter.get(1));
  }

  @Test
  public void invoke_RemoveEntry() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.put(1, "Gudday World");
    cache.invokeEntryProcessor(1, new Cache.EntryProcessor<Integer, String, Void>() {
      @Override
      public Void process(Cache.MutableEntry<Integer, String> entry, Object... arguments) {
        entry.remove();
        return null;
      }
    });

    assertEquals(1, cacheWriter.getWriteCount());
    assertEquals(1, cacheWriter.getDeleteCount());
    assertFalse(cacheWriter.containsKey(1));
  }

  @Test
  public void remove_SingleEntry() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.put(1, "Gudday World");
    cache.remove(1);

    assertEquals(1, cacheWriter.getWriteCount());
    assertEquals(1, cacheWriter.getDeleteCount());
    assertFalse(cacheWriter.containsKey(1));
  }

  @Test
  public void remove_SingleEntryMultipleTimes() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.put(1, "Gudday World");
    cache.remove(1);
    cache.remove(1);
    cache.remove(1);

    assertEquals(1, cacheWriter.getWriteCount());
    assertEquals(3, cacheWriter.getDeleteCount());
    assertFalse(cacheWriter.containsKey(1));
  }

  @Test
  public void remove_SpecificEntry() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.put(1, "Gudday World");
    cache.remove(1, "Hello World");
    cache.remove(1, "Gudday World");
    cache.remove(1, "Gudday World");
    cache.remove(1);

    assertEquals(1, cacheWriter.getWriteCount());
    assertEquals(2, cacheWriter.getDeleteCount());
    assertFalse(cacheWriter.containsKey(1));
  }

  @Test
  public void getAndRemove_SingleEntry() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.getAndRemove(1);
    cache.put(1, "Gudday World");
    cache.getAndRemove(1);

    assertEquals(1, cacheWriter.getWriteCount());
    assertEquals(2, cacheWriter.getDeleteCount());
    assertFalse(cacheWriter.containsKey(1));
  }

  @Test
  public void iterator_remove() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    cache.getAndPut(1, "Gudday World");
    cache.getAndPut(2, "Bonjour World");
    cache.getAndPut(3, "Hello World");

    Iterator<Cache.Entry<Integer, String>> iterator = cache.iterator();

    iterator.next();
    iterator.remove();
    iterator.next();
    iterator.next();
    iterator.remove();

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(2, cacheWriter.getDeleteCount());
  }

  @Test
  public void putAll() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(1, "Gudday World");
    map.put(2, "Bonjour World");
    map.put(3, "Hello World");

    cache.putAll(map);

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    for (Integer key : map.keySet()) {
      assertTrue(cacheWriter.containsKey(key));
      assertEquals(map.get(key), cacheWriter.get(key));
      assertTrue(cache.containsKey(key));
      assertEquals(map.get(key), cache.get(key));
    }

    map.put(4, "Hola World");

    cache.putAll(map);

    assertEquals(7, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    for (Integer key : map.keySet()) {
      assertTrue(cacheWriter.containsKey(key));
      assertEquals(map.get(key), cacheWriter.get(key));
      assertTrue(cache.containsKey(key));
      assertEquals(map.get(key), cache.get(key));
    }
  }

  @Test
  public void removeAll() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(1, "Gudday World");
    map.put(2, "Bonjour World");
    map.put(3, "Hello World");

    cache.putAll(map);

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    for (Integer key : map.keySet()) {
      assertTrue(cacheWriter.containsKey(key));
      assertEquals(map.get(key), cacheWriter.get(key));
      assertTrue(cache.containsKey(key));
      assertEquals(map.get(key), cache.get(key));
    }

    cache.removeAll();

    assertEquals(3, cacheWriter.getWriteCount());
    assertEquals(3, cacheWriter.getDeleteCount());

    for (Integer key : map.keySet()) {
      assertFalse(cacheWriter.containsKey(key));
      assertFalse(cache.containsKey(key));
    }

    map.put(4, "Hola World");

    cache.putAll(map);

    assertEquals(7, cacheWriter.getWriteCount());
    assertEquals(3, cacheWriter.getDeleteCount());

    for (Integer key : map.keySet()) {
      assertTrue(cacheWriter.containsKey(key));
      assertEquals(map.get(key), cacheWriter.get(key));
      assertTrue(cache.containsKey(key));
      assertEquals(map.get(key), cache.get(key));
    }
  }

  @Test
  public void removeAllSpecific() {
    assertEquals(0, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    HashMap<Integer, String> map = new HashMap<Integer, String>();
    map.put(1, "Gudday World");
    map.put(2, "Bonjour World");
    map.put(3, "Hello World");
    map.put(4, "Hola World");

    cache.putAll(map);

    assertEquals(4, cacheWriter.getWriteCount());
    assertEquals(0, cacheWriter.getDeleteCount());

    for (Integer key : map.keySet()) {
      assertTrue(cacheWriter.containsKey(key));
      assertEquals(map.get(key), cacheWriter.get(key));
      assertTrue(cache.containsKey(key));
      assertEquals(map.get(key), cache.get(key));
    }

    HashSet<Integer> set = new HashSet<Integer>();
    set.add(1);
    set.add(4);

    cache.removeAll(set);

    assertEquals(4, cacheWriter.getWriteCount());
    assertEquals(2, cacheWriter.getDeleteCount());

    for (Integer key : set) {
      assertFalse(cacheWriter.containsKey(key));
      assertFalse(cache.containsKey(key));
    }

    cache.put(4, "Howdy World");

    assertEquals(5, cacheWriter.getWriteCount());
    assertEquals(2, cacheWriter.getDeleteCount());

    set.clear();
    set.add(2);

    cache.removeAll(set);

    assertTrue(cacheWriter.containsKey(3));
    assertTrue(cache.containsKey(3));
    assertTrue(cacheWriter.containsKey(4));
    assertTrue(cache.containsKey(4));
  }

  /**
   * A CacheWriter implementation that records the entries written to it so
   * that they may be later asserted.
   *
   * @param <K> the type of the keys
   * @param <V> the type of the values
   */
  public static class RecordingCacheWriter<K, V> implements CacheWriter<K, V>,
      Serializable {

    /**
     * A map of keys to values that have been written.
     */
    private ConcurrentHashMap<K, V> map;

    /**
     * The number of writes that have so far occurred.
     */
    private AtomicLong writeCount;

    /**
     * The number of deletes that have so far occurred.
     */
    private AtomicLong deleteCount;

    /**
     * Constructs a RecordingCacheWriter.
     */
    public RecordingCacheWriter() {
      this.map = new ConcurrentHashMap<K, V>();
      this.writeCount = new AtomicLong();
      this.deleteCount = new AtomicLong();
    }

    @Override
    public void write(Cache.Entry<? extends K, ? extends V> entry) {
      V previous = map.put(entry.getKey(), entry.getValue());
      writeCount.incrementAndGet();
    }

    @Override
    public void writeAll(Collection<Cache.Entry<? extends K, ? extends V>> entries) {
      Iterator<Cache.Entry<? extends K, ? extends V>> iterator = entries.iterator();

      while (iterator.hasNext()) {
        write(iterator.next());
        iterator.remove();
      }
    }

    @Override
    public void delete(Object key) {
      map.remove(key);
      deleteCount.incrementAndGet();
    }

    @Override
    public void deleteAll(Collection<?> entries) {
      for (Iterator<?> keys = entries.iterator(); keys.hasNext(); ) {
        delete(keys.next());
        keys.remove();
      }
    }

    /**
     * Gets the last written value of the specified key
     *
     * @param key the key
     * @return the value last written
     */
    public V get(K key) {
      return map.get(key);
    }

    /**
     * Determines if there is a last written value for the specified key
     *
     * @param key the key
     * @return true if there is a last written value
     */
    public boolean containsKey(K key) {
      return map.containsKey(key);
    }

    /**
     * Gets the number of writes that have occurred.
     *
     * @return the number of writes
     */
    public long getWriteCount() {
      return writeCount.get();
    }

    /**
     * Gets the number of deletes that have occurred.
     *
     * @return the number of writes
     */
    public long getDeleteCount() {
      return deleteCount.get();
    }

    /**
     * Clears the contents of stored values.
     */
    public void clear() {
      map.clear();
      this.writeCount = new AtomicLong();
      this.deleteCount = new AtomicLong();
    }
  }
}

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

import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.MutableConfiguration;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test for Cache.
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheInvokeTest extends CacheTestSupport<Integer, String> {
  private static long SLEEP_HIGH = 10L;
  private static long SLEEP_LOW = 1L;

  /**
   * Rule used to exclude tests
   */
  @Rule
  public ExcludeListExcluder rule = new ExcludeListExcluder(CacheInvokeTest.class);

  @Override
  protected MutableConfiguration<Integer, String> newMutableConfiguration() {
    return new MutableConfiguration<Integer, String>(Integer.class, String.class);
  }

  @Test
  public void nullKey() {
    try {
      cache.invokeEntryProcessor(null, new MockEntryProcessor<Integer, String, Void>());
      fail("null key");
    } catch (NullPointerException e) {
      //
    }
  }

  @Test
  public void nullProcessor() {
    try {
      cache.invokeEntryProcessor(123, null);
      fail("null key");
    } catch (NullPointerException e) {
      //
    }
  }

  @Test
  public void close() {
    cache.close();
    try {
      cache.invokeEntryProcessor(123, new MockEntryProcessor<Integer, String, Void>());
      fail("null key");
    } catch (IllegalStateException e) {
      //
    }
  }

  private static class MockEntryProcessor<K, V, T> implements Cache.EntryProcessor<K, V, T>, Serializable {

    @Override
    public T process(Cache.MutableEntry<K, V> kvMutableEntry, Object... arguments) {
      throw new UnsupportedOperationException();
    }
  }

  static public class NoValueNoMutationEntryProcessor<K, V, T> extends MockEntryProcessor<K, V, T> {
    NoValueNoMutationEntryProcessor(T ret) {
      this.ret = ret;
    }

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
      assertFalse(entry.exists());
      return ret;
    }

    private final T ret;
  }

  ;

  @Test
  public void noValueNoMutation() {
    final Integer key = 123;
    final Integer ret = 456;
    Cache.EntryProcessor<Integer, String, Integer> processor =
        new NoValueNoMutationEntryProcessor<Integer, String, Integer>(ret);
    assertEquals(ret, cache.invokeEntryProcessor(key, processor));
    assertFalse(cache.containsKey(key));
  }

  static public class VarArgumentsPassedInEntryProcessor<K, V, T> extends MockEntryProcessor<K, V, T> {
    VarArgumentsPassedInEntryProcessor(T ret) {
      this.ret = ret;
    }

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
      assertFalse(entry.exists());
      assertEquals("These", arguments[0]);
      assertEquals("are", arguments[1]);
      assertEquals("arguments", arguments[2]);
      assertEquals(1L, arguments[3]);
      return ret;
    }

    private final T ret;
  }

  @Test
  public void varArgumentsPassedIn() {
    final Integer key = 123;
    final Integer ret = 456;
    Cache.EntryProcessor<Integer, String, Integer> processor =
        new VarArgumentsPassedInEntryProcessor<Integer, String, Integer>(ret);
    assertEquals(ret, cache.invokeEntryProcessor(key, processor, "These", "are", "arguments", 1L));
    assertFalse(cache.containsKey(key));
  }

  static public class NoValueSetValueEntryProcessor<K, V, T> extends MockEntryProcessor<K, V, T> {
    NoValueSetValueEntryProcessor(T ret, V newValue) {
      this.ret = ret;
      this.newValue = newValue;
    }

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
      assertFalse(entry.exists());
      entry.setValue(newValue);
      assertTrue(entry.exists());
      return ret;
    }

    private final V newValue;
    private final T ret;
  }


  @Test
  public void noValueSetValue() {
    final Integer key = 123;
    final Integer ret = 456;
    final String newValue = "abc";
    Cache.EntryProcessor<Integer, String, Integer> processor =
        new NoValueSetValueEntryProcessor<Integer, String, Integer>(ret, newValue);
    assertEquals(ret, cache.invokeEntryProcessor(key, processor));
    assertEquals(newValue, cache.get(key));
  }

  static public class NoValueExceptionEntryProcessor<K, V, T> extends MockEntryProcessor<K, V, T> {
    NoValueExceptionEntryProcessor(V setValue) {
      this.setValue = setValue;
    }

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
      assertFalse(entry.exists());
      entry.setValue(setValue);
      assertTrue(entry.exists());
      throw new IllegalAccessError();
    }

    private final V setValue;
  }

  @Test
  public void noValueException() {
    final Integer key = 123;
    final String setValue = "abc";
    Cache.EntryProcessor<Integer, String, Void> processor =
        new NoValueExceptionEntryProcessor<Integer, String, Void>(setValue);
    try {
      cache.invokeEntryProcessor(key, processor);
      fail();
    } catch (CacheException e) {
      assertTrue(e.getCause() instanceof IllegalAccessError);
    }
    assertFalse(cache.containsKey(key));
  }

  static public class ExistingReplaceEntryProcessor<K, V, T> extends MockEntryProcessor<K, V, T> {
    ExistingReplaceEntryProcessor(V oldValue, V newValue) {
      this.newValue = newValue;
      this.oldValue = oldValue;
    }

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
      assertTrue(entry.exists());
      V value1 = entry.getValue();
      assertEquals(oldValue, entry.getValue());
      entry.setValue(newValue);
      assertTrue(entry.exists());
      assertEquals(newValue, entry.getValue());
      return (T) value1;
    }

    private final V oldValue;
    private final V newValue;
  }

  @Test
  public void existingReplace() {
    final Integer key = 123;
    final String oldValue = "abc";
    final String newValue = "def";
    Cache.EntryProcessor<Integer, String, String> processor =
        new ExistingReplaceEntryProcessor<Integer, String, String>(oldValue, newValue);
    cache.put(key, oldValue);
    assertEquals(oldValue, cache.invokeEntryProcessor(key, processor));
    assertEquals(newValue, cache.get(key));
  }

  static public class ExistingExceptionEntryProcessor<K, V, T> extends MockEntryProcessor<K, V, T> {
    ExistingExceptionEntryProcessor(V oldValue, V newValue) {
      this.newValue = newValue;
      this.oldValue = oldValue;
    }

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
      assertEquals(oldValue, entry.getValue());
      entry.setValue(newValue);
      assertTrue(entry.exists());
      assertEquals(newValue, entry.getValue());
      throw new IllegalAccessError();
    }

    private final V oldValue;
    private final V newValue;
  }

  @Test
  public void existingException() {
    final Integer key = 123;
    final String oldValue = "abc";
    final String newValue = "def";
    Cache.EntryProcessor<Integer, String, String> processor =
        new ExistingExceptionEntryProcessor<Integer, String, String>(oldValue, newValue);
    cache.put(key, oldValue);
    try {
      cache.invokeEntryProcessor(key, processor);
      fail();
    } catch (CacheException e) {
      assertTrue(e.getCause() instanceof IllegalAccessError);
    }
    assertEquals(oldValue, cache.get(key));
  }

  static public class RemoveMissingEntryProcessor<K, V, T> extends MockEntryProcessor<K, V, T> {
    RemoveMissingEntryProcessor(T ret) {
      this.ret = ret;
    }

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
      assertFalse(entry.exists());
      entry.setValue((V) "aba");
      assertTrue(entry.exists());
      entry.remove();
      assertFalse(entry.exists());
      return ret;
    }

    private final T ret;
  }

  @Test
  public void removeMissing() {
    final Integer key = 123;
    final Integer ret = 456;
    Cache.EntryProcessor<Integer, String, Integer> processor =
        new RemoveMissingEntryProcessor<Integer, String, Integer>(ret);
    assertEquals(ret, cache.invokeEntryProcessor(key, processor));
    assertFalse(cache.containsKey(key));
  }

  static public class RemoveThereEntryProcessor<K, V, T> extends MockEntryProcessor<K, V, T> {

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
      assertTrue(entry.exists());
      String oldValue = (String) entry.getValue();
      entry.remove();
      assertFalse(entry.exists());
      return (T) oldValue;
    }
  }

  @Test
  public void removeThere() {
    final Integer key = 123;
    final String oldValue = "abc";
    Cache.EntryProcessor<Integer, String, String> processor =
        new RemoveThereEntryProcessor<Integer, String, String>();
    cache.put(key, oldValue);
    assertEquals(oldValue, cache.invokeEntryProcessor(key, processor));
    assertFalse(cache.containsKey(key));
  }


  static public class RemoveExceptionEntryProcessor<K, V, T> extends MockEntryProcessor<K, V, T> {

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
      assertTrue(entry.exists());
      entry.remove();
      assertFalse(entry.exists());
      throw new IllegalAccessError();
    }
  }

  @Test
  public void removeException() {
    final Integer key = 123;
    final String oldValue = "abc";
    Cache.EntryProcessor<Integer, String, Void> processor =
        new RemoveExceptionEntryProcessor<Integer, String, Void>();
    cache.put(key, oldValue);
    try {
      cache.invokeEntryProcessor(key, processor);
      fail();
    } catch (CacheException e) {
      assertTrue(e.getCause() instanceof IllegalAccessError);
    }
    assertEquals(oldValue, cache.get(key));
  }
}

/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.processor;

import org.junit.Assert;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.MutableEntry;
import java.io.Serializable;

/**
 * Specialized Entry processor that can return a different type and value than the entry value.
 *
 * @param <K> key type
 * @param <V> value type
 * @param <T> process return type
 */
public class SetValueCreateEntryReturnDifferentTypeEntryProcessor<K, V, T> implements EntryProcessor<K, V, T>, Serializable {

  /**
   * The value to set.
   */
  private V value;

  /**
   * The result to return.
   */
  private T result;


  /**
   * Constructs a {@link SetValueCreateEntryReturnDifferentTypeEntryProcessor}.
   *
   * @param result   process result
   * @param newValue new entry value
   */
  public SetValueCreateEntryReturnDifferentTypeEntryProcessor(T result, V newValue) {
    this.value = newValue;
    this.result = result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T process(MutableEntry<K, V> entry, Object... arguments) {
    Assert.assertFalse(entry.exists());
    entry.setValue(value);
    Assert.assertTrue(entry.exists());

    return result;
  }
}

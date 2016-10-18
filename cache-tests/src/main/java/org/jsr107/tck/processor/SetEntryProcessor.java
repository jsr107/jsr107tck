/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package org.jsr107.tck.processor;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.MutableEntry;
import java.io.Serializable;

/**
 * An {@link EntryProcessor} to set the value of an entry.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class SetEntryProcessor<K, V> implements EntryProcessor<K, V, V>, Serializable {

  /**
   * The value to set.
   */
  private V value;

  /**
   * Constructs a {@link SetEntryProcessor}.
   *
   * @param value entry value
   */
  public SetEntryProcessor(V value) {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V process(MutableEntry<K, V> entry, Object... arguments) {
    entry.setValue(value);

    return entry.getValue();
  }

  /**
   * Obtains the value to set.
   *
   * @return the value to set
   */
  public V getValue() {
    return value;
  }
}

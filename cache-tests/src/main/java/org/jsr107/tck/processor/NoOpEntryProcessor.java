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
 * An {@link javax.cache.processor.EntryProcessor} which does nothing.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class NoOpEntryProcessor<K, V> implements EntryProcessor<K, V, V>, Serializable {

  /**
   * {@inheritDoc}
   */
  @Override
  public V process(MutableEntry<K, V> entry, Object... arguments) {
    //noop
    return null;
  }
}

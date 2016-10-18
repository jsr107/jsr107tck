/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.integration;

import javax.cache.integration.CacheLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link CacheLoader} implementation that always returns <code>null</code>
 * values for keys requested.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @author Brian Oliver
 */
public class NullValueCacheLoader<K, V> implements CacheLoader<K, V> {

  /**
   * {@inheritDoc}
   */
  @Override
  public V load(K key) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<K, V> loadAll(Iterable<? extends K> keys) {
    HashMap<K, V> map = new HashMap<K, V>();
    for (K key : keys) {
      map.put(key, null);
    }

    return map;
  }
}

/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.integration;

import javax.cache.integration.CacheLoader;
import java.util.Map;

/**
 * A {@link CacheLoader} implementation that always throws a
 * {@link UnsupportedOperationException}, regardless of the request.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @author Brian Oliver
 */
public class FailingCacheLoader<K, V> extends RecordingCacheLoader {

  /**
   * {@inheritDoc}
   */
  @Override
  public V load(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map loadAll(Iterable keys) {
    throw new UnsupportedOperationException();
  }
}

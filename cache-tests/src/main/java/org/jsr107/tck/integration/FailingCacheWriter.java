/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheWriter;
import java.util.Collection;

/**
 * A {@link CacheWriter} implementation that always throws a
 * {@link UnsupportedOperationException}, regardless of the request.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class FailingCacheWriter<K,V> extends RecordingCacheWriter<K,V> {

    @Override
    public void write(Cache.Entry<? extends K, ? extends V> entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeAll(Collection<Cache.Entry<? extends K, ? extends V>> entries) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Collection<?> keys) {
        throw new UnsupportedOperationException();
    }
}

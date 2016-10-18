/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */



package org.jsr107.tck.processor;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import java.io.Serializable;

/**
 * EntryProcessor that throws clazz exception.
 *
 * @param <K> key type
 * @param <V> value type
 * @param <T> return type
 */
public class ThrowExceptionEntryProcessor<K, V, T> implements EntryProcessor<K,
    V, T>, Serializable {

    private final Class<? extends Throwable> clazz;

    public ThrowExceptionEntryProcessor(Class<? extends Throwable> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T process(MutableEntry<K, V> entry, Object... arguments) {
        try {
            throw clazz.newInstance();
        } catch (Throwable t) {
          if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
          } else {

            // only wrapper checked exceptions.
            throw new EntryProcessorException(t);
          }
        }
    }
}

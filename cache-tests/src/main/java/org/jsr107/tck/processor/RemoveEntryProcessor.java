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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Remove entry processor
 * @param <K>  key type
 * @param <V>  value type
 * @param <T>  process return type
 */
public class RemoveEntryProcessor<K, V, T> implements EntryProcessor<K, V,
    T>, Serializable {

    private final boolean assertExists;

    public RemoveEntryProcessor(){
        this(false);
    }

    public RemoveEntryProcessor(boolean assertExists) {
        this.assertExists = assertExists;
    }

    @Override
    public T process(MutableEntry<K, V> entry, Object... arguments) {
        T result = null;
        if (assertExists) {
            assertTrue(entry.exists());
            result = (T)entry.getValue();
        }
        entry.remove();
        assertFalse(entry.exists());

        return result;
    }
}

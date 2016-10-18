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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Replace entry processor
 * @param <K>  key type
 * @param <V>  value type
 * @param <T>  process return type
 */
public class ReplaceEntryProcessor<K, V, T> implements EntryProcessor<K, V,
    T>, Serializable {
    private final V newValue;
    private final V oldValue;

    public ReplaceEntryProcessor(V oldValue, V newValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    @Override
    public T process(MutableEntry<K, V> entry, Object... arguments) {
        assertTrue(entry.exists());
        V value1 = entry.getValue();
        assertEquals(oldValue, entry.getValue());
        entry.setValue(newValue);
        assertTrue(entry.exists());
        assertEquals(newValue, entry.getValue());

        return (T) value1;
    }
}

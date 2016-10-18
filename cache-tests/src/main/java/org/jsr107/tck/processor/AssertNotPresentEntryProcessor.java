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

/**
 * Assert entry is not present entry processor.
 * @param <K>  key type
 * @param <V>  value type
 * @param <T>  process return type
 */
public class AssertNotPresentEntryProcessor<K, V, T> implements EntryProcessor<K,
    V, T>, Serializable {
    private final T ret;

    public AssertNotPresentEntryProcessor(T ret) {
        this.ret = ret;
    }

    @Override
    public T process(MutableEntry<K, V> entry, Object... arguments) {
        assertFalse(entry.exists());

        return ret;
    }
}

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
 *
 * @param <K> key class
 * @param <V> value class
 * @param <T> process return type
 */
public class MultiArgumentHandlingEntryProcessor<K, V, T> implements EntryProcessor<K, V, T>, Serializable {
    private final T ret;

    public MultiArgumentHandlingEntryProcessor(T ret) {
        this.ret = ret;
    }

    @Override
    public T process(MutableEntry<K, V> entry, Object... arguments) {
        Assert.assertEquals("These", arguments[0]);
        Assert.assertEquals("are", arguments[1]);
        Assert.assertEquals("arguments", arguments[2]);
        Assert.assertEquals(1L, arguments[3]);

        return ret;
    }
}

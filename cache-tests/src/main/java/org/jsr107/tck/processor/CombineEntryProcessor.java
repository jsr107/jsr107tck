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
 * Combine multiple elementary processors into a composite.
 * @param <K>  key type
 * @param <V>  value type
 */
public class CombineEntryProcessor<K,V> implements EntryProcessor<K,V,
    Object[]>, Serializable {

    private EntryProcessor<K,V,Object>[] processors;

    public CombineEntryProcessor(EntryProcessor<K,V,Object>[] processors) {
        if (processors == null) {
            throw new NullPointerException();
        }
        this.processors = processors;
    }


    @Override
    public Object[] process(MutableEntry<K, V> entry, Object... arguments) {
        Object[] results = new Object[processors.length];

        for (int i = 0; i < processors.length; i++) {
            results[i] = processors[i].process(entry, arguments);
        }
        return results;
    }
}

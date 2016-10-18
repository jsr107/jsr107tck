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
 * Set entry processor that generates a value based on the entries key.
 * The generated value is concatenation of valuePrefix + entry.getKey() + valuePrefix.
 *
 * @param <K> the key type
 *
 * @author Joe Fialli
 */
public class SetEntryWithComputedValueProcessor<K> implements EntryProcessor<K,
    String, String>, Serializable {
    private String valuePrefix;
    private String valuePostfix;

    public SetEntryWithComputedValueProcessor(String valuePrefix, String valuePostfix) {
        this.valuePrefix = valuePrefix;
        this.valuePostfix = valuePostfix;
    }

    @Override
    public String process(MutableEntry<K, String> entry, Object... arguments) {
        StringBuffer computedValue = new StringBuffer();
        if (valuePrefix != null) {
            computedValue.append(valuePrefix);
        }
        computedValue.append(entry.getKey().toString());
        if (valuePostfix != null) {
            computedValue.append(valuePostfix);
        }

        // Not trying to be efficient here.
        // For testing purposes in entry processor, follow the set with a get.
        // It would be more efficient to just return value that was passed to setValue.
        // This is testing the entry processor path of create or update an entry followed by an entry access.
        entry.setValue(computedValue.toString());
        return entry.getValue();
    }
}

/**
 *  Copyright 2011 Terracotta, Inc.
 *  Copyright 2011 Oracle, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */



package org.jsr107.tck.entryprocessor;

import static org.junit.Assert.assertFalse;

import javax.cache.Cache;
import java.io.Serializable;

/**
 * Assert entry is not present entry processor.
 * @param <K>  key type
 * @param <V>  value type
 * @param <T>  process return type
 */
public class AssertNotPresentEntryProcessor<K, V, T> implements Cache.EntryProcessor<K, V, T>, Serializable {
    private final T ret;

    public AssertNotPresentEntryProcessor(T ret) {
        this.ret = ret;
    }

    @Override
    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
        assertFalse(entry.exists());

        return ret;
    }
}

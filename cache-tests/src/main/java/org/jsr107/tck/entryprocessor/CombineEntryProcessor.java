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

import javax.cache.Cache;
import java.io.Serializable;

/**
 * Combine multiple elementary processors into a composite.
 * @param <K>  key type
 * @param <V>  value type
 */
public class CombineEntryProcessor<K,V> implements Cache.EntryProcessor<K,V,Object[]>, Serializable {

    private Cache.EntryProcessor<K,V,Object>[] processors;

    public CombineEntryProcessor(Cache.EntryProcessor<K,V,Object>[] processors) {
        if (processors == null) {
            throw new NullPointerException();
        }
        this.processors = processors;
    }


    @Override
    public Object[] process(Cache.MutableEntry<K, V> entry, Object... arguments) {
        Object[] results = new Object[processors.length];

        for (int i = 0; i < processors.length; i++) {
            results[i] = processors[i].process(entry, arguments);
        }
        return results;
    }
}

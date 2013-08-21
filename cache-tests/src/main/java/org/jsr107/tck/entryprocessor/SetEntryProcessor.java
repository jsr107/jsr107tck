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

import java.io.Serializable;
import javax.cache.Cache;

/**
 * Basic set entry processor.
 * @param <K>  key type
 * @param <V>  value type
 * @param <T>  process return type
 */
public class SetEntryProcessor<K, V, T> implements Cache.EntryProcessor<K, V, T>, Serializable {
    private V value;

    public SetEntryProcessor(V value) {
        this.value = value;
    }

    public T process(Cache.MutableEntry<K, V> entry, Object... arguments) {
        entry.setValue(value);

        return (T) entry.getValue();
    }

    public V getValue() {
        return value;
    }
}

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

import org.junit.Assert;

import javax.cache.processor.MutableEntry;

/**
 * Specialized Entry processor that can return a different type and value than the entry value.
 *
 * @param <K>  key type
 * @param <V>  value type
 * @param <T>  process return type
 *
 */
public class SetValueCreateEntryReturnDifferentTypeEntryProcessor<K, V, T> extends SetEntryProcessor<K, V, T> {
    private final T ret;

    public SetValueCreateEntryReturnDifferentTypeEntryProcessor(T ret, V newValue) {
        super(newValue);
        this.ret = ret;
    }

    @Override
    public T process(MutableEntry<K, V> entry, Object... arguments) {
        Assert.assertFalse(entry.exists());
        super.process(entry, arguments);
        Assert.assertTrue(entry.exists());

        return ret;
    }
}

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
package javax.cache;

import org.junit.Rule;
import org.junit.Test;

import javax.cache.util.ExcludeListExcluder;
import java.util.Collection;

import static org.junit.Assert.fail;

/**
 * Unit test for Cache.
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheInvokeTest extends CacheTestSupport<Integer, String> {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

    @Test
    public void nullKey() {
        Cache.EntryProcessor<Integer, String> processor = new Cache.EntryProcessor<Integer, String>() {

            @Override
            public Object processAll(Collection<Cache.MutableEntry<? extends Integer, ? extends String>> mutableEntries) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object process(Cache.MutableEntry<Integer, String> integerStringMutableEntry) {
                throw new UnsupportedOperationException();
            }
        };

        try {
            cache.invokeEntryProcessor(null, processor);
            fail("null key");
        } catch (NullPointerException e) {
            //
        }
    }

    @Test
    public void nullProcessor() {
        try {
            cache.invokeEntryProcessor(123, null);
            fail("null key");
        } catch (NullPointerException e) {
            //
        }
    }

    @Test
    public void notStarted() {
        cache.stop();
        try {
            cache.invokeEntryProcessor(null, null);
            fail("null key");
        } catch (IllegalStateException e) {
            //
        }
    }
}

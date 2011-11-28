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

import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.util.ExcludeListExcluder;
import java.security.Key;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        try {
            cache.invokeEntryProcessor(null, new MockEntryProcessor<Integer, String>());
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
            cache.invokeEntryProcessor(123, new MockEntryProcessor<Integer, String>());
            fail("null key");
        } catch (IllegalStateException e) {
            //
        }
    }

    @Test
    public void noValueNoMutation() {
        final Integer key = 123;
        final Integer ret = 456;
        Cache.EntryProcessor<Integer, String> processor = new MockEntryProcessor<Integer, String>() {
            @Override
            public Object process(Cache.MutableEntry<Integer, String> entry) {
                assertFalse(entry.exists());
                return ret;
            }
        };
        assertEquals(ret, cache.invokeEntryProcessor(key, processor));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void noValueSetValue() {
        final Integer key = 123;
        final Integer ret = 456;
        final String newValue = "abc";
        Cache.EntryProcessor<Integer, String> processor = new MockEntryProcessor<Integer, String>() {
            @Override
            public Object process(Cache.MutableEntry<Integer, String> entry) {
                assertFalse(entry.exists());
                entry.setValue(newValue);
                assertTrue(entry.exists());
                return ret;
            }
        };
        assertEquals(ret, cache.invokeEntryProcessor(key, processor));
        assertEquals(newValue, cache.get(key));
    }

    @Test
    public void noValueException() {
        final Integer key = 123;
        final String setValue = "abc";
        Cache.EntryProcessor<Integer, String> processor = new MockEntryProcessor<Integer, String>() {
            @Override
            public Object process(Cache.MutableEntry<Integer, String> entry) {
                assertFalse(entry.exists());
                entry.setValue(setValue);
                assertTrue(entry.exists());
                throw new IllegalAccessError();
            }
        };
        try {
            cache.invokeEntryProcessor(key, processor);
            fail();
        } catch (IllegalAccessError e) {
            //
        }
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void existingReplace() {
        final Integer key = 123;
        final String oldValue = "abc";
        final String newValue = "def";
        Cache.EntryProcessor<Integer, String> processor = new MockEntryProcessor<Integer, String>() {
            @Override
            public Object process(Cache.MutableEntry<Integer, String> entry) {
                assertTrue(entry.exists());
                String value1 = entry.getValue();
                assertEquals(oldValue, entry.getValue());
                entry.setValue(newValue);
                assertTrue(entry.exists());
                assertEquals(newValue, entry.getValue());
                return value1;
            }
        };
        cache.put(key, oldValue);
        assertEquals(oldValue, cache.invokeEntryProcessor(key, processor));
        assertEquals(newValue, cache.get(key));
    }

    @Test
    public void existingException() {
        final Integer key = 123;
        final String oldValue = "abc";
        final String newValue = "def";
        Cache.EntryProcessor<Integer, String> processor = new MockEntryProcessor<Integer, String>() {
            @Override
            public Object process(Cache.MutableEntry<Integer, String> entry) {
                assertTrue(entry.exists());
                String value1 = entry.getValue();
                assertEquals(oldValue, entry.getValue());
                entry.setValue(newValue);
                assertTrue(entry.exists());
                assertEquals(newValue, entry.getValue());
                throw new IllegalAccessError();
            }
        };
        cache.put(key, oldValue);
        try {
            cache.invokeEntryProcessor(key, processor);
            fail();
        } catch (IllegalAccessError e) {
            //
        }
        assertEquals(oldValue, cache.get(key));
    }

    @Test
    public void removeMissing() {
        final Integer key = 123;
        final Integer ret = 456;
        Cache.EntryProcessor<Integer, String> processor = new MockEntryProcessor<Integer, String>() {
            @Override
            public Object process(Cache.MutableEntry<Integer, String> entry) {
                assertFalse(entry.exists());
                entry.setValue("aba");
                assertTrue(entry.exists());
                entry.remove();
                assertFalse(entry.exists());
                return ret;
            }
        };
        assertEquals(ret, cache.invokeEntryProcessor(key, processor));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void removeThere() {
        final Integer key = 123;
        final String oldValue = "abc";
        Cache.EntryProcessor<Integer, String> processor = new MockEntryProcessor<Integer, String>() {
            @Override
            public Object process(Cache.MutableEntry<Integer, String> entry) {
                assertTrue(entry.exists());
                String oldValue = entry.getValue();
                entry.remove();
                assertFalse(entry.exists());
                return oldValue;
            }
        };
        cache.put(key, oldValue);
        assertEquals(oldValue, cache.invokeEntryProcessor(key, processor));
        assertFalse(cache.containsKey(key));
    }

    @Test
    public void removeException() {
        final Integer key = 123;
        final String oldValue = "abc";
        Cache.EntryProcessor<Integer, String> processor = new MockEntryProcessor<Integer, String>() {
            @Override
            public Object process(Cache.MutableEntry<Integer, String> entry) {
                assertTrue(entry.exists());
                entry.remove();
                assertFalse(entry.exists());
                throw new IllegalAccessError();
            }
        };
        cache.put(key, oldValue);
        try {
            cache.invokeEntryProcessor(key, processor);
            fail();
        } catch (IllegalAccessError e) {
            //
        }
        assertEquals(oldValue, cache.get(key));
    }

    @Test
    public void twoThreads() throws Exception {
        final Integer key = 123;
        final String value1 = "a1";
        final String value2 = "a2";
        final String value3 = "a3";

        cache.put(key, value1);
        Thread t1 = new Thread(new MyRunnable<Integer, String>(cache, key, value1, value2, 100L));
        Thread t2 = new Thread(new MyRunnable<Integer, String>(cache, key, value2, value3, 1L));
        t1.start();
        Thread.sleep(10L);
        t2.start();
        t1.join();
        t2.join();
        assertEquals(value3, cache.get(key));
    }

    private static class MockEntryProcessor<K, V> implements Cache.EntryProcessor<K, V> {
        @Override
        public Object processAll(Collection<Cache.MutableEntry<? extends K, ? extends V>> mutableEntries) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object process(Cache.MutableEntry<K, V> kvMutableEntry) {
            throw new UnsupportedOperationException();
        }
    }

    private static class MyRunnable<K, V> implements Runnable {
        private final Cache<K,V> cache;
        private final K key;
        private final V oldValue;
        private final V newValue;
        private final long sleep;

        public MyRunnable(Cache<K, V> cache, K key, V oldValue, V newValue, long sleep) {
            this.cache = cache;
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.sleep = sleep;
        }

        @Override
        public void run() {
            Cache.EntryProcessor<K, V> processor = new MockEntryProcessor<K, V>() {

                @Override
                public Object process(Cache.MutableEntry<K, V> entry) {
                    assertEquals(oldValue, entry.getValue());
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        //
                    }
                    entry.setValue(newValue);
                    return oldValue;
                }
            };
            cache.invokeEntryProcessor(key, processor);
        }
    }
}

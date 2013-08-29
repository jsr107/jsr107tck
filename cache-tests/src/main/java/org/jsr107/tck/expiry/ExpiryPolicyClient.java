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
package org.jsr107.tck.expiry;

import org.jsr107.tck.support.CacheClient;
import org.jsr107.tck.support.Operation;

import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

/**
 *
 * @param <K>
 */
public class ExpiryPolicyClient<K> extends CacheClient implements ExpiryPolicy<K> {

    /**
     * Constructs a {@link ExpiryPolicyClient}.
     *
     * @param address the {@link java.net.InetAddress} on which to connect to the {@link org.jsr107.tck.expiry.ExpiryPolicyServer}
     * @param port    the port to which to connect to the {@link org.jsr107.tck.expiry.ExpiryPolicyServer}
     */
    public ExpiryPolicyClient(InetAddress address, int port) {
        super(address, port);

        this.client = null;
    }

    @Override
    public Duration getExpiryForCreatedEntry(K key) {
        return getClient().invoke(new GetExpiryForCreatedEntryOperation<K>(key));
    }

    @Override
    public Duration getExpiryForAccessedEntry(K key) {
        return getClient().invoke(new GetExpiryForAccessedEntryOperation<K>(key));
    }

    @Override
    public Duration getExpiryForModifiedEntry(K key) {
        return getClient().invoke(new GetExpiryForModifiedEntryOperation<K>(key));
    }

    /**
     * The {@link AbstractGetExpiryForEntryOperation}.
     *
     * @param <K> the type for key
     */
    abstract private static class AbstractGetExpiryForEntryOperation<K> implements Operation<Duration> {
        /**
         * The key to load.
         */
        private K key;

        /**
         * Constructs a {@link AbstractGetExpiryForEntryOperation}.
         *
         * @param key the Key to load
         */
        public AbstractGetExpiryForEntryOperation(K key) {
            this.key = key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Duration onInvoke(ObjectInputStream ois,
                                 ObjectOutputStream oos) throws IOException, ClassNotFoundException {
            oos.writeObject(key);

            Object o = ois.readObject();

            if (o instanceof RuntimeException) {
                throw (RuntimeException) o;
            } else {
                return (Duration) o;
            }
        }
    }



    /**
     * The {@link GetExpiryForCreatedEntryOperation} representing a {@link javax.cache.expiry.ExpiryPolicy#getExpiryForCreatedEntry(Object)}
     * request.
     *
     * @param <K> the type for key
     */
    private static class GetExpiryForCreatedEntryOperation<K> extends AbstractGetExpiryForEntryOperation<K> {

        /**
         * Constructs a {@link GetExpiryForCreatedEntryOperation}.
         *
         * @param key the Key to compute expiry policy for
         */
        public GetExpiryForCreatedEntryOperation(K key) {
            super(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getType() {
            return "getExpiryForCreatedEntry";
        }
    }

    /**
     * The {@link GetExpiryForModifiedEntryOperation} representing a {@link javax.cache.expiry.ExpiryPolicy#getExpiryForCreatedEntry(Object)}
     * request.
     *
     * @param <K> the type for key
     */
    private static class GetExpiryForModifiedEntryOperation<K> extends AbstractGetExpiryForEntryOperation<K> {

        /**
         * Constructs a {@link GetExpiryForModifiedEntryOperation}.
         *
         * @param key the Key to load
         */
        public GetExpiryForModifiedEntryOperation(K key) {
            super(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getType() {
            return "getExpiryForModifiedEntry";
        }
    }

    /**
     * The {@link GetExpiryForAccessedEntryOperation} representing a {@link javax.cache.expiry.ExpiryPolicy#getExpiryForAccessedEntry(Object)}
     * request.
     *
     * @param <K> the type for key
     */
    private static class GetExpiryForAccessedEntryOperation<K> extends AbstractGetExpiryForEntryOperation<K> {

        /**
         * Constructs a {@link GetExpiryForAccessedEntryOperation}.
         *
         * @param key the Key to compute expiry policy for
         */
        public GetExpiryForAccessedEntryOperation(K key) {
            super(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getType() {
            return "getExpiryForAccessedEntry";
        }
    }

}

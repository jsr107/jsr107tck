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

import org.jsr107.tck.support.OperationHandler;
import org.jsr107.tck.support.Server;

import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @param <K>
 */
public class ExpiryPolicyServer<K> extends Server {
    /**
     * The underlying {@link javax.cache.integration.CacheLoader} that will be used to
     * load entries requested by the {@link org.jsr107.tck.integration.CacheLoaderClient}s.
     */
    private ExpiryPolicy<K> expiryPolicy;

    /**
     * Constructs an {@link ExpiryPolicyServer} (without a {@link CacheLoader} to
     * which client requests will be delegated).
     *
     * @param port the port on which to accept {@link org.jsr107.tck.integration.CacheLoaderClient} requests
     */
    public ExpiryPolicyServer(int port) {
        this(port, null);
    }

    /**
     * Constructs an CacheLoaderServer.
     *
     * @param port        the port on which to accept {@link org.jsr107.tck.integration.CacheLoaderClient} requests
     * @param expiryPolicy (optional) the {@link ExpiryPolicy} that will be used to handle
     *                    client requests
     */
    public ExpiryPolicyServer(int port, ExpiryPolicy<K> expiryPolicy) {
        super(port);

        // establish the client-server operation handlers
        addOperationHandler(new GetExpiryForCreatedEntryOperationHandler());
        addOperationHandler(new GetExpiryForAccessedEntryOperationHandler());
        addOperationHandler(new GetExpiryForModifiedEntryOperationHandler());

        this.expiryPolicy = expiryPolicy;
    }

    /**
     * Set the {@link ExpiryPolicy} the {@link ExpiryPolicyServer} should use
     * from now on.
     *
     * @param expiryPolicy the {@link ExpiryPolicy}
     */
    public void setExpiryPolicy(ExpiryPolicy<K> expiryPolicy) {
        this.expiryPolicy = expiryPolicy;
    }

    /**
     * The {@link org.jsr107.tck.support.OperationHandler} for a {@link ExpiryPolicy#getExpiryForCreatedEntry(Object)}} operation.
     */
    public class GetExpiryForCreatedEntryOperationHandler implements OperationHandler {
        @Override
        public String getType() {
            return "getExpiryForCreatedEntry";
        }

        @Override
        public void onProcess(ObjectInputStream ois,
                              ObjectOutputStream oos) throws IOException, ClassNotFoundException {

            if (expiryPolicy == null) {
                throw new NullPointerException("The ExpiryPolicy for the ExpiryPolicyServer has not be set");
            } else {
                K key = (K) ois.readObject();
                try {
                    Duration duration = expiryPolicy.getExpiryForCreatedEntry(key);
                    oos.writeObject(duration);
                } catch (Exception e) {
                    oos.writeObject(e);
                }
            }
        }
    }

    /**
     * The {@link org.jsr107.tck.support.OperationHandler} for a {@link ExpiryPolicy#getExpiryForAccessedEntry(Object)}} operation.
     */
    public class GetExpiryForAccessedEntryOperationHandler implements OperationHandler {
        @Override
        public String getType() {
            return "getExpiryForAccessedEntry";
        }

        @Override
        public void onProcess(ObjectInputStream ois,
                              ObjectOutputStream oos) throws IOException, ClassNotFoundException {

            if (expiryPolicy == null) {
                throw new NullPointerException("The ExpiryPolicy for the ExpiryPolicyServer has not be set");
            } else {
                K key = (K) ois.readObject();
                try {
                    Duration duration = expiryPolicy.getExpiryForAccessedEntry(key);
                    oos.writeObject(duration);
                } catch (Exception e) {
                    oos.writeObject(e);
                }
            }
        }
    }

    /**
     * The {@link org.jsr107.tck.support.OperationHandler} for a {@link ExpiryPolicy#getExpiryForModifiedEntry(Object)}} operation.
     */
    public class GetExpiryForModifiedEntryOperationHandler implements OperationHandler {
        @Override
        public String getType() {
            return "getExpiryForModifiedEntry";
        }

        @Override
        public void onProcess(ObjectInputStream ois,
                              ObjectOutputStream oos) throws IOException, ClassNotFoundException {

            if (expiryPolicy == null) {
                throw new NullPointerException("The ExpiryPolicy for the ExpiryPolicyServer has not be set");
            } else {
                K key = (K) ois.readObject();
                try {
                    Duration duration = expiryPolicy.getExpiryForModifiedEntry(key);
                    oos.writeObject(duration);
                } catch (Exception e) {
                    oos.writeObject(e);
                }
            }
        }
    }
}

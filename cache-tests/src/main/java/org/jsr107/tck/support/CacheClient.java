/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.support;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * A client-side base class for delegating requests to a server.
 *
 * @author Brian Oliver
 * @author Joe Fialli
 */
public class CacheClient implements AutoCloseable, Serializable {
    /**
     * The {@link java.net.InetAddress} on which to connect to the {@link org.jsr107.tck.integration.CacheLoaderServer}.
     */
    protected InetAddress address;

    /**
     * The port on which to connect to the {@link org.jsr107.tck.integration.CacheLoaderServer}.
     */
    protected int port;

    /**
     * The {@link org.jsr107.tck.support.Client} connection to the {@link org.jsr107.tck.integration.CacheLoaderServer}.
     */
    protected transient Client client;

    protected CacheClient(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.client = null;
    }

    /**
     * Obtains the internal {@link Client} used to communicate with the
     * {@link org.jsr107.tck.integration.CacheLoaderServer}.  If the {@link Client} is not connected, a
     * connection will be attempted.
     *
     * @return the {@link Client}
     */
    protected synchronized Client getClient() {
        if (client == null) {
            try {
                client = new Client(address, port);
            } catch (Exception e) {
                throw new RuntimeException("Failed to acquire Client address:" + address + ":" + port, e);
            }
        }

        return client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void close() throws Exception {
        if (client != null) {
            try {
                client.close();
            } finally {
                client = null;
            }
        }
    }
}

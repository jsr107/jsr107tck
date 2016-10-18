/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
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
 */
public class ExpiryPolicyClient extends CacheClient implements ExpiryPolicy {

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

  /**
   * {@inheritDoc}
   */
  @Override
  public Duration getExpiryForCreation() {
    return getClient().invoke(new GetExpiryOperation(ExpiryPolicyServer.EntryOperation.CREATION));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Duration getExpiryForAccess() {
    return getClient().invoke(new GetExpiryOperation(ExpiryPolicyServer.EntryOperation.ACCESSED));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Duration getExpiryForUpdate() {
    return getClient().invoke(new GetExpiryOperation(ExpiryPolicyServer.EntryOperation.UPDATED));
  }

  /**
   * The {@link GetExpiryOperation}.
   */
  private static class GetExpiryOperation implements Operation<Duration> {

    private ExpiryPolicyServer.EntryOperation entryOperation;

    /**
     * Constructs a {@link GetExpiryOperation}.
     */
    public GetExpiryOperation(ExpiryPolicyServer.EntryOperation entryOperation) {
      this.entryOperation = entryOperation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
      return "getExpiry";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration onInvoke(ObjectInputStream ois,
                             ObjectOutputStream oos) throws IOException, ClassNotFoundException {
      oos.writeObject(entryOperation.name());

      Object o = ois.readObject();

      if (o instanceof RuntimeException) {
        throw (RuntimeException) o;
      } else {
        return (Duration) o;
      }
    }
  }
}

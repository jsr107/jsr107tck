/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package org.jsr107.tck.event;

import org.jsr107.tck.support.CacheClient;
import org.jsr107.tck.support.Operation;

import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

/**
 * A {@link javax.cache.event.CacheEntryListener} that delegates requests to a
 * {@link org.jsr107.tck.event.CacheEntryListenerServer}. Added to support testing TCK in a distributed
 * environment.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @author Joe Fialli
 */
public class CacheEntryListenerClient<K, V> extends CacheClient
  implements CacheEntryListener<K, V>,
  CacheEntryCreatedListener<K, V>, CacheEntryUpdatedListener<K, V>,
  CacheEntryRemovedListener<K, V>, CacheEntryExpiredListener<K, V> {

  /**
   * Constructs a {@link CacheEntryListenerClient}.
   *
   * @param address the {@link java.net.InetAddress} on which to connect to the
   * {@link org.jsr107.tck.event.CacheEntryListenerServer}
   * @param port    the port to which to connect to the {@link org.jsr107.tck.event.CacheEntryListenerServer}
   */
  public CacheEntryListenerClient(InetAddress address, int port) {
    super(address, port);

    this.client = null;
  }

  @Override
  public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    for (CacheEntryEvent<? extends K, ? extends V> event : cacheEntryEvents) {
      getClient().invoke(new OnCacheEntryEventHandler<K, V>(event));
    }
  }

  @Override
  public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {

    // since ExpiryEvents are processed asynchronously, this may cause issues.
    // the test do not currently delay waiting for asynchronous expiry events to complete processing.
    // not breaking anything now, so leaving in for time being.
    for (CacheEntryEvent<? extends K, ? extends V> event : cacheEntryEvents) {
      getClient().invoke(new OnCacheEntryEventHandler<K, V>(event));
    }
  }

  @Override
  public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
    for (CacheEntryEvent<? extends K, ? extends V> event : cacheEntryEvents) {
      getClient().invoke(new OnCacheEntryEventHandler<K, V>(event));
    }
  }

  @Override
  public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents)
    throws CacheEntryListenerException {
    for (CacheEntryEvent<? extends K, ? extends V> event : cacheEntryEvents) {
      getClient().invoke(new OnCacheEntryEventHandler<K, V>(event));
    }
  }

  /**
   * Represent a CacheEntryEvent to dispatch to server.
   * @param <K>
   * @param <V>
   */
  private static class OnCacheEntryEventHandler<K, V> implements Operation<Object> {
    private CacheEntryEvent event;

    public OnCacheEntryEventHandler(CacheEntryEvent<? extends K, ? extends V> event) {
      this.event = event;
    }

    @Override
    public String getType() {
      return event.getEventType().name();
    }

    @Override
    public Object onInvoke(ObjectInputStream ois, ObjectOutputStream oos)
      throws IOException, ClassNotFoundException, ExecutionException {
      Object result = null;
      try {
        // serialize components of source since source is definitely not serializable.
        // use these two components to resolve source in server.
        oos.writeUTF(event.getSource().getName());
        oos.writeObject(event.getSource().getCacheManager().getURI());

        // Serialize rest of CacheEntryEvent
        oos.writeObject(event.getKey());
        oos.writeObject(event.getValue());

        // commented out since there is an issue with working
        // with these next 2 fields.
        // be sure to read these in TestCacheEntryEvent.readObject
        // when trying to reinstate them.
        /*
        oos.writeBoolean(event.isOldValueAvailable());
        if (event.isOldValueAvailable()) {
          Object oldValue = null;
          try {
            oldValue = event.getOldValue();
          } catch (Throwable t) {
            t.printStackTrace();
          }
          oos.writeObject(oldValue);
        }
        */
        result = ois.readObject();
      } catch (Throwable t) {
        t.printStackTrace();
      }
      if (result instanceof CacheEntryListenerException) {
        throw ((CacheEntryListenerException)result);
      }

      // nothing to return.
      return null;
    }
  }
}

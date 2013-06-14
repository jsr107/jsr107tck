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
package org.jsr107.tck.integration;

import org.jsr107.tck.support.Client;
import org.jsr107.tck.support.Operation;

import javax.cache.Cache;
import javax.cache.integration.CacheLoader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link CacheLoader} that delegates requests to a {@link CacheLoaderServer}.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 * @author Brian Oliver
 */
public class CacheLoaderClient<K, V> implements CacheLoader<K, V>, AutoCloseable, Serializable {
  /**
   * The {@link InetAddress} on which to connect to the {@link CacheLoaderServer}.
   */
  private InetAddress address;

  /**
   * The port on which to connect to the {@link CacheLoaderServer}.
   */
  private int port;

  /**
   * The {@link Client} connection to the {@link CacheLoaderServer}.
   */
  private transient Client client;

  /**
   * Constructs a {@link CacheLoaderClient}.
   *
   * @param address the {@link InetAddress} on which to connect to the {@link CacheLoaderServer}
   * @param port    the port to which to connect to the {@link CacheLoaderServer}
   */
  public CacheLoaderClient(InetAddress address, int port) {
    this.address = address;
    this.port = port;

    this.client = null;
  }

  /**
   * Obtains the internal {@link Client} used to communicate with the
   * {@link CacheLoaderServer}.  If the {@link Client} is not connected, a
   * connection will be attempted.
   *
   * @return the {@link Client}
   */
  private synchronized Client getClient() {
    if (client == null) {
      try {
        client = new Client(address, port);
      } catch (Exception e) {
        throw new RuntimeException("Failed to acquire Client", e);
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

  @Override
  public Cache.Entry<K, V> load(final K key) {
    final V value = getClient().invoke(new LoadOperation<K, V>(key));

    return value == null ? null : new Cache.Entry<K, V>() {
      @Override
      public K getKey() {
        return key;
      }

      @Override
      public V getValue() {
        return value;
      }

      @Override
      public <T> T unwrap(Class<T> clazz) {
        throw new UnsupportedOperationException("Can't unwrap a loaded entry");
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<K, V> loadAll(Iterable<? extends K> keys) {
    //TODO: this should call the server loadall, but for now we'll just call load

    HashMap<K, V> map = new HashMap<K, V>();
    for (K key : keys) {
      Cache.Entry<K, V> entry = load(key);

      if (entry != null && entry.getValue() != null) {
        map.put(key, entry.getValue());
      }
    }
    return map;
  }


  /**
   * The {@link LoadOperation} representing a {@link CacheLoader#load(Object)}.
   *
   * @param <K> the type of keys
   * @param <V> the type of values
   */
  private static class LoadOperation<K, V> implements Operation<V> {
    /**
     * The key to load.
     */
    private K key;

    /**
     * Constructs a {@link LoadOperation}.
     *
     * @param key the Key to load
     */
    public LoadOperation(K key) {
      this.key = key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
      return "load";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V onInvoke(ObjectInputStream ois,
                      ObjectOutputStream oos) throws IOException, ClassNotFoundException {
      oos.writeObject(key);

      Object o = ois.readObject();

      if (o instanceof RuntimeException) {
        throw (RuntimeException) o;
      } else {
        return (V) o;
      }
    }
  }
}

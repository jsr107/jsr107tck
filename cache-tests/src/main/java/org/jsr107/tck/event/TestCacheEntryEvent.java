/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.event;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;
import java.io.ObjectInputStream;

/**
 *
 * @param <K> key class
 * @param <V> value class
 */
public class TestCacheEntryEvent<K, V> extends CacheEntryEvent<K, V> {
  @Override
  public V getOldValue() {
    return oldValue;
  }

  @Override
  public boolean isOldValueAvailable() {
    return isOldValueAvailable;
  }

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
    throw new UnsupportedOperationException("not implemented");
  }

  public void setOldValue(V oldValue) {
    this.oldValue = oldValue;
    isOldValueAvailable = oldValue != null;
  }

  public void setKey(K key) {
    this.key = key;
  }


  public void setOldValueAvailable(boolean oldValueAvailable) {
    isOldValueAvailable = oldValueAvailable;
  }

  public void setValue(V value) {
    this.value = value;

  }

  private K key;
  private V value;
  private boolean isOldValueAvailable = false;
  private V oldValue;

  public TestCacheEntryEvent(Cache source, EventType type) {
    super(source, type);
    isOldValueAvailable = false;
    oldValue = null;
  }

  public CacheEntryEvent readObject(ObjectInputStream ois)  {
    try {
      key = (K) ois.readObject();
      value = (V) ois.readObject();

      // problem dealing with the next 2 fields of CacheEntryEvent.
      // comment out for now.
      // Before trying to add back, be sure to write these fields
      // in org.jsr107.tck.event.CacheEntryListenerClient.onInvoke
      // isOldValueAvailable = ois.readBoolean();
      // oldValue = isOldValueAvailable ? (V) ois.readObject() : null;
    } catch (Exception e) {
      e.printStackTrace();
    }
      return this;
  }
}

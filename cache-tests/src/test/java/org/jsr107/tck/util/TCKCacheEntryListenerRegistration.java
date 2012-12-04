package org.jsr107.tck.util;

import javax.cache.event.CacheEntryFilter;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerRegistration;

/**
 * The reference implementation of the {@link CacheEntryListenerRegistration}.
 * 
 * @author Brian Oliver
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class TCKCacheEntryListenerRegistration<K, V> implements CacheEntryListenerRegistration<K, V> {

    private CacheEntryListener<K, V> listener;
    private CacheEntryFilter<K, V> filter;
    private boolean isOldValueRequired;
    private boolean isSynchronous;
    
    /**
     * Constructs an {@link TCKCacheEntryListenerRegistration}.
     * 
     * @param listener            the {@link CacheEntryListener}
     * @param filter              the optional {@link CacheEntryFilter}
     * @param isOldValueRequired  if the old value is required for events with this listener
     * @param isSynchronous       if the listener should block the thread causing the event
     */
    public TCKCacheEntryListenerRegistration(CacheEntryListener<K, V> listener, 
                                             CacheEntryFilter<K, V> filter, 
                                             boolean isOldValueRequired, 
                                             boolean isSynchronous) {
        this.listener = listener;
        this.filter = filter;
        this.isOldValueRequired = isOldValueRequired;
        this.isSynchronous = isSynchronous;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CacheEntryFilter<K, V> getCacheEntryFilter() {
        return filter;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CacheEntryListener<K, V> getCacheEntryListener() {
        return listener;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOldValueRequired() {
        return isOldValueRequired;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSynchronous() {
        return isSynchronous;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((filter == null) ? 0 : filter.hashCode());
        result = prime * result + (isOldValueRequired ? 1231 : 1237);
        result = prime * result + (isSynchronous ? 1231 : 1237);
        result = prime * result
                + ((listener == null) ? 0 : listener.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TCKCacheEntryListenerRegistration)) {
            return false;
        }
        TCKCacheEntryListenerRegistration other = (TCKCacheEntryListenerRegistration) obj;
        if (filter == null) {
            if (other.filter != null) {
                return false;
            }
        } else if (!filter.equals(other.filter)) {
            return false;
        }
        if (isOldValueRequired != other.isOldValueRequired) {
            return false;
        }
        if (isSynchronous != other.isSynchronous) {
            return false;
        }
        if (listener == null) {
            if (other.listener != null) {
                return false;
            }
        } else if (!listener.equals(other.listener)) {
            return false;
        }
        return true;
    }
}

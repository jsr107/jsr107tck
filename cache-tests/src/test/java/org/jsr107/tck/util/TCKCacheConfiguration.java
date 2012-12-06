package org.jsr107.tck.util;

import java.util.ArrayList;
import javax.cache.Cache;
import javax.cache.CacheConfiguration;
import javax.cache.CacheEntryExpiryPolicy;
import javax.cache.CacheLoader;
import javax.cache.CacheWriter;
import javax.cache.InvalidConfigurationException;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerRegistration;
import javax.cache.transaction.IsolationLevel;
import javax.cache.transaction.Mode;

/**
 * A {@link CacheConfiguration} implementation for the TCK.
 * 
 * @param <K> the type of keys maintained the cache
 * @param <V> the type of cached values
 * 
 * @author Brian Oliver
 * @since 1.0
 */
public class TCKCacheConfiguration<K, V> implements CacheConfiguration<K, V> {

    private static final boolean DEFAULT_IS_READ_THROUGH = false;
    private static final boolean DEFAULT_IS_WRITE_THROUGH = false;
    private static final boolean DEFAULT_IS_STATISTICS_ENABLED = false;
    private static final boolean DEFAULT_STORE_BY_VALUE = true;
    private static final IsolationLevel DEFAULT_TRANSACTION_ISOLATION_LEVEL = IsolationLevel.NONE;
    private static final Mode DEFAULT_TRANSACTION_MODE = Mode.NONE;

    /**
     * The {@link CacheEntryListenerRegistration}s for a {@link CacheConfiguration}.
     */
    protected ArrayList<CacheEntryListenerRegistration<? super K, ? super V>> cacheEntryListenerRegistrations;

    /**
     * The {@link CacheLoader} for the built {@link CacheConfiguration}.
     */
    protected CacheLoader<K, ? extends V> cacheLoader;
    
    /**
     * The {@link CacheWriter} for the build {@link CacheConfiguration}.
     */
    protected CacheWriter<? super K, ? super V> cacheWriter;

    /**
     * A flag indicating if "read-through" mode is required.
     */
    protected boolean isReadThrough = DEFAULT_IS_READ_THROUGH;
    
    /**
     * A flag indicating if "write-through" mode is required.
     */
    protected boolean isWriteThrough = DEFAULT_IS_WRITE_THROUGH;
    
    /**
     * A flag indicating if statistics gathering is enabled.
     */
    protected boolean isStatisticsEnabled = DEFAULT_IS_STATISTICS_ENABLED;

    /**
     * A flag indicating if the cache will be store-by-value or store-by-reference.
     */
    protected boolean isStoreByValue;

    /**
     * The {@link CacheEntryExpiryPolicy}.
     */
    protected CacheEntryExpiryPolicy<? super K, ? super V> cacheEntryExpiryPolicy;
    
    /**
     * The transaction {@link IsolationLevel}.
     */
    protected IsolationLevel txnIsolationLevel;

    /**
     * The transaction {@link Mode}.
     */
    protected Mode txnMode;
    
    /**
     * Constructs {@link TCKCacheConfiguration} using the
     * default {@link CacheConfiguration} options.
     */
    public TCKCacheConfiguration() {
        this.cacheEntryListenerRegistrations = new ArrayList<CacheEntryListenerRegistration<? super K, ? super V>>();
        this.cacheLoader = null;
        this.cacheWriter = null;
        this.cacheEntryExpiryPolicy = new CacheEntryExpiryPolicy.Default<K, V>();
        this.isReadThrough = DEFAULT_IS_READ_THROUGH;
        this.isWriteThrough = DEFAULT_IS_WRITE_THROUGH;
        this.isStatisticsEnabled = DEFAULT_IS_STATISTICS_ENABLED;
        this.isStoreByValue = DEFAULT_STORE_BY_VALUE;
        this.txnIsolationLevel = DEFAULT_TRANSACTION_ISOLATION_LEVEL;
        this.txnMode = DEFAULT_TRANSACTION_MODE;
    }
    
    /**
     * Adds a {@link CacheEntryListener} that must be registered with a Cache
     * on startup.
     * 
     * @param listener
     * @param requireOldValue
     * @param filter
     * @param synchronous
     * 
     * @return a {@link TCKCacheConfiguration}
     */
    public TCKCacheConfiguration<K, V> addCacheEntryListener(CacheEntryListener<K, V> listener,
                                                             boolean requireOldValue, 
                                                             CacheEntryEventFilter<K, V> filter,
                                                             boolean synchronous) {
        
        TCKCacheEntryListenerRegistration<K, V> registration = 
                new TCKCacheEntryListenerRegistration<K, V>(listener,  filter, requireOldValue, synchronous);
        cacheEntryListenerRegistrations.add(registration);        
        return this;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<CacheEntryListenerRegistration<? super K, ? super V>> getCacheEntryListenerRegistrations() {
        return cacheEntryListenerRegistrations;
    }
    
    /**
     * Sets the {@link CacheLoader}.  
     * <p/>
     * When set to a non-null value, a {@link Cache} configured with this
     * a {@link CacheConfiguration} will support read-through semantics.  
     * When set to a null value, "read-through" won't be provided.
     *
     * @param cacheLoader the {@link CacheLoader}
     * @return the {@link TCKCacheConfiguration}
     */
    public TCKCacheConfiguration<K, V> setCacheLoader(CacheLoader<K, ? extends V> cacheLoader) {
        this.cacheLoader = cacheLoader;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CacheLoader<K, ? extends V> getCacheLoader() {
        return cacheLoader;
    }
    
    /**
     * Sets the {@link CacheWriter}.
     * <p/>
     * When set to a non-null value, a {@link Cache} configured with this
     * a {@link CacheConfiguration} will support write-through semantics.  
     * When set to a null value, "write-through" won't be provided.
     * 
     * @param cacheWriter the {@link CacheWriter}
     * @return the {@link TCKCacheConfiguration}
     */
    public TCKCacheConfiguration<K, V> setCacheWriter(CacheWriter<? super K, ? super V> cacheWriter) {
        this.cacheWriter = cacheWriter;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CacheWriter<? super K, ? super V> getCacheWriter() {
        return this.cacheWriter;
    }
    
    /**
     * Sets the {@link CacheEntryExpiryPolicy}.
     * 
     * @param policy the {@link CacheEntryExpiryPolicy}
     * @return the {@link TCKCacheConfiguration}
     * @throws NullPointerException policy is <code>null</code>
     */
    
    public TCKCacheConfiguration<K, V> setCacheEntryExpiryPolicy(CacheEntryExpiryPolicy<? super K, ? super V> policy) {
        if (policy == null) {
            throw new NullPointerException("policy can not be null");
        }
            
        this.cacheEntryExpiryPolicy = policy;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public CacheEntryExpiryPolicy<? super K, ? super V> getCacheEntryExpiryPolicy() {
        return cacheEntryExpiryPolicy;
    }
    
    /**
     * Sets if a {@link Cache} should operate in "read-through" mode.
     * 
     * @param isReadThrough will the {@link CacheConfiguration} establish "read-through" mode
     * @return the {@link TCKCacheConfiguration}
     * @see CacheConfiguration#isReadThrough()
     */
    public TCKCacheConfiguration<K, V> setReadThrough(boolean isReadThrough) {
        this.isReadThrough = isReadThrough;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadThrough() {
        return isReadThrough;
    }
    
    /**
     * Sets if a {@link Cache} should operate in "write-through" mode.
     * 
     * @param isWriteThrough will the {@link CacheConfiguration} establish "write-through" mode
     * @return the {@link TCKCacheConfiguration}
     * 
     * @see CacheConfiguration#isWriteThrough()
     */
    public TCKCacheConfiguration<K, V> setWriteThrough(boolean isWriteThrough) {
        this.isWriteThrough = isWriteThrough;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteThrough() {
        return isWriteThrough;
    }
    
    /**
     * Sets whether statistics gathering is enabled on this cache.
     *
     * @param isStatisticsEnabled true to enable statistics, false to disable
     * @return the {@link TCKCacheConfiguration}
     * @see CacheConfiguration#setStatisticsEnabled(boolean)
     */
    public void setStatisticsEnabled(boolean isStatisticsEnabled) {
        this.isStatisticsEnabled = isStatisticsEnabled;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStatisticsEnabled() {
        return isStatisticsEnabled;
    }
    
    /**
     * Sets whether the cache is store-by-value cache.
     *
     * @param isStoreByValue the value for storeByValue
     * @return the {@link TCKCacheConfiguration}
     * @throws InvalidConfigurationException if the cache does not support store by reference
     * @see CacheConfiguration#isStoreByValue()
     */
    public TCKCacheConfiguration<K, V> setStoreByValue(boolean isStoreByValue) {
        this.isStoreByValue = isStoreByValue;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStoreByValue() {
        return isStoreByValue;
    }
    
    /**
     * Sets whether transaction are enabled for this cache.
     *
     * @param isolationLevel - the isolation level for this cache
     * @param mode - the mode (Local or XA) for this cache
     * @return the {@link TCKCacheConfiguration}
     * @throws IllegalArgumentException if the cache does not support transactions,
     *            or an attempt is made to set the isolation level to 
     *            {@link IsolationLevel#NONE} or the mode to {@link Mode#NONE}.
     * @see CacheConfiguration#isTransactionEnabled()
     */
    public TCKCacheConfiguration<K, V> setTransactions(IsolationLevel isolationLevel, Mode mode) {
        this.txnIsolationLevel = isolationLevel;
        this.txnMode = mode;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public IsolationLevel getTransactionIsolationLevel() {
        return txnIsolationLevel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Mode getTransactionMode() {
        return txnMode;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTransactionEnabled() {
        return (txnIsolationLevel == null || txnIsolationLevel == IsolationLevel.NONE) &&
               (txnMode == null || txnMode == Mode.NONE);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((cacheLoader == null) ? 0 : cacheLoader.hashCode());
        result = prime * result
                + ((cacheWriter == null) ? 0 : cacheWriter.hashCode());
        result = prime * result
                + ((cacheEntryExpiryPolicy == null) ? 0 : cacheEntryExpiryPolicy.hashCode());
        result = prime * result + (isReadThrough ? 1231 : 1237);
        result = prime * result + (isStatisticsEnabled ? 1231 : 1237);
        result = prime * result + (isWriteThrough ? 1231 : 1237);
        result = prime * result + (isStoreByValue ? 1231 : 1237);
        result = prime
                * result
                + ((txnIsolationLevel == null) ? 0 : txnIsolationLevel
                        .hashCode());
        result = prime * result + ((txnMode == null) ? 0 : txnMode.hashCode());
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TCKCacheConfiguration<?, ?> other = (TCKCacheConfiguration<?, ?>) obj;
        if (cacheLoader == null) {
            if (other.cacheLoader != null)
                return false;
        } else if (!cacheLoader.equals(other.cacheLoader))
            return false;
        if (cacheWriter == null) {
            if (other.cacheWriter != null)
                return false;
        } else if (!cacheWriter.equals(other.cacheWriter))
            return false;
        if (cacheEntryExpiryPolicy == null) {
            if (other.cacheEntryExpiryPolicy != null)
                return false;
        } else if (!cacheEntryExpiryPolicy.equals(other.cacheEntryExpiryPolicy))
            return false;
        if (isReadThrough != other.isReadThrough)
            return false;
        if (isStatisticsEnabled != other.isStatisticsEnabled)
            return false;
        if (isWriteThrough != other.isWriteThrough)
            return false;
        if (isStoreByValue != other.isStoreByValue)
            return false;
        if (txnIsolationLevel != other.txnIsolationLevel)
            return false;
        if (txnMode != other.txnMode)
            return false;
        return true;
    }
}

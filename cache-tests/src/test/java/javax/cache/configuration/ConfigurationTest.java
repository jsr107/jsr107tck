/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.configuration;

import org.jsr107.tck.testutil.CacheTestSupport;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Functional tests for the {@link javax.cache.configuration.Configuration}
 * interface.
 *
 * @author Greg Luck
 */
public class ConfigurationTest extends CacheTestSupport {


  /**
   * Ensure that a {@link javax.cache.configuration.MutableConfiguration} correctly
   * uses the defaults.
   *
   * @param config
   */
  private void validateDefaults(CompleteConfiguration<?, ?> config) {
    assertEquals(Object.class, config.getKeyType());
    assertEquals(Object.class, config.getValueType());
    assertFalse(config.isReadThrough());
    assertFalse(config.isWriteThrough());
    assertTrue(config.isStoreByValue());
    assertFalse(config.isStatisticsEnabled());
    assertFalse(config.isManagementEnabled());
    assertTrue(getConfigurationCacheEntryListenerConfigurationSize(config) == 0);
    assertNull(config.getCacheLoaderFactory());
    assertNull(config.getCacheWriterFactory());

    //expiry policies
    ExpiryPolicy expiryPolicy = config.getExpiryPolicyFactory().create();
    assertTrue(expiryPolicy instanceof EternalExpiryPolicy);
    assertThat(Duration.ETERNAL, equalTo(expiryPolicy.getExpiryForCreation()));
    assertThat(expiryPolicy.getExpiryForAccess(), is(nullValue()));
    assertThat(expiryPolicy.getExpiryForUpdate(), is(nullValue()));
  }

  /**
   * Ensure that a {@link MutableConfiguration} correctly uses the defaults
   * from an implementation of the base Configuration interface.
   */
  @Test
  public void testValidateFromBasicConfigurationRetrievedFromCache() {
    Cache<String, Integer> cache = getCacheManager().createCache("basicCache", new MutableBasicConfiguration());
    CompleteConfiguration configuration = cache.getConfiguration(CompleteConfiguration.class);
    validateDefaults(configuration);

  }

  /**
   * Tests use of default constructor and generics.
   *
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @Test
  public void testDefaultConstructor() throws IllegalAccessException, InstantiationException {
    try {
      MutableConfiguration<String, String> configuration = new MutableConfiguration<String, String>();

      assertEquals(Object.class, configuration.getKeyType());
      assertEquals(Object.class, configuration.getValueType());

      Class<String> keyType = configuration.getKeyType();
      String s = keyType.newInstance();
    } catch (ClassCastException e) {
      //expected
    }
  }

  /**
   * Tests use of default constructor and generics, this time setting the desired type
   *
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @Test
  public void testDefaultConstructorWithSetTypes() throws IllegalAccessException, InstantiationException {
      MutableConfiguration<String, String> configuration = new MutableConfiguration<String, String>().setTypes(String.class, String.class);
      Class<String> keyType = configuration.getKeyType();
      String s = keyType.newInstance();
      //no exception. This sets the types.
  }

  /**
   * Tests use of default constructor and generics, this time setting the desired type using the constructor
   *
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @Test
  public void testDefaultConstructorWithTypeConstructor() throws IllegalAccessException, InstantiationException {
    MutableConfiguration<String, String> configuration = new MutableConfiguration<String, String>(String.class, String.class);
    Class<String> keyType = configuration.getKeyType();
    String s = keyType.newInstance();
    //no exception. This sets the types.
  }

  /**
   * Ensure a cache's config isn't changed by its configuration object after construction.
   */
  @Test
  public void testModifyingConfigurationAfterCreateCacheDoesNotModifyCacheConfiguration() {
    MutableBasicConfiguration mutableConfiguration = new MutableBasicConfiguration().setTypes(Integer.class, Integer.class);
    Cache<Object, Object> cache = getCacheManager().createCache(getTestCacheName() + "_", mutableConfiguration);
    mutableConfiguration.setTypes(String.class, String.class);
    assertEquals(Integer.class, cache.getConfiguration(Configuration.class).getKeyType());
    assertEquals(Integer.class, cache.getConfiguration(Configuration.class).getValueType());
  }


    @Override
  protected MutableConfiguration newMutableConfiguration() {
    return new MutableConfiguration();
  }

  public static class MutableBasicConfiguration<K, V> implements Configuration<K, V> {


    /**
     * Construct using defaults
     */
    public MutableBasicConfiguration() {
      this.keyType = (Class<K>) Object.class;
      this.valueType = (Class<V>) Object.class;
      this.isStoreByValue = true;
    }

    /**
     * The serialVersionUID required for {@link java.io.Serializable}.
     */
    public static final long serialVersionUID = 201311111637L;

    /**
     * The type of keys for {@link Cache}s configured with this
     * {@link Configuration}.
     */
    protected Class<K> keyType;

    /**
     * The type of values for {@link Cache}s configured with this
     * {@link Configuration}.
     */
    protected Class<V> valueType;


    /**
     * A flag indicating if the cache will be store-by-value or
     * store-by-reference.
     */
    protected boolean isStoreByValue;


    /**
     * {@inheritDoc}
     */
    @Override
    public Class<K> getKeyType() {
      return keyType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<V> getValueType() {
      return valueType;
    }

    /**
     * Sets the expected type of keys and values for a {@link Cache}
     * configured with this {@link Configuration}. Setting both to
     * <code>Object.class</code> means type-safety checks are not required.
     * <p>
     * This is used by {@link javax.cache.CacheManager} to ensure that the key and
     * value types are the same as those configured for the {@link Cache} prior to
     * returning a requested cache from this method.
     * </p>
     * Implementations may further perform type checking on mutative cache
     * operations and throw a {@link ClassCastException} if these checks fail.
     *
     * @param keyType   the expected key type
     * @param valueType the expected value type
     * @return the {@link MutableConfiguration} to permit fluent-style method calls
     * @throws NullPointerException should the key or value type be null
     * @see javax.cache.CacheManager#getCache(String, Class, Class)
     */
    public MutableBasicConfiguration<K, V> setTypes(Class<K> keyType, Class<V> valueType) {
      if (keyType == null || valueType == null) {
        throw new NullPointerException("keyType and/or valueType can't be null");
      } else {
        this.keyType = keyType;
        this.valueType = valueType;
        return this;
      }
    }


    /**
     * Set if a configured cache should use store-by-value or store-by-reference
     * semantics.
     *
     * @param isStoreByValue <code>true</code> if store-by-value is required,
     *                       <code>false</code> for store-by-reference
     * @return the {@link MutableConfiguration} to permit fluent-style method
     *         calls
     */
    public MutableBasicConfiguration<K, V> setStoreByValue(boolean isStoreByValue) {
      this.isStoreByValue = isStoreByValue;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStoreByValue() {
      return this.isStoreByValue;
    }


  }

  private int getConfigurationCacheEntryListenerConfigurationSize(CompleteConfiguration config) {
    int i = 0;
    for (Object listenerConfig : config.getCacheEntryListenerConfigurations()) {
      i++;
    }
    return i;
  }

}

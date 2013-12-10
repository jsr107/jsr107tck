/**
 *  Copyright 2012 Terracotta, Inc.
 *  Copyright 2012 Oracle, Inc.
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

package javax.cache.configuration;

import org.jsr107.tck.integration.FailingCacheLoader;
import org.jsr107.tck.integration.FailingCacheWriter;
import org.jsr107.tck.integration.NullValueCacheLoader;
import org.jsr107.tck.integration.RecordingCacheWriter;
import org.jsr107.tck.testutil.CacheTestSupport;
import org.junit.Assert;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Functional tests for the {@link MutableConfiguration} class.
 *
 * @author Brian Oliver
 * @author Greg Luck
 */
public class MutableConfigurationTest extends CacheTestSupport {

  /**
   * Ensure that a {@link MutableConfiguration} correctly uses the defaults.
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
    List<? extends CacheEntryListenerConfiguration<?,?>> cacheEntryListenerConfigurations = config.getCacheEntryListenerConfigurations();
    assertTrue(cacheEntryListenerConfigurations == null ||
        cacheEntryListenerConfigurations.size() == 0);
    assertNull(config.getCacheLoaderFactory());
    assertNull(config.getCacheWriterFactory());

    // code coverage
    config.hashCode();

    //expiry policies
    ExpiryPolicy expiryPolicy = config.getExpiryPolicyFactory().create();
    assertTrue(expiryPolicy instanceof EternalExpiryPolicy);
    assertThat(Duration.ETERNAL, equalTo(expiryPolicy.getExpiryForCreation()));
    assertThat(expiryPolicy.getExpiryForAccess(), is(nullValue()));
    assertThat(expiryPolicy.getExpiryForUpdate(), is(nullValue()));
  }

  /**
   * Ensure that a {@link MutableConfiguration} correctly uses the defaults.
   */
  @Test
  public void testDefaultCacheFromCacheManagerUsesCorrectDefaults() {
    Cache<Object, Object> cache = getCacheManager().getCache(getTestCacheName());
    //get a basic configuration from the Cache
    CompleteConfiguration configuration = cache.getConfiguration(CompleteConfiguration.class);
    validateDefaults(configuration);

  }

  /**
   * Ensure a cache's config isn't changed by its configuration object after construction.
   */
  @Test
  public void testModifyingConfigurationAfterCreateCacheDoesNotModifyCacheConfiguration() {
    MutableConfiguration mutableConfiguration = new MutableConfiguration().setTypes(Integer.class, Integer.class);
    Cache<Object, Object> cache = getCacheManager().createCache(getTestCacheName() + "_", mutableConfiguration);
    mutableConfiguration.setTypes(String.class, String.class);
    assertEquals(Integer.class, cache.getConfiguration(CompleteConfiguration.class).getKeyType());
    assertEquals(Integer.class, cache.getConfiguration(CompleteConfiguration.class).getValueType());
  }

  @Test
  public void testNewMutableConfigurationUsesCorrectDefaults() {

    Configuration<?, ?> config = new MutableConfiguration();
    validateDefaults((CompleteConfiguration)config);
  }

  /**
   * Ensure that two {@link MutableConfiguration}s are equal.
   */
  @Test
  public void shouldBeEqualWhenUsingDefaults() {
    Configuration config1 = new MutableConfiguration();
    Configuration config2 = new MutableConfiguration();
    assertEquals(config1, config2);
  }

  /**
   * Ensure that two {@link MutableConfiguration}s, one specifying types
   * as Object and another not specifying types should be equal.
   */
  @Test
  public void shouldBeEqualWhenUsingTypedAndUntypedConfigurationsWithObject() {
    Configuration config1 = new MutableConfiguration();
    Configuration config2 = new MutableConfiguration<Object, Object>()
        .setTypes(Object.class, Object.class);

    assertThat(config1.equals(config2), is(true));
  }

  /**
   * Ensure that multiple {@link MutableConfiguration}s are equal but not
   * the same, including those that are cloned.
   */
  @Test
  public void shouldNotBeTheSameButAClone() {
    Configuration config1 = new MutableConfiguration(new MutableConfiguration());
    Configuration config2 = new MutableConfiguration();
    assertNotSame(config1, config2);
    assertEquals(config1, config2);
  }

  /**
   * Ensure that multiple {@link MutableConfiguration}s are equal but not
   * the same.
   */
  @Test
  public void shouldNotBeTheSame() {
    Configuration config1 = new MutableConfiguration();
    Configuration config2 = new MutableConfiguration();
    assertNotSame(config1, config2);
  }

  /**
   * Ensure that multiple {@link MutableConfiguration}s are equal.
   */
  @Test
  public void shouldEqual() {
    Configuration config1 = new MutableConfiguration();
    Configuration config2 = new MutableConfiguration();
    assertEquals(config1, config2);
  }


  @Test
  public void testCopyConstructor() {
    CompleteConfiguration<String, String> copyConfig =
      new MutableConfiguration<String, String>() {

        @Override
        public Factory<ExpiryPolicy> getExpiryPolicyFactory() {
          return null;
        }
      };
    // code coverage of expiry policy factory as null.
    copyConfig.hashCode();

    Assert.assertNull(copyConfig.getExpiryPolicyFactory());
    MutableConfiguration<String, String> config =
      new MutableConfiguration<String, String>(copyConfig);
    assertNotNull(config.getExpiryPolicyFactory());
  }

  @Test
  public void testSetAttributeToNull() {
    MutableConfiguration<String, String> config = new MutableConfiguration<String, String>();
    try {
      config.setTypes(null, String.class);
      fail("null poiner exception expected");
    } catch (NullPointerException e) {
      // ignore this expected exception.
    }
    try {
      config.setTypes(String.class, null);
      fail("null poiner exception expected");
    } catch (NullPointerException e) {
      // ignore this expected exception.
    }
    try {
      config.addCacheEntryListenerConfiguration(null);
      fail("null poiner exception expected");
    } catch (NullPointerException e) {
      // ignore this expected exception.
    }
    try {
      config.setExpiryPolicyFactory(null);
      assertNotNull(config.getExpiryPolicyFactory());
    } catch (NullPointerException e) {
      fail("should not throw NullPointerException when setting ExpiryPolicyFactory to null");
    }
  }

  @Test
  public void testEqualsHashCode() {
    MutableConfiguration<String, String> config1 = new MutableConfiguration<String, String>();
    config1.setTypes(String.class, String.class);
    config1.hashCode();

    // code coverage testing of equals.
    assertTrue(config1.equals(config1));

    assertFalse(config1.equals(null));

    assertFalse(config1.equals("NonMutableConfigurationCompareForCodeCoverage"));

    MutableConfiguration<Integer, String> config2 = new MutableConfiguration<Integer, String>();
    config2.setTypes(Integer.class, String.class);
    assertFalse(config1.equals(config2));


    MutableConfiguration<String, Integer> config3 = new MutableConfiguration<String, Integer>();
    config3.setTypes(String.class, Integer.class);
    assertFalse(config1.equals(config3));

    MutableConfiguration<String, String> differentConfig1 = new MutableConfiguration<>(config1);
    differentConfig1.setStatisticsEnabled(! config1.isStatisticsEnabled());
    differentConfig1.hashCode();
    config1.hashCode();
    assertFalse(config1.equals(differentConfig1));

    differentConfig1 = new MutableConfiguration<>(config1);
    differentConfig1.setStoreByValue(! config1.isStoreByValue());
    differentConfig1.hashCode();
    assertFalse(config1.equals(differentConfig1));

    differentConfig1 = new MutableConfiguration<>(config1);
    differentConfig1.setReadThrough(! config1.isReadThrough());
    differentConfig1.hashCode();
    assertFalse(config1.equals(differentConfig1));

    differentConfig1 = new MutableConfiguration<>(config1);
    differentConfig1.setWriteThrough(!config1.isWriteThrough());
    differentConfig1.hashCode();
    assertFalse(config1.equals(differentConfig1));

    differentConfig1 = new MutableConfiguration<>(config1);
    differentConfig1.setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.MILLISECONDS, 20)));
    assertFalse(config1.equals(differentConfig1));

    MutableConfiguration rawConfig1 = new MutableConfiguration();
    MutableConfiguration rawConfig2 = new MutableConfiguration(rawConfig1);
    rawConfig2.setCacheLoaderFactory(FactoryBuilder.factoryOf(NullValueCacheLoader.class));
    assertFalse(rawConfig1.equals(rawConfig2));
    assertFalse(rawConfig2.equals(rawConfig1));
    rawConfig1.setCacheLoaderFactory(rawConfig2.getCacheLoaderFactory());
    rawConfig1.hashCode();
    assertEquals(rawConfig1, rawConfig2);

    rawConfig2.setCacheLoaderFactory(FactoryBuilder.factoryOf(FailingCacheLoader.class));
    assertFalse(rawConfig2.equals(rawConfig1));
    assertFalse(rawConfig1.equals(rawConfig2));

    rawConfig1 = new MutableConfiguration();
    rawConfig2 = new MutableConfiguration(rawConfig1);
    rawConfig2.setCacheWriterFactory(FactoryBuilder.factoryOf(RecordingCacheWriter.class));
    rawConfig2.hashCode();
    assertFalse(rawConfig1.equals(rawConfig2));
    assertFalse(rawConfig2.equals(rawConfig1));
    rawConfig1.setCacheWriterFactory(rawConfig2.getCacheWriterFactory());
    assertEquals(rawConfig1, rawConfig2);

    rawConfig2.setCacheWriterFactory(FactoryBuilder.factoryOf(FailingCacheWriter.class));
    rawConfig2.hashCode();
    assertFalse(rawConfig2.equals(rawConfig1));
    assertFalse(rawConfig1.equals(rawConfig2));

    // for code coverage in equals
    rawConfig1 = new MutableConfiguration<String, String>() {
      @Override
      public MutableConfiguration<String, String> setExpiryPolicyFactory(Factory<? extends 	ExpiryPolicy> factory) {
        this.expiryPolicyFactory = null;
        return this;
      }
    };
    rawConfig2 = new MutableConfiguration(rawConfig1);
    rawConfig1.setExpiryPolicyFactory(null);
    rawConfig1.hashCode();
    assertFalse(rawConfig1.equals(rawConfig2));

    rawConfig2 = new MutableConfiguration<String, String>() {
      @Override
      public MutableConfiguration<String, String> setExpiryPolicyFactory(Factory<? extends 	ExpiryPolicy> factory) {
        this.expiryPolicyFactory = null;
        return this;
      }
    };
    rawConfig2.setExpiryPolicyFactory(null);
    assertTrue(rawConfig1.equals(rawConfig2));


    MutableCacheEntryListenerConfiguration<String, String> listenerConfig1 =
      new MutableCacheEntryListenerConfiguration<String, String>(null, null, false, false);
    MutableCacheEntryListenerConfiguration<String, String> listenerConfig2 =
      new MutableCacheEntryListenerConfiguration<String, String>(null, null, true, false);


    rawConfig1 = new MutableConfiguration();
    rawConfig2 = new MutableConfiguration(rawConfig1);
    rawConfig1.hashCode();
    rawConfig2.addCacheEntryListenerConfiguration(new MutableCacheEntryListenerConfiguration(listenerConfig2));
    rawConfig2.hashCode();
    assertFalse(rawConfig1.equals(rawConfig2));
    assertFalse(rawConfig2.equals(rawConfig1));
    rawConfig1.addCacheEntryListenerConfiguration(listenerConfig1);
    assertFalse(rawConfig1.equals(rawConfig2));
    assertFalse(rawConfig2.equals(rawConfig1));
    MutableConfiguration rawConfig3 = new MutableConfiguration(rawConfig1);
    assertTrue(rawConfig3.equals(rawConfig1));
  }

  @Override
  protected MutableConfiguration newMutableConfiguration() {
    return new MutableConfiguration();
  }

}

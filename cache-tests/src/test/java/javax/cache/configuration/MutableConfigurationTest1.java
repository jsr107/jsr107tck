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

import org.jsr107.tck.testutil.CacheTestSupport;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Functional tests for the {@link MutableConfiguration} class.
 *
 * @author Brian Oliver
 * @author Greg Luck
 */
public class MutableConfigurationTest1 extends CacheTestSupport {

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

  @Override
  protected MutableConfiguration newMutableConfiguration() {
    return new MutableConfiguration();
  }
}

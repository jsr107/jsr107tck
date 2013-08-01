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

import org.junit.Test;

import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import java.util.concurrent.TimeUnit;

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
 */
public class MutableConfigurationTest {

  /**
   * Ensure that a {@link MutableConfiguration} correctly uses the defaults.
   */
  @Test
  public void shouldUseDefaults() {
    Configuration<?, ?> config = new MutableConfiguration<>();
    assertFalse(config.isReadThrough());
    assertFalse(config.isWriteThrough());
    assertFalse(config.isStatisticsEnabled());
    assertTrue(config.isStoreByValue());

    ExpiryPolicy<?, ?> expiryPolicy = config.getExpiryPolicyFactory().create();

    Duration duration = new Duration(TimeUnit.MINUTES, 10);

    assertThat(Duration.ETERNAL, equalTo(expiryPolicy.getExpiryForCreatedEntry(null)));
    assertThat(expiryPolicy.getExpiryForAccessedEntry(null), is(nullValue()));
    assertThat(expiryPolicy.getExpiryForModifiedEntry(null), is(nullValue()));
  }

  /**
   * Ensure that two {@link MutableConfiguration}s are equal.
   */
  @Test
  public void shouldBeEqualWhenUsingDefaults() {
    Configuration<?, ?> config1 = new MutableConfiguration<>();
    Configuration<?, ?> config2 = new MutableConfiguration<>();
    assertEquals(config1, config2);
  }

  /**
   * Ensure that two {@link MutableConfiguration}s, one specifying types and
   * another not specifying types should not be equal.
   */
  @Test
  public void shouldNotBeEqualWhenUsingTypedAndUntypedConfigurations() {
    Configuration<?, ?> config1 = new MutableConfiguration<>();
    Configuration<?, ?> config2 = new MutableConfiguration<Object, Object>()
        .setTypes(Object.class, Object.class);

    assertThat(config1.equals(config2), is(false));
  }

  /**
   * Ensure that multiple {@link MutableConfiguration}s are equal but not
   * the same, including those that are cloned.
   */
  @Test
  public void shouldNotBeTheSameButAClone() {
    Configuration<?, ?> config1 = new MutableConfiguration<>(new MutableConfiguration<>());
    Configuration<?, ?> config2 = new MutableConfiguration<>();
    assertNotSame(config1, config2);
    assertEquals(config1, config2);
  }

  /**
   * Ensure that multiple {@link MutableConfiguration}s are equal but not
   * the same.
   */
  @Test
  public void shouldNotBeTheSame() {
    Configuration<?, ?> config1 = new MutableConfiguration<>();
    Configuration<?, ?> config2 = new MutableConfiguration<>();
    assertNotSame(config1, config2);
  }

  /**
   * Ensure that multiple {@link MutableConfiguration}s are equal.
   */
  @Test
  public void shouldEqual() {
    Configuration<?, ?> config1 = new MutableConfiguration<>();
    Configuration<?, ?> config2 = new MutableConfiguration<>();
    assertEquals(config1, config2);
  }
}

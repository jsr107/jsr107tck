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

package org.jsr107.tck;

import org.junit.Test;

import javax.cache.Configuration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.MutableConfiguration;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for a {@link javax.cache.Configuration}.
 *
 * @author Brian Oliver
 * @since 1.0
 */
public class TCKConfigurationTest {

  /**
   * Obtains the {@link javax.cache.Configuration} implementation to use for testing
   *
   * @return a new {@link javax.cache.Configuration} instance
   */
  public <K, V> Configuration<K, V> getConfiguration() {
    return new MutableConfiguration<K, V>();
  }

  @Test
  public void checkDefaults() {
    Configuration<?, ?> config = getConfiguration();
    assertFalse(config.isReadThrough());
    assertFalse(config.isWriteThrough());
    assertFalse(config.isStatisticsEnabled());
    assertTrue(config.isStoreByValue());

    ExpiryPolicy<?, ?> expiryPolicy = config.getExpiryPolicyFactory().create();

    Duration duration = new Duration(TimeUnit.MINUTES, 10);
    assertEquals(Duration.ETERNAL, expiryPolicy.getExpiryForCreatedEntry(null));
    assertNull(expiryPolicy.getExpiryForAccessedEntry(null));
    assertNull(expiryPolicy.getExpiryForModifiedEntry(null));
  }

  @Test
  public void notSame() {
    Configuration<?, ?> config1 = getConfiguration();
    Configuration<?, ?> config2 = getConfiguration();
    assertNotSame(config1, config2);
  }

  @Test
  public void equals() {
    Configuration<?, ?> config1 = getConfiguration();
    Configuration<?, ?> config2 = getConfiguration();
    assertEquals(config1, config2);
  }

  @Test
  public void notEquals() {
    Configuration<?, ?> config1 = getConfiguration();
    Configuration<?, ?> config2 = new MutableConfiguration<Object, Object>();
    ((MutableConfiguration) config2).setStatisticsEnabled(true);
    assertFalse(config1.equals(config2));
  }

  @Test
  public void DurationEquals() {
    Duration duration1 = new Duration(TimeUnit.DAYS, 2);
    Duration duration2 = new Duration(TimeUnit.DAYS, 2);
    assertEquals(duration1, duration2);
  }


  @Test
  public void durationNotEqualsAmount() {
    Duration duration1 = new Duration(TimeUnit.DAYS, 2);
    Duration duration2 = new Duration(TimeUnit.DAYS, 3);
    assertFalse(duration1.equals(duration2));
    assertFalse(duration1.hashCode() == duration2.hashCode());
  }

  @Test
  public void durationNotEqualsUnit() {
    Duration duration1 = new Duration(TimeUnit.DAYS, 2);
    Duration duration2 = new Duration(TimeUnit.MINUTES, 2);
    assertFalse(duration1.equals(duration2));
    assertFalse(duration1.hashCode() == duration2.hashCode());

  }

  /**
   * Checks that equals() is semantically meaningful.
   * <p/>
   * Also verifies the second requirement in the contract of hashcode:
   * * <li>If two objects are equal according to the <tt>equals(Object)</tt>
   * method, then calling the <code>hashCode</code> method on each of
   * the two objects must produce the same integer result.
   */
  @Test
  public void durationEqualsWhenSemanticallyEqualsButExpressedDifferentUnits() {
    Duration duration1 = new Duration(TimeUnit.SECONDS, 120);
    Duration duration2 = new Duration(TimeUnit.MINUTES, 2);
    assertEquals(duration1, duration2);
    assertEquals(duration1.hashCode(), duration2.hashCode());
  }

  @Test
  public void durationEqualsWhenSemanticallyEqualsButExpressedDifferentUnitsHashCode() {
    Duration duration1 = new Duration(TimeUnit.SECONDS, 120);
    Duration duration2 = new Duration(TimeUnit.MINUTES, 2);
    assertEquals(duration1, duration2);
    assertEquals(duration1.hashCode(), duration2.hashCode());
  }


  @Test
  public void durationNotEqualsUnitEquals() {
    long time = 2;
    Duration duration1 = new Duration(TimeUnit.HOURS, 2);
    time *= 60;
    Duration duration2 = new Duration(TimeUnit.MINUTES, 120);
    assertEquals(duration1, duration2);
    time *= 60;
    duration2 = new Duration(TimeUnit.SECONDS, time);
    assertEquals(duration1, duration2);
    time *= 1000;
    duration2 = new Duration(TimeUnit.MILLISECONDS, time);
    assertEquals(duration1, duration2);
  }


  @Test
  public void DurationExceptions() {
    try {
      new Duration(null, 2);
    } catch (NullPointerException e) {
      //expected
    }

    try {
      new Duration(TimeUnit.MINUTES, 0);
    } catch (NullPointerException e) {
      //expected
    }


    try {
      new Duration(TimeUnit.MICROSECONDS, 10);
    } catch (IllegalArgumentException e) {
      //expected
    }

    try {
      new Duration(TimeUnit.MILLISECONDS, -10);
    } catch (IllegalArgumentException e) {
      //expected
    }
  }
}

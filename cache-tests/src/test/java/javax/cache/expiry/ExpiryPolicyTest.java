/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.expiry;

import org.jsr107.tck.testutil.TestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import java.util.concurrent.TimeUnit;

import static javax.cache.expiry.Duration.ETERNAL;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the policy classes shipped with the API.
 * @author Greg Luck
 */
public class ExpiryPolicyTest extends TestSupport {

  @Test
  public void testCreatedExpiryPolicy() {

    ExpiryPolicy policy = CreatedExpiryPolicy.factoryOf( new Duration(TimeUnit.MILLISECONDS, 20)).create();
    CreatedExpiryPolicy policy2 = new CreatedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 20));
    CreatedExpiryPolicy policy3 = new CreatedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 10));
    assertEquals(policy, policy2);
    assertNotEquals(policy, policy3);
    assertEquals(policy.hashCode(), policy2.hashCode());
    assertNotEquals(policy.hashCode(), policy3.hashCode());

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(policy2)).setStatisticsEnabled(true);
    Cache<Integer, Integer> cache = cacheManager.createCache(getTestCacheName(), config);

    assertEquals(20, policy.getExpiryForCreation().getDurationAmount());
    assertNull(policy.getExpiryForAccess());
    assertNull(policy.getExpiryForUpdate());
  }

  @Before
  public void setup()
      {
      cacheManager = Caching.getCachingProvider().getCacheManager();
      }

  @After
  public void cleanupAfterEachTest() throws InterruptedException {
    for (String cacheName : cacheManager.getCacheNames()) {
      cacheManager.destroyCache(cacheName);
    }
    cacheManager.close();
    cacheManager = null;
  }

  @Test
  public void testModifiedExpiryPolicy() {

    ModifiedExpiryPolicy policy = new ModifiedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 20));
    ModifiedExpiryPolicy policy2 = new ModifiedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 20));
    ModifiedExpiryPolicy policy3 = new ModifiedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 10));
    assertEquals(policy, policy2);
    assertNotEquals(policy, policy3);
    assertEquals(policy.hashCode(), policy2.hashCode());
    assertNotEquals(policy.hashCode(), policy3.hashCode());

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(policy)).setStatisticsEnabled(true);
    Cache<Integer, Integer> cache = cacheManager.createCache(getTestCacheName(), config);

    assertEquals(20, policy.getExpiryForCreation().getDurationAmount());
    assertNull(policy.getExpiryForAccess());
    assertEquals(20, policy.getExpiryForUpdate().getDurationAmount());
  }

  @Test
  public void testAccessedExpiryPolicy() {

    AccessedExpiryPolicy policy = new AccessedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 20));
    AccessedExpiryPolicy policy2 = new AccessedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 20));
    AccessedExpiryPolicy policy3 = new AccessedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 10));
    assertEquals(policy, policy2);
    assertNotEquals(policy, policy3);
    assertEquals(policy.hashCode(), policy2.hashCode());
    assertNotEquals(policy.hashCode(), policy3.hashCode());

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(policy)).setStatisticsEnabled(true);
    Cache<Integer, Integer> cache = cacheManager.createCache(getTestCacheName(), config);

    assertEquals(20, policy.getExpiryForCreation().getDurationAmount());
    assertEquals(20, policy.getExpiryForAccess().getDurationAmount());
    assertNull(policy.getExpiryForUpdate());
  }

  @Test
  public void testTouchedExpiryPolicy() {

    TouchedExpiryPolicy policy = new TouchedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 20));
    TouchedExpiryPolicy policy2 = new TouchedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 20));
    TouchedExpiryPolicy policy3 = new TouchedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 10));
    assertEquals(policy, policy2);
    assertNotEquals(policy, policy3);
    assertEquals(policy.hashCode(), policy2.hashCode());
    assertNotEquals(policy.hashCode(), policy3.hashCode());

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(policy)).setStatisticsEnabled(true);
    Cache<Integer, Integer> cache = cacheManager.createCache(getTestCacheName(), config);

    //any operation adds the duration onto the expiry
    assertEquals(20, policy.getExpiryForCreation().getDurationAmount());
    assertEquals(20, policy.getExpiryForAccess().getDurationAmount());
    assertEquals(20, policy.getExpiryForUpdate().getDurationAmount());
  }

  @Test
  public void testEternalExpiryPolicy() {

    EternalExpiryPolicy policy = new EternalExpiryPolicy();
    EternalExpiryPolicy policy2 = new EternalExpiryPolicy();
    assertEquals(policy, policy2);
    assertEquals(policy.hashCode(), policy2.hashCode());

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(policy)).setStatisticsEnabled(true);
    Cache<Integer, Integer> cache = cacheManager.createCache(getTestCacheName(), config);

    assertEquals(ETERNAL, policy.getExpiryForCreation());
    assertNull(policy.getExpiryForAccess());
    assertNull(policy.getExpiryForUpdate());
  }

  @Test
  public void testEqualsForCreatedExpiryPolicy() {

    // added for code coverage
    ExpiryPolicy policy = CreatedExpiryPolicy.factoryOf( new Duration(TimeUnit.MILLISECONDS, 20)).create();
    assertTrue(policy.equals(policy));

    assertFalse(policy.equals(null));

    assertFalse(policy.equals("noMatchWrongClass"));
    ExpiryPolicy nullDurationPolicy = CreatedExpiryPolicy.factoryOf(null).create();
    nullDurationPolicy.hashCode();
    assertFalse(nullDurationPolicy.equals(policy));

    ExpiryPolicy nullDurationPolicy1 = CreatedExpiryPolicy.factoryOf(null).create();
    assertTrue(nullDurationPolicy.equals(nullDurationPolicy1));
  }

  @Test
  public void testEqualsForAccessedExpiryPolicy() {

    // added for code coverage
    ExpiryPolicy policy = AccessedExpiryPolicy.factoryOf( new Duration(TimeUnit.MILLISECONDS, 20)).create();
    assertTrue(policy.equals(policy));

    assertFalse(policy.equals(null));

    assertFalse(policy.equals("noMatchWrongClass"));
    ExpiryPolicy nullDurationPolicy = AccessedExpiryPolicy.factoryOf(null).create();
    nullDurationPolicy.hashCode();
    assertFalse(nullDurationPolicy.equals(policy));

    ExpiryPolicy nullDurationPolicy1 = AccessedExpiryPolicy.factoryOf(null).create();
    assertTrue(nullDurationPolicy.equals(nullDurationPolicy1));
  }

  @Test
  public void testEqualsForModifiedExpiryPolicy() {

    // added for code coverage
    ExpiryPolicy policy = ModifiedExpiryPolicy.factoryOf( new Duration(TimeUnit.MILLISECONDS, 20)).create();
    assertTrue(policy.equals(policy));

    assertFalse(policy.equals(null));

    assertFalse(policy.equals("noMatchWrongClass"));
    ExpiryPolicy nullDurationPolicy = ModifiedExpiryPolicy.factoryOf(null).create();
    nullDurationPolicy.hashCode();
    assertFalse(nullDurationPolicy.equals(policy));

    ExpiryPolicy nullDurationPolicy1 = ModifiedExpiryPolicy.factoryOf(null).create();
    assertTrue(nullDurationPolicy.equals(nullDurationPolicy1));
  }
  @Test
  public void testEqualsForTouchedExpiryPolicy() {

    // added for code coverage
    ExpiryPolicy policy = TouchedExpiryPolicy.factoryOf( new Duration(TimeUnit.MILLISECONDS, 20)).create();
    assertTrue(policy.equals(policy));

    assertFalse(policy.equals(null));

    assertFalse(policy.equals("noMatchWrongClass"));
    ExpiryPolicy nullDurationPolicy = TouchedExpiryPolicy.factoryOf(null).create();
    nullDurationPolicy.hashCode();
    assertFalse(nullDurationPolicy.equals(policy));

    ExpiryPolicy nullDurationPolicy1 = TouchedExpiryPolicy.factoryOf(null).create();
    assertTrue(nullDurationPolicy.equals(nullDurationPolicy1));
  }

  private CacheManager cacheManager;
}

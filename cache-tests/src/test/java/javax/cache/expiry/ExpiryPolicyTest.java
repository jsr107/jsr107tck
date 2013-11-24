package javax.cache.expiry;

import org.junit.Test;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import java.util.concurrent.TimeUnit;

import static javax.cache.expiry.Duration.ETERNAL;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests the policy classes shipped with the API.
 * @author Greg Luck
 */
public class ExpiryPolicyTest {

  @Test
  public void testCreatedExpiryPolicy() {

    CreatedExpiryPolicy policy = new CreatedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 20));
    CreatedExpiryPolicy policy2 = new CreatedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 20));
    CreatedExpiryPolicy policy3 = new CreatedExpiryPolicy(new Duration(TimeUnit.MILLISECONDS, 10));
    assertEquals(policy, policy2);
    assertNotEquals(policy, policy3);
    assertEquals(policy.hashCode(), policy2.hashCode());
    assertNotEquals(policy.hashCode(), policy3.hashCode());

    MutableConfiguration<Integer, Integer> config = new MutableConfiguration<>();
    config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(policy)).setStatisticsEnabled(true);
    Cache<Integer, Integer> cache = Caching.getCachingProvider().getCacheManager().createCache("test", config);

    assertEquals(20, policy.getExpiryForCreation().getDurationAmount());
    assertNull(policy.getExpiryForAccess());
    assertNull(policy.getExpiryForUpdate());
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
    Cache<Integer, Integer> cache = Caching.getCachingProvider().getCacheManager().createCache("test", config);

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
    Cache<Integer, Integer> cache = Caching.getCachingProvider().getCacheManager().createCache("test", config);

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
    Cache<Integer, Integer> cache = Caching.getCachingProvider().getCacheManager().createCache("test", config);

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
    Cache<Integer, Integer> cache = Caching.getCachingProvider().getCacheManager().createCache("test", config);

    assertEquals(ETERNAL, policy.getExpiryForCreation());
    assertNull(policy.getExpiryForAccess());
    assertNull(policy.getExpiryForUpdate());
  }
}

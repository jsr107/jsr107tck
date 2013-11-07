package org.jsr107.tck;

import domain.Beagle;
import domain.BorderCollie;
import domain.Chihuahua;
import domain.Dachshund;
import domain.Dog;
import domain.Identifier;
import domain.Identifier2;
import domain.RoughCoatedCollie;
import org.jsr107.tck.testutil.CacheTestSupport;
import org.junit.After;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.swing.border.Border;

import static domain.Sex.FEMALE;
import static domain.Sex.MALE;
import static javax.cache.expiry.Duration.ONE_HOUR;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests of type interactions with Caches
 *
 * @author Greg Luck
 */
public class TypesTest extends CacheTestSupport<Identifier, String> {

  private CacheManager cacheManager = getCacheManager();

  private Beagle pistachio = (Beagle) new Beagle().name(new Identifier("Pistachio")).color("tricolor").sex(MALE).weight(7);
  private RoughCoatedCollie juno = (RoughCoatedCollie) new RoughCoatedCollie().name(new Identifier("Juno")).sex(MALE).weight(7);
  private Dachshund skinny = (Dachshund) new Dachshund().name(new Identifier("Skinny")).sex(MALE).weight(5).neutered(true);
  private Chihuahua tonto = (Chihuahua) new Chihuahua().name(new Identifier("Tonto")).weight(3).sex(MALE).neutered(false);
  private BorderCollie bonzo = (BorderCollie) new BorderCollie().name(new Identifier("Bonzo")).color("tricolor").sex(FEMALE).weight(10);
  private final String cacheName = "sampleCache";

  protected MutableConfiguration<Identifier, String> newMutableConfiguration() {
    return new MutableConfiguration<Identifier, String>().setTypes(Identifier.class, String.class);
  }

  @After
  public void teardown() {
    cacheManager.close();
  }

  /**
   * What happens when you:
   *
   * 1) don't declare using generics and
   * 2) don't specify types during configuration.
   */
  @Test
  public void simpleAPINoGenericsAndNoTypeEnforcement() {

    MutableConfiguration config = new MutableConfiguration();
    Cache cache = cacheManager.createCache(cacheName, config);

    //can put different things in
    cache.put(1, "something");
    cache.put(pistachio.getName(), pistachio);
    cache.put(tonto.getName(), tonto);

    //can get them out
    assertNotNull(cache.get(1));
    assertNotNull(cache.get(pistachio.getName()));

    //can remove them
    assertTrue(cache.remove(1));
    assertTrue(cache.remove(pistachio.getName()));
  }

  /**
   * What happens when you:
   *
   * 1) declare using generics and
   * 2) don't specify types during configuration.
   */
  @Test
  public void simpleAPIWithGenericsAndNoTypeEnforcement() {

    MutableConfiguration config = new MutableConfiguration<String, Integer>();
    Cache<Identifier, Dog> cache = cacheManager.createCache(cacheName, config);


    //Types are restricted
    //Cannot put in wrong types
    //cache.put(1, "something");

    //can put in
    cache.put(pistachio.getName(), pistachio);
    cache.put(tonto.getName(), tonto);

    //cannot get out wrong key types
    //assertNotNull(cache.get(1));
    assertNotNull(cache.get(pistachio.getName()));
    assertNotNull(cache.get(tonto.getName()));

    //cannot remove wrong key types
    //assertTrue(cache.remove(1));
    assertTrue(cache.remove(pistachio.getName()));
    assertTrue(cache.remove(tonto.getName()));

  }


  @Test
  public void simpleAPITypeEnforcement() {

    //configure the cache
    MutableConfiguration<String, Integer> config = new MutableConfiguration<>();
    config.setStoreByValue(true)
        .setTypes(String.class, Integer.class)
        .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(ONE_HOUR))
        .setStatisticsEnabled(true);

    //create the cache
    cacheManager.createCache("simpleCache", config);

    //... and then later to get the cache
    Cache<String, Integer> cache = Caching.getCache("simpleCache",
        String.class, Integer.class);

    //use the cache
    String key = "key";
    Integer value1 = 1;
    cache.put("key", value1);
    Integer value2 = cache.get(key);
    assertEquals(value1, value2);

    cache.remove("key");
    assertNull(cache.get("key"));
  }

  /**
   * Shows the consequences of using Object, Object where you want no enforcement
   */
  @Test
  public void simpleAPITypeEnforcementObject() {


    //configure the cache
    MutableConfiguration<Object, Object> config = new MutableConfiguration<>();
    config.setTypes(Object.class, Object.class)
        .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(ONE_HOUR))
        .setStatisticsEnabled(true);

    //create the cache
    cacheManager.createCache("simpleCache4", config);

    //... and then later to get the cache
    Cache<Object, Object> cache = Caching.getCache("simpleCache4",
        Object.class, Object.class);

    //use the cache
    String key = "key";
    Integer value1 = 1;
    cache.put("key", value1);
    Object value2 = cache.get(key);
    assertEquals(value1, value2);

    cache.remove("key");
    assertNull(cache.get("key"));
  }


  @Test
  public void simpleAPITypeEnforcementUsingCaching() {

    //configure the cache
    MutableConfiguration<String, Integer> config = new MutableConfiguration<>();
    config.setTypes(String.class, Integer.class)
        .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(ONE_HOUR))
        .setStatisticsEnabled(true);

    //create the cache
    cacheManager.createCache("simpleCache2", config);

    //... and then later to get the cache
    Cache<String, Integer> cache = Caching.getCache("simpleCache2",
        String.class, Integer.class);

    //use the cache
    String key = "key";
    Integer value1 = 1;
    cache.put("key", value1);
    Integer value2 = cache.get(key);
    assertEquals(value1, value2);
    cache.remove("key");
    assertNull(cache.get("key"));
  }









}

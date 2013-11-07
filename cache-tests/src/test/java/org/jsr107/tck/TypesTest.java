package org.jsr107.tck;

import domain.Beagle;
import domain.Identifier;
import org.jsr107.tck.testutil.CacheTestSupport;
import org.junit.After;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.spi.CachingProvider;

import static javax.cache.expiry.Duration.ONE_HOUR;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * Tests of type interactions with Caches
 *
 * @author Greg Luck
 */
public class TypesTest extends CacheTestSupport<Identifier, String> {

  private CacheManager cacheManager = getCacheManager();

  @Override
  protected MutableConfiguration<Identifier, String> newMutableConfiguration() {
    return new MutableConfiguration<Identifier, String>().setTypes(Identifier.class, String.class);
  }

  @After
  public void teardown() {
    cacheManager.close();
  }


  /**
   * All these work with get(Object)
   */
  @Test
  public void genericsTest() {

    String cacheName = "genericsCache";
    CacheManager cacheManager = getCacheManager();
    Cache<Identifier, Beagle> cacheGeneric = cacheManager.getCache(cacheName);
    //no runtime enforcement
    cacheGeneric = cacheManager.createCache(cacheName, new MutableConfiguration<Identifier, Beagle>());
    Beagle pistachio = new Beagle();

    cacheGeneric.put(new Identifier("Pistachio"), pistachio);
    //Illegal with change to get(K)
    //Object value = cacheGeneric.get(new Identifier2("Pistachio"));

    Cache cacheNonGeneric = cacheManager.getCache(cacheName);
    //Illegal with change to get(K)
    //value = cacheNonGeneric.get(new Identifier2("Pistachio"));
    //assertNotNull(value);
  }

  @Test
  public void simpleAPINoGenericsAndNoTypeEnforcement() {

    //configure the cache
    String cacheName = "sampleCache";
    MutableConfiguration config = new MutableConfiguration();
    config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(ONE_HOUR))
        .setStatisticsEnabled(true);

    //create the cache
    cacheManager.createCache(cacheName, config);

    //... and then later to get the cache
    Cache cache = cacheManager.getCache(cacheName);

    //use the cache
    String key = "key";
    Integer value1 = 1;
    cache.put(key, value1);

    cache.put("Pistachio", new Beagle());

    //wrong
    cache.put(value1, key);
    Integer value2 = (Integer) cache.get(key);
    assertEquals(value1, value2);

    cache.remove(key);
    assertNull(cache.get(key));
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

  @Test
  public void simpleAPIWithGenericsAndNoTypeEnforcement() {

    //configure the cache
    String cacheName = "sampleCache3";
    MutableConfiguration config = new MutableConfiguration<String, Integer>();
    config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(ONE_HOUR))
        .setStatisticsEnabled(true);

    //create the cache
    cacheManager.createCache(cacheName, config);

    //... and then later to get the cache
    Cache<String, Integer> cache = cacheManager.getCache(cacheName);

    //use the cache
    String key = "key";
    Integer value1 = 1;
    cache.put("key", value1);

    //The following line gives a compile error
    //cache.put(value1, "key1");
    Integer value2 = (Integer) cache.get(key);

    cache.remove(key);
    assertNull(cache.get(key));
  }







}

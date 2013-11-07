package org.jsr107.tck.management;

import org.jsr107.tck.processor.GetEntryProcessor;
import org.jsr107.tck.processor.NoOpEntryProcessor;
import org.jsr107.tck.processor.RemoveEntryProcessor;
import org.jsr107.tck.processor.SetEntryProcessor;
import org.jsr107.tck.testutil.CacheTestSupport;
import org.jsr107.tck.testutil.ExcludeListExcluder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


/**
 * Tests cache statistics
 *
 * @author Greg Luck
 */
public class CacheStatisticsTest extends CacheTestSupport<Long, String> {



  @Before
  public void moreSetUp() {
    cache = getCacheManager().getCache(getTestCacheName(), Long.class, String.class);
    cache.getCacheManager().enableStatistics(cache.getName(), true);
  }

  @Override
  protected MutableConfiguration<Long, String> newMutableConfiguration() {
    return new MutableConfiguration<Long, String>().setTypes(Long.class, String.class);
  }

  @Override
  protected MutableConfiguration<Long, String> extraSetup(MutableConfiguration<Long, String> configuration) {
    return configuration.setStoreByValue(true);
  }


  /**
   * Rule used to exclude tests
   */
  @Rule
  public MethodRule rule = new ExcludeListExcluder(this.getClass()) {

    /* (non-Javadoc)
     * @see javax.cache.util.ExcludeListExcluder#isExcluded(java.lang.String)
     */
    @Override
    protected boolean isExcluded(String methodName) {
      if ("testUnwrap".equals(methodName) && getUnwrapClass(CacheManager.class) == null) {
        return true;
      }

      return super.isExcluded(methodName);
    }
  };

  /**
   * Removes registered CacheStatistics for a Cache
   *
   * @throws javax.cache.CacheException - all exceptions are wrapped in CacheException
   */
  static Object lookupCacheStatisticsAttribute(Cache cache, String attributeName) throws Exception {

    Set<ObjectName> registeredObjectNames = null;
    MBeanServer mBeanServer = CacheTestSupport.resolveMBeanServer();

    ObjectName objectName = calculateObjectName(cache);
    return mBeanServer.getAttribute(objectName, attributeName);
  }

  /**
   * Creates an object name using the scheme
   * "javax.cache:type=Cache&lt;Statistics|Configuration&gt;,CacheManager=&lt;cacheManagerName&gt;,name=&lt;cacheName&gt;"
   */
  private static ObjectName calculateObjectName(Cache cache) {
    try {
      return new ObjectName("javax.cache:type=CacheStatistics" + ",CacheManager="
          + mbeanSafe(cache.getCacheManager().getURI().toString()) + ",Cache=" + mbeanSafe(cache.getName()));
    } catch (MalformedObjectNameException e) {
      throw new CacheException(e);
    }
  }


  /**
   * Filter out invalid ObjectName characters from string.
   *
   * @param string input string
   * @return A valid JMX ObjectName attribute value.
   */
  private static String mbeanSafe(String string) {
    return string == null ? "" : string.replaceAll(":|=|\n|,", ".");
  }


  /**
   * Check that zeroes work
   */
  @Test
  public void testCacheStatisticsAllZero() throws Exception {

    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageGetTime"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AveragePutTime"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"));

  }

  @Test
  public void testCacheStatistics() throws Exception {

    cache.put(1l, "Sooty");
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    Map<Long, String> entries = new HashMap<Long, String>();
    entries.put(2l, "Lucky");
    entries.put(3l, "Prince");
    cache.putAll(entries);
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(3L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));


    //Update. But we count these simply as puts for stats
    cache.put(1l, "Sooty");
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(4L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    cache.putAll(entries);
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(6L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    cache.getAndPut(4l, "Cody");
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(100.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(7L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    cache.getAndPut(4l, "Cody");
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(8L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    String value = cache.get(1l);
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(66.66667f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(33.333336f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(8L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));


    //now do a second miss
    value = cache.get(1234324324l);
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(8L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    //containsKey() should not affect statistics
    assertTrue(cache.containsKey(1l));
    assertFalse(cache.containsKey(1234324324l));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(8L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));


    assertTrue(cache.remove(1L));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(8L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    //no update to cache removals as does not exist
    assertFalse(cache.remove(1L));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(8L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    //should update removals as succeeded
    cache.put(1l, "Sooty");
    assertTrue(cache.remove(1L, "Sooty"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(9L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));


    //should not update removals as remove failed
    assertFalse(cache.remove(1L, "Sooty"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(9L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));


    cache.clear();
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(9L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));


    cache.removeAll();
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(9L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    entries.put(21L, "Trinity");
    cache.putAll(entries);
    cache.removeAll();
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(12L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(5L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));


    cache.putAll(entries);
    entries.remove(21L);
    cache.removeAll(entries.keySet());
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(15L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(7L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));

    cache.removeAll(entries.keySet());
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(50.0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(15L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(7L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));
  }

  /**
   * The lookup and locking of the key is enough to invoke the hit or miss. No
   * Cache.Entry or MutableEntry operation is required.
   */
  @Test
  public void testCacheStatisticsInvokeEntryProcessorNoOp() throws Exception {

    cache.put(1l, "Sooty");

    //existent key. cache hit even though this entry processor does not call anything
    cache.invoke(1l, new NoOpEntryProcessor<Long, String>());
    cache.invoke(1l, new NoOpEntryProcessor<Long, String>());

    //non-existent key. cache miss.
    cache.invoke(1000l, new NoOpEntryProcessor<Long, String>());

    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"), greaterThanOrEqualTo(66.65f));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"), lessThanOrEqualTo(33.34f));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));
  }



  @Test
  public void testCacheStatisticsInvokeEntryProcessorGet() throws Exception {

    cache.put(1l, "Sooty");

    //cache hit
    String result = cache.invoke(1l, new GetEntryProcessor<Long, String>());

    //existent key. cache hit even though this entry processor does not call anything
    cache.invoke(1l, new NoOpEntryProcessor<Long, String>());

    //non-existent key. cache miss.
    cache.invoke(1000l, new NoOpEntryProcessor<Long, String>());

    assertEquals(result, "Sooty");
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"), greaterThanOrEqualTo(66.65f));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"), lessThanOrEqualTo(33.34f));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));
  }


  @Test
  public void testCacheStatisticsInvokeEntryProcessorUpdate() throws Exception {

    cache.put(1l, "Sooty");
    String result = cache.invoke(1l, new SetEntryProcessor<Long, String>("Trinity"));
    assertEquals(result, "Trinity");
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(100.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));
  }

  @Test
  public void testCacheStatisticsInvokeEntryProcessorRemove() throws Exception {

    cache.put(1l, "Sooty");
    String result = cache.invoke(1l, new RemoveEntryProcessor<Long, String, String>(true));
    assertEquals(result, "Sooty");
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(100.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));
  }

  @Test
  public void testIterateAndRemove() throws Exception {


    for (long i = 0; i < 100L; i++) {
      String word = "";
      word = word + " " + "Trinity";
      cache.put(i, word);
    }

    Iterator<Cache.Entry<Long, String>> iterator = cache.iterator();
    while (iterator.hasNext()) {
      iterator.next();
      iterator.remove();
    }

    assertEquals(100L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
    assertEquals(100.0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
    assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
    assertEquals(100L, lookupCacheStatisticsAttribute(cache, "CacheGets"));
    assertEquals(100L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
    assertEquals(100L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
    assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageGetTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AveragePutTime"), greaterThanOrEqualTo(0f));
    assertThat((Float) lookupCacheStatisticsAttribute(cache, "AverageRemoveTime"), greaterThanOrEqualTo(0f));


  }

  @Test
  public void testExpiryOnCreation() throws Exception {

      // close cache since need to configure cache with ExpireOnCreationPolicy
      CacheManager mgr = cache.getCacheManager();
      mgr.destroyCache(cache.getName());

      MutableConfiguration<Long, String> config = new MutableConfiguration<Long, String>();
      config.setStatisticsEnabled(true);
      config.setTypes(Long.class, String.class);
      config.setExpiryPolicyFactory(FactoryBuilder.factoryOf(ExpireOnCreationPolicy.class));

      cache = mgr.createCache(getTestCacheName(), config);
      cache.put(1L, "hello");
      assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CachePuts"));

      Map<Long, String> map = new HashMap<Long, String>();
      map.put(2L, "goodbye");
      map.put(3L, "world");
      cache.putAll(map);
      assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
  }

    /**
     * An {@link javax.cache.expiry.ExpiryPolicy} that will expire {@link Cache} entries
     * before they are created.
     */
    public static class ExpireOnCreationPolicy implements ExpiryPolicy
    {
        @Override
        public Duration getExpiryForCreation() {
            return Duration.ZERO;
        }

        @Override
        public Duration getExpiryForAccess() {
            return Duration.ZERO;
        }

        @Override
        public Duration getExpiryForUpdate() {
            return Duration.ZERO;
        }
    }
}

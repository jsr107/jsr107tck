package org.jsr107.tck.management;

import org.jsr107.tck.testutil.CacheTestSupport;
import org.jsr107.tck.testutil.ExcludeListExcluder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;

import static junit.framework.TestCase.fail;
import static org.jsr107.tck.testutil.TestSupport.MBeanType.CacheConfiguration;
import static org.junit.Assert.assertEquals;


/**
 * Tests cache statistics
 *
 * @author Greg Luck
 */
public class CacheMXBeanTest extends CacheTestSupport<Long, String> {

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

  @Before
  public void moreSetUp() {
    cache = getCacheManager().getCache(getTestCacheName(), Long.class, String.class);
    cache.getCacheManager().enableStatistics(cache.getName(), true);
    cache.getCacheManager().enableManagement(cache.getName(), true);
  }

  @Override
  protected MutableConfiguration<Long, String> newMutableConfiguration() {
    return new MutableConfiguration<Long, String>().setTypes(Long.class, String.class);
  }

  @Override
  protected MutableConfiguration<Long, String> extraSetup(MutableConfiguration<Long, String> configuration) {
    return configuration.setStoreByValue(true);
  }




  @Test
  public void testCacheMXBeanManagementTurnedOff() throws Exception {
    cache.getCacheManager().enableManagement(cache.getName(), false);
    try {
      lookupManagementAttribute(cache, CacheConfiguration, "ReadThrough");
      fail();
    } catch (javax.management.InstanceNotFoundException e) {
      //expected. Shouldn't be there
    }
  }

  @Test
  public void testCacheMXBean() throws Exception {
    assertEquals("java.lang.Long", lookupManagementAttribute(cache, CacheConfiguration, "KeyType"));
    assertEquals("java.lang.String", lookupManagementAttribute(cache, CacheConfiguration, "ValueType"));
    assertEquals(false, lookupManagementAttribute(cache, CacheConfiguration, "ReadThrough"));
    assertEquals(false, lookupManagementAttribute(cache, CacheConfiguration, "WriteThrough"));
    assertEquals(true, lookupManagementAttribute(cache, CacheConfiguration, "StoreByValue"));
    assertEquals(true, lookupManagementAttribute(cache, CacheConfiguration, "StatisticsEnabled"));
    assertEquals(true, lookupManagementAttribute(cache, CacheConfiguration, "ManagementEnabled"));
  }

  @Test
  public void testCustomConfiguration() throws Exception {
    CompleteConfiguration configuration = new MutableConfiguration()
        .setReadThrough(true).setWriteThrough(true).setStoreByValue(false)
        .setTypes(java.math.BigDecimal.class, java.awt.Color.class)
        .setManagementEnabled(true).setStatisticsEnabled(false);
    Cache cache = getCacheManager().createCache("customCache", configuration);

    assertEquals("java.math.BigDecimal", lookupManagementAttribute(cache, CacheConfiguration, "KeyType"));
    assertEquals("java.awt.Color", lookupManagementAttribute(cache, CacheConfiguration, "ValueType"));
    assertEquals(true, lookupManagementAttribute(cache, CacheConfiguration, "ReadThrough"));
    assertEquals(true, lookupManagementAttribute(cache, CacheConfiguration, "WriteThrough"));
    assertEquals(false, lookupManagementAttribute(cache, CacheConfiguration, "StoreByValue"));
    assertEquals(false, lookupManagementAttribute(cache, CacheConfiguration, "StatisticsEnabled"));
    assertEquals(true, lookupManagementAttribute(cache, CacheConfiguration, "ManagementEnabled"));

    cache.close();
  }

}

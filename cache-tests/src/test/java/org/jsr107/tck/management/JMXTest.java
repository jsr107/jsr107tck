/**
 *  Copyright 2011 Terracotta, Inc.
 *  Copyright 2011 Oracle, Inc.
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


package org.jsr107.tck.management;

import org.hamcrest.collection.IsEmptyCollection;
import org.jsr107.tck.testutil.ExcludeListExcluder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;


/**
 * Tests the Cache Statistics using the platform MBeanServer
 * <p/>
 * To examine a typical cache in JConsole, run the main() method and start JConsole. As we only using OpenMBeans there is
 * no need to add any classpath.
 *
 * @author Greg Luck
 * @version $Id: ManagementServiceTest.java 5945 2012-07-10 17:43:48Z teck $
 */
public class JMXTest {

  private CacheManager cacheManager;
  public static final int EMPTY = 0;

  MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
  MutableConfiguration<Integer, String> configuration = new MutableConfiguration<Integer, String>()
      .setStatisticsEnabled(true)
      .setManagementEnabled(true);

  private Cache<Integer, String> cache1;
  private Cache<Integer, String> cache2;

  @Rule
  public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

  /**
   * Obtains a CacheManager using a string-based name from the default
   * CachingProvider.
   *
   * @return a CacheManager
   * @throws Exception
   */
  public static CacheManager getCacheManager() throws Exception {
    CachingProvider provider = Caching.getCachingProvider();

    URI uri = provider.getDefaultURI();

    return Caching.getCachingProvider().getCacheManager(uri, provider.getDefaultClassLoader());
  }

  /**
   * setup test
   */
  @Before
  public void setUp() throws Exception {
    //ensure that the caching provider is closed
    Caching.getCachingProvider().close();

    //now get a new cache manager
    cacheManager = getCacheManager();
  }

  @After
  public void tearDown() throws MalformedObjectNameException {
    //assertEquals(0, mBeanServer.queryNames(new ObjectName("java.cache:*"), null).size());
    cacheManager.close();
    //All registered object names should be removed during shutdown
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), IsEmptyCollection.<ObjectName>empty());
  }

  @Test
  public void testNoEntriesWhenNoCaches() throws Exception {
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(EMPTY));
  }

  @Test
  public void testJMXGetsCacheAdditionsAndRemovals() throws Exception {
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(EMPTY));
    cacheManager.configureCache("new cache", configuration);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(2));
    //name does not exist so no change
    cacheManager.removeCache("sampleCache1");
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(2));
    //correct name, should get removed.
    cacheManager.removeCache("new cache");
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(EMPTY));
  }

  @Test
  public void testMultipleCacheManagers() throws Exception {
    cacheManager.configureCache("new cache", configuration);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(2));

    CacheManager cacheManager2 = getCacheManager();
    cacheManager2.configureCache("other cache", configuration);

    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(4));

    cacheManager2.close();
  }

  @Test
  public void testDoubleRegistration() throws MalformedObjectNameException {
    cacheManager.configureCache("new cache", configuration);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(2));

    cacheManager.enableStatistics("new cache", true);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(2));
  }


  @Test
  public void testCacheStatisticsOffThenOnThenOff() throws Exception {
    MutableConfiguration configuration = new MutableConfiguration();
    configuration.setStatisticsEnabled(false);
    cacheManager.configureCache("cache1", configuration);
    cacheManager.configureCache("cache2", configuration);
    Set<? extends ObjectName> names = mBeanServer.queryNames(new ObjectName("javax.cache:*"), null);
    Assert.assertTrue(names.size() == 0);

    configuration.setStatisticsEnabled(true);
    cacheManager.configureCache("cache3", configuration);
    cacheManager.configureCache("cache4", configuration);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(2));

    cacheManager.enableStatistics("cache3", false);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(1));

    cacheManager.enableStatistics("cache3", true);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(2));
  }

  @Test
  public void testCacheManagementOffThenOnThenOff() throws Exception {
    MutableConfiguration configuration = new MutableConfiguration();
    configuration.setManagementEnabled(false);
    cacheManager.configureCache("cache1", configuration);
    cacheManager.configureCache("cache2", configuration);
    Set<? extends ObjectName> names = mBeanServer.queryNames(new ObjectName("javax.cache:*"), null);
    Assert.assertTrue(names.size() == 0);

    configuration.setManagementEnabled(true);
    cacheManager.configureCache("cache3", configuration);
    cacheManager.configureCache("cache4", configuration);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(2));

    cacheManager.enableManagement("cache3", false);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(1));


    cacheManager.enableManagement("cache3", true);
    assertThat(mBeanServer.queryNames(new ObjectName("javax.cache:*"), null), hasSize(2));
  }


  /**
   * To view in JConsole, start main then run JConsole and connect then go to the
   * MBeans tab and expand javax.cache.&lt;CacheManager&gt;
   */
  public static void main(String[] args) throws Exception {
    System.out.println("Starting...");
    CacheManager cacheManager1 = getCacheManager();
    CacheManager cacheManager2 = getCacheManager();
    try {

      MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
      MutableConfiguration configuration = new MutableConfiguration()
          .setStatisticsEnabled(true)
          .setManagementEnabled(true);

      cacheManager1.configureCache("greg cache1", configuration);
      cacheManager1.configureCache("greg cache2", configuration);
      cacheManager2.configureCache("luck cache1", configuration);
      cacheManager2.configureCache("luck cache2", configuration);


      ObjectName search = new ObjectName("javax.cache:*");
      System.out.println("size=" + mBeanServer.queryNames(search, null).size());
      Thread.sleep(60 * 10000);
      System.out.println("Done...");
    } finally {
      cacheManager1.close();
      cacheManager2.close();
    }
  }
}


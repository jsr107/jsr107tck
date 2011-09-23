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


package javax.cache.statistics;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.util.ExcludeListExcluder;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;



/**
 * Tests the Cache Statistics using the platform MBeanServer
 * @author Greg Luck
 */
public class JMXTest {

    private static final Logger LOG = Logger.getLogger(JMXTest.class.getName());
    private MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    private CacheManager cacheManager;
    private MBeanServerRegistrationUtility mBeanServerRegistrationUtility;
    private Cache<Integer, String> cache1;
    private Cache<Integer, String> cache2;
    
    @Rule
    public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

    /**
     * setup test
     */
    @Before
    public void setUp() throws Exception {
        cacheManager = Caching.getCacheManager(this.getClass().getName());
    }

    @After
    public void tearDown() throws MalformedObjectNameException {
        //assertEquals(0, mBeanServer.queryNames(new ObjectName("java.cache:*"), null).size());
        mBeanServerRegistrationUtility.dispose();
        cacheManager.shutdown();
    }


//    @Test
//    public void testNoCacheStatisticsWhereNoStatisticsTurnedOn() throws Exception {
//        cacheManager.createCacheBuilder("cache1").build();
//        cacheManager.createCacheBuilder("cache2").build();
//
//        mBeanServerRegistrationUtility = new MBeanServerRegistrationUtility(cacheManager, mBeanServer);
//        assertEquals(0, mBeanServer.queryNames(new ObjectName("javax.cache:*"), null).size());
//    }


    @Test
    public void testCacheStatisticsWhereStatisticsTurnedOn() throws Exception {
        cacheManager.createCacheBuilder("cache1").setStatisticsEnabled(true).build();
        cacheManager.createCacheBuilder("cache2").setStatisticsEnabled(true).build();

        mBeanServerRegistrationUtility = new MBeanServerRegistrationUtility(cacheManager, mBeanServer);
        Assert.assertTrue((mBeanServer.queryNames(new ObjectName("javax.cache:*"), null).size()) >= 2);
    }



}

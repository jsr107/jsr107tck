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


package org.jsr107.tck.statistics;

import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.SimpleCacheConfiguration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;



/**
 * Tests the Cache Statistics using the platform MBeanServer
 * @author Greg Luck
 * @since 1.0
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
    	SimpleCacheConfiguration configuration = new SimpleCacheConfiguration();
    	configuration.setStatisticsEnabled(false);
    	
        cacheManager.configureCache("cache1", configuration);
        cacheManager.configureCache("cache2", configuration);

        mBeanServerRegistrationUtility = new MBeanServerRegistrationUtility(cacheManager, mBeanServer);
        Assert.assertTrue((mBeanServer.queryNames(new ObjectName("javax.cache:*"), null).size()) >= 2);
    }

    /*
        TEST_CLASSES=jsr107tck/cache-tests/target/test-classes
        API_JAR=jsr107spec/target/cache-api-0.5.jar
        RI_JAR=RI/cache-ri-impl/target/cache-ri-impl-0.5.jar
        RI_COMMON_JAR=RI/cache-ri-common/target/cache-ri-common-0.5.jar

        TEST=javax.cache.statistics.JMXTest

        CP="$TEST_CLASSES;$API_JAR;$RI_JAR;$RI_COMMON_JAR"

        java -cp $CP -Dcom.sun.management.jmxremote $TEST
    */
    public static void main(String[] args) throws Exception {
        System.out.println("Starting -----------------");
        CacheManager cacheManager = Caching.getCacheManager("Yannis");
        MBeanServerRegistrationUtility mBeanServerRegistrationUtility = null;
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            SimpleCacheConfiguration configuration = new SimpleCacheConfiguration();
        	configuration.setStatisticsEnabled(false);
        	
            cacheManager.configureCache("cache1", configuration);
            cacheManager.configureCache("cache2", configuration);
            
            mBeanServerRegistrationUtility = new MBeanServerRegistrationUtility(cacheManager, mBeanServer);

            ObjectName search = new ObjectName("javax.cache:*");
            System.out.println("size=" + mBeanServer.queryNames(search, null).size());
            Thread.sleep(60 * 1000);
            System.out.println("Done -----------------");
        } finally {
            if (mBeanServerRegistrationUtility != null) mBeanServerRegistrationUtility.dispose();
            cacheManager.shutdown();
        }
    }
}


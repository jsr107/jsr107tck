package org.jsr107.tck;

import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.MutableConfiguration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


/**
 * Tests cache statistics
 *
 * @author Greg Luck
 */
public class CacheStatisticsTest extends CacheTestSupport<Long, String> {


    @Before
    public void setUp() {
        super.setUp();
        cache.getCacheManager().enableStatistics(cache.getName(), true);
    }

    protected <A, B> MutableConfiguration<A, B> extraSetup(MutableConfiguration<A, B> configuration) {
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
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

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
                    + mbeanSafe(cache.getCacheManager().getName()) + ",Cache=" + mbeanSafe(cache.getName()));
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
        return string == null ? "" : string.replaceAll(":|=|\n", ".");
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
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageGetMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AveragePutMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageRemoveMillis"));

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
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageGetMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AveragePutMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageRemoveMillis"));

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
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageGetMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AveragePutMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageRemoveMillis"));


        //Update. But we count these simply as puts for stats
        cache.put(1l, "Sooty");
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
        assertEquals(4L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageGetMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AveragePutMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageRemoveMillis"));



        cache.putAll(entries);
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
        assertEquals(6L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageGetMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AveragePutMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageRemoveMillis"));

        cache.getAndPut(4l, "Cody");
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
        assertEquals(7L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageGetMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AveragePutMillis"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageRemoveMillis"));

        cache.getAndPut(4l, "Cody");
        assertEquals(1L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
        assertEquals(1f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
        assertEquals(8L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageGetMillis"));
        assertThat((Float)lookupCacheStatisticsAttribute(cache, "AveragePutMillis"), greaterThanOrEqualTo(0f));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageRemoveMillis"));

        String value = cache.get(1l);
        assertEquals(2L, lookupCacheStatisticsAttribute(cache, "CacheHits"));
        assertEquals(1f, lookupCacheStatisticsAttribute(cache, "CacheHitPercentage"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheMisses"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "CacheMissPercentage"));
        assertEquals(8L, lookupCacheStatisticsAttribute(cache, "CachePuts"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheRemovals"));
        assertEquals(0L, lookupCacheStatisticsAttribute(cache, "CacheEvictions"));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageGetMillis"));
        assertThat((Float)lookupCacheStatisticsAttribute(cache, "AveragePutMillis"), greaterThanOrEqualTo(0f));
        assertEquals(0f, lookupCacheStatisticsAttribute(cache, "AverageRemoveMillis"));

//        String result = cache.invokeEntryProcessor(1l, new Cache.EntryProcessor<Long, String, String>() {
//            @Override
//            public String process(Cache.MutableEntry<Long, String> entry) {
//                return entry.getValue();
//            }
//        });
//        assertEquals(value, result);
//        assertEquals(4, listener.getCreated());
//        assertEquals(4, listener.getUpdated());
//        assertEquals(3, listener.getReads());
//        assertEquals(0, listener.getExpired());
//        assertEquals(0, listener.getRemoved());
//
//        result = cache.invokeEntryProcessor(1l, new Cache.EntryProcessor<Long, String, String>() {
//            @Override
//            public String process(Cache.MutableEntry<Long, String> entry) {
//                entry.setValue("Zoot");
//                return entry.getValue();
//            }
//        });
//        assertEquals("Zoot", result);
//        assertEquals(4, listener.getCreated());
//        assertEquals(5, listener.getUpdated());
//        assertEquals(3, listener.getReads());
//        assertEquals(0, listener.getExpired());
//        assertEquals(0, listener.getRemoved());
//
//        result = cache.invokeEntryProcessor(1l, new Cache.EntryProcessor<Long, String, String>() {
//            @Override
//            public String process(Cache.MutableEntry<Long, String> entry) {
//                entry.remove();
//                return entry.getValue();
//            }
//        });
//        assertNull(result);
//        assertEquals(4, listener.getCreated());
//        assertEquals(5, listener.getUpdated());
//        assertEquals(3, listener.getReads());
//        assertEquals(0, listener.getExpired());
//        assertEquals(1, listener.getRemoved());
//
//        result = cache.invokeEntryProcessor(1l, new Cache.EntryProcessor<Long, String, String>() {
//            @Override
//            public String process(Cache.MutableEntry<Long, String> entry) {
//                entry.setValue("Moose");
//                return entry.getValue();
//            }
//        });
//        assertEquals("Moose", result);
//        assertEquals(5, listener.getCreated());
//        assertEquals(5, listener.getUpdated());
//        assertEquals(3, listener.getReads());
//        assertEquals(0, listener.getExpired());
//        assertEquals(1, listener.getRemoved());
//
//        Iterator<Cache.Entry<Long, String>> iterator = cache.iterator();
//        while(iterator.hasNext()) {
//            iterator.next();
//            iterator.remove();
//        }
//        assertEquals(5, listener.getCreated());
//        assertEquals(5, listener.getUpdated());
//        assertEquals(7, listener.getReads());
//        assertEquals(0, listener.getExpired());
//        assertEquals(5, listener.getRemoved());
//    }
    }

}

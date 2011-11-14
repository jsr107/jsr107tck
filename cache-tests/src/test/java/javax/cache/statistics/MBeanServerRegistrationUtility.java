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

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.CacheStatistics;
import javax.cache.Status;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A convenience class for registering CacheStatisticsMBeans with an MBeanServer.
 *
 * @author Greg Luck
 * @since 1.0
 */
public class MBeanServerRegistrationUtility {

    private static final Logger LOG = Logger.getLogger(MBeanServerRegistrationUtility.class.getName());

    private MBeanServer mBeanServer;
    private CacheManager cacheManager;
    private Status status;

    /**
     * Creates a Management Service.
     * <p/>
     * This should be disposed of when the CahceManager is.
     */
    public MBeanServerRegistrationUtility(CacheManager cacheManager, MBeanServer mBeanServer) {

        this.cacheManager = cacheManager;
        this.mBeanServer = mBeanServer;
        status = Status.UNINITIALISED;

        try {
            for (Cache<?, ?> cache : this.cacheManager.getCaches()) {
                //todo some caches may not be capturing statistics
                registerCacheStatistics(cache);
            }
        } catch (Exception e) {
            throw new CacheException(e);
        }
        status = Status.STARTED;
    }

    /**
     * Creates an object name using the scheme "javax.cache:type=RICacheStatistics,CacheManager=<cacheManagerName>,name=<cacheName>"
     */
    private ObjectName calculateObjectName(String cacheManagerName, String cacheName) {
        try {
            return new ObjectName("javax.cache:type=RICacheStatistics,CacheManager="
                    + cacheManagerName + ",name=" + mbeanSafe(cacheName));
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
    public static String mbeanSafe(String string) {
        return string == null ? "" : string.replaceAll(":|=|\n", ".");
    }


    private void registerCacheStatistics(Cache cache) throws InstanceAlreadyExistsException,
            MBeanRegistrationException, NotCompliantMBeanException {
        CacheStatistics cacheStatistics = cache.getStatistics();
        if (cacheStatistics != null) {
            mBeanServer.registerMBean(cacheStatistics, calculateObjectName(cacheManager.getName(), cacheStatistics.getName()));
        }
    }


    /**
     * Returns the listener status.
     *
     * @return the status at the point in time the method is called
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Stop the listener and free any resources.
     * Removes registered ObjectNames
     *
     * @throws CacheException - all exceptions are wrapped in CacheException
     */
    public void dispose() {

        Set<ObjectName> registeredObjectNames = null;

        try {
            registeredObjectNames = mBeanServer.queryNames(
                    new ObjectName("javax.cache:*,CacheManager=" + cacheManager.getName()), null);
        } catch (MalformedObjectNameException e) {
            // this should not happen
            LOG.log(Level.SEVERE, "Error querying MBeanServer. Error was " + e.getMessage(), e);
        }
        for (ObjectName registeredObjectName : registeredObjectNames) {
            try {
                mBeanServer.unregisterMBean(registeredObjectName);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error unregistering object instance "
                        + registeredObjectName + " . Error was " + e.getMessage(), e);
            }
        }
        status = Status.STOPPED;
    }
}


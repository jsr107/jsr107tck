/**
 *  Copyright 2011-2013 Terracotta, Inc.
 *  Copyright 2011-2013 Oracle, Inc.
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
package org.jsr107.tck.testutil;

import org.junit.After;
import org.junit.Before;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.configuration.MutableConfiguration;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Unit test support base class
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public abstract class CacheTestSupport<K, V> extends TestSupport {

  protected Cache<K, V> cache;

  @Before
  public void setUp() {
    getCacheManager().createCache(getTestCacheName(), extraSetup(newMutableConfiguration()));

  }

  @After
  public void teardown() {
    getCacheManager().destroyCache(getTestCacheName());
  }

  /**
   * Constructs a new {@link MutableConfiguration} for the test.
   *
   * @return a new {@link MutableConfiguration}
   */
  abstract protected MutableConfiguration<K, V> newMutableConfiguration();


  protected MutableConfiguration<K, V> extraSetup(MutableConfiguration<K, V> configuration) {
    return configuration;
  }


  private LinkedHashMap<Long, String> createLSData(int count, long now) {
    LinkedHashMap<Long, String> map = new LinkedHashMap<Long, String>(count);
    for (int i = 0; i < count; i++) {
      Long key = now + i;
      map.put(key, "value" + key);
    }
    return map;
  }

  private LinkedHashMap<Date, Date> createDDData(int count, long now) {
    LinkedHashMap<Date, Date> map = new LinkedHashMap<Date, Date>(count);
    for (int i = 0; i < count; i++) {
      map.put(new Date(now + i), new Date(now + 1000 + i));
    }
    return map;
  }

  protected LinkedHashMap<Date, Date> createDDData(int count) {
    return createDDData(count, System.currentTimeMillis());
  }

  protected LinkedHashMap<Long, String> createLSData(int count) {
    return createLSData(count, System.currentTimeMillis());
  }

  /**
   * To test your implementation specify system properties per the following RI
   * examples:
   * -Djavax.management.builder.initial=org.jsr107.ri .RITCKMBeanServerBuilder
   * -Dorg.jsr107.tck.management.agentId=RIMBeanServer
   */
  public static MBeanServer resolveMBeanServer() {
    String agentId = System.getProperty("org.jsr107.tck.management.agentId");
    ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(agentId);
    if (mBeanServers.size() < 1) {
      throw new CacheException("The specification requires registration of " +
          "MBeans in an implementation specific MBeanServer. A search for an " +
          "MBeanServer did not find any.");
    }
    return mBeanServers.get(0);
  }

}

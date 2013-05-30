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
package org.jsr107.tck;

import org.jsr107.tck.util.AllTestExcluder;
import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.MutableConfiguration;
import javax.cache.OptionalFeature;
import javax.cache.transaction.IsolationLevel;
import javax.cache.transaction.Mode;
import javax.transaction.UserTransaction;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for CacheManagers that support transactions
 * <p/>
 *
 * @author Greg Luck
 * @since 1.0
 */
public class CacheManagerTransactionsTest extends TestSupport {

  /**
   * Rule used to exclude tests that do not implement Transactions
   */
  @Rule
  public MethodRule rule =
      Caching.getCachingProvider().isSupported(OptionalFeature.TRANSACTIONS) ?
          new ExcludeListExcluder(this.getClass()) :
          new AllTestExcluder();


  @Test
  public void transactionalStatusWhenNoUserTransaction() throws Exception {
    CacheManager cacheManager = getCacheManager();
    UserTransaction userTrans = cacheManager.getUserTransaction();
    assertEquals(javax.transaction.Status.STATUS_NO_TRANSACTION, userTrans.getStatus());
  }

  /**
   * The isolation level returned by a non-transactional cache
   */
  @Test
  public void isolationLevelForNonTransactionalCache() throws Exception {
    CacheManager cacheManager = getCacheManager();
    Cache cache = cacheManager.configureCache("test", new MutableConfiguration());
    assertEquals(IsolationLevel.NONE, cache.getConfiguration().getTransactionIsolationLevel());
  }

  /**
   * Test various illegal combinations
   */
  @Test
  public void setIncorrectIsolationLevelForTransactionalCache() throws Exception {
    CacheManager cacheManager = getCacheManager();
    try {
      cacheManager.configureCache("test", new MutableConfiguration().setTransactions(IsolationLevel.NONE, Mode.NONE));
    } catch (IllegalArgumentException e) {
      //expected
    }
    try {
      cacheManager.configureCache("test", new MutableConfiguration().setTransactions(IsolationLevel.READ_COMMITTED, Mode.NONE));
    } catch (IllegalArgumentException e) {
      //expected
    }
    try {
      cacheManager.configureCache("test", new MutableConfiguration().setTransactions(IsolationLevel.NONE, Mode.LOCAL));
    } catch (IllegalArgumentException e) {
      //expected
    }
  }
}

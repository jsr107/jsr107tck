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
package javax.cache;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import javax.cache.util.AllTestExcluder;
import javax.cache.util.TestExcluder;
import javax.transaction.UserTransaction;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Transactions
 * <p/>
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public class CacheManagerTransactionTest extends TestSupport {
    /**
     * Rule used to exclude tests
     */
    @Rule
    public MethodRule rule =
            CacheManagerFactory.INSTANCE.isSupported(OptionalFeature.JTA) ?
                    new TestExcluder(this.getClass()) :
                    new AllTestExcluder();

    @Test
    public void getUserTransaction() throws Exception {
        CacheManager cm = getCacheManager();
        UserTransaction userTrans = (UserTransaction) cm.getUserTransaction();
        assertEquals(javax.transaction.Status.STATUS_NO_TRANSACTION , userTrans.getStatus());
    }

    @Test
    public void getUserTransactionAlt() throws Exception {
        TransactionalCacheManager cm = (TransactionalCacheManager) getCacheManager();
        UserTransaction userTrans = cm.getUserTransactionAlt();
        assertEquals(javax.transaction.Status.STATUS_NO_TRANSACTION , userTrans.getStatus());
    }

    /**
     * Demonstrating alternative API possibility
     * TODO: remove this
     */
    private interface TransactionalCacheManager extends CacheManager {
        UserTransaction getUserTransactionAlt();
    }
}

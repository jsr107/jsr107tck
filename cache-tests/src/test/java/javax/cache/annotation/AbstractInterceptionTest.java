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
package javax.cache.annotation;

import static org.junit.Assert.*;

import javax.cache.cdiutils.BeanManagerLocatorFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.cache.CacheManagerFactory;
import javax.cache.OptionalFeature;
import javax.cache.util.AllTestExcluder;
import javax.cache.util.ExcludeListExcluder;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import manager.BlogManager;

import org.junit.Rule;
import org.junit.Test;

import domain.Blog;
import org.junit.rules.MethodRule;

/**
 * todo use a factory to lookup a CDI implementation rather than hardcoding in Weld.  - Rick
 * todo enable these tests to be used to test Eric's Spring implementation and any others that come
 *
 * @author Rick Hightower
 */
public abstract class AbstractInterceptionTest {

    /**
     * Rule used to exclude tests that do not implement Transactions
     */
    @Rule
    public MethodRule rule =
            CacheManagerFactory.isSupported(OptionalFeature.ANNOTATIONS) ?
                    new ExcludeListExcluder(this.getClass()) :
                    new AllTestExcluder();

    /**
     *
     */
    private static BeanManager beanManager;
    /**
     *
     */
    protected static BlogManager blogManager;

    /**
     * @param <T>
     * @param type
     * @param qualifiers
     * @return
     */
    public <T> T getBeanByType(Class<T> type, Annotation... qualifiers) {
        if (type == null) {
            throw new IllegalArgumentException("CDI Bean type cannot be null");
        }

        if (beanManager == null) {
            beanManager = BeanManagerLocatorFactory.create().locateBeanManager();
        }

        Set<Bean<?>> beans = beanManager.getBeans(type, qualifiers);
        if (beans.isEmpty()) {
            throw new IllegalStateException("Could not locate a bean of type "
                    + type.getName());
        }
        Bean<?> bean = beanManager.resolve(beans);
        CreationalContext<?> context = beanManager
                .createCreationalContext(bean);
        @SuppressWarnings("unchecked")
        T result = (T) beanManager.getReference(bean, bean.getBeanClass(),
                context);
        return result;
    }

    /**
     * @return
     */
    public abstract BlogManager getBlogManager();

    @Test
    /**
     *
     */
    public void test_AT_CacheResult() {
        String testBody = "" + System.currentTimeMillis();
        String testTitle = "title a";
        Blog blog = new Blog(testTitle, testBody);
        BlogManager blogManager = getBlogManager();
        blogManager.createEntry(blog);

        Blog entryCached = blogManager.getEntryCached(testTitle);
        assertEquals(entryCached.getBody(), testBody);

        /* clear from map, but not from cache */
        blogManager.clearEntry(testTitle);
        entryCached = blogManager.getEntryCached(testTitle);
        assertNotNull("Item should still be in the cache thus not null",
                entryCached);
        assertEquals(
                "Item should still be in the cache and the title should be the same as before",
                entryCached.getBody(), testBody);

    }


    @Test
    /**
     *
     */
    public void test_AT_CacheResult_UsingAt_CacheKeyParam() {
        String testBody = "" + System.currentTimeMillis();
        String testTitle = "title abc";
        Blog blog = new Blog(testTitle, testBody);
        BlogManager blogManager = getBlogManager();
        blogManager.createEntry(blog);

        Blog entryCached = blogManager.getEntryCached("asdf", testTitle, "adsfa");
        assertEquals(entryCached.getBody(), testBody);

        /* clear from map, but not from cache */
        blogManager.clearEntry(testTitle);
        entryCached = blogManager.getEntryCached(testTitle);
        assertNotNull("Item should still be in the cache thus not null",
                entryCached);
        assertEquals(
                "Item should still be in the cache and the title should be the same as before",
                entryCached.getBody(), testBody);

    }


    @Test
    public void test_AT_CacheRemoveEntry() {
        String testBody = "" + System.currentTimeMillis();
        String testTitle = "title b";
        Blog blog = new Blog(testTitle, testBody);
        BlogManager blogManager = getBlogManager();
        blogManager.createEntry(blog);

        Blog entryCached = blogManager.getEntryCached(testTitle);
        assertEquals(entryCached.getBody(), testBody);

        /* clear from cache using annotation @CacheRemoveEntry */
        blogManager.clearEntryFromCache(testTitle);

        /* clear from map, but not from cache */
        blogManager.clearEntry(testTitle);

        entryCached = blogManager.getEntryCached(testTitle);
        assertNull("Item should removed from the cache and the map",
                entryCached);


    }


    @Test
    public void test_AT_CacheRemoveAll() {
        String testBody = "" + System.currentTimeMillis();
        String testTitle = "title b";

        Blog blog = new Blog(testTitle, testBody);
        BlogManager blogManager = getBlogManager();
        blogManager.createEntry(blog);

        Blog entryCached = blogManager.getEntryCached(testTitle);
        assertEquals(entryCached.getBody(), testBody);

        /* clear from map, but not from cache */
        blogManager.clearEntry(testTitle);

        /* clear from cache using annotation @CacheRemoveAll */
        blogManager.clearCache();

        entryCached = blogManager.getEntryCached(testTitle);

        assertNull("Item should removed from the cache and the map",
                entryCached);


    }

}

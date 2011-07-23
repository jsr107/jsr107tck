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

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import manager.BlogManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;

import domain.Blog;

/**
 * 
 * @author Rick Hightower
 * 
 */
public class InterceptionTest {

    /**
     * 
     */
    private static BeanManager beanManager;
    /**
     * 
     */
    private static BlogManager blogManager;

    /**
     * 
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
            WeldContainer delegate;
            Weld weld = new Weld();
            delegate = weld.initialize();
            beanManager = delegate.getBeanManager();
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
     * 
     * @return
     */
    public BlogManager getBlogManager() {
        if (blogManager == null) {
            blogManager = getBeanByType(BlogManager.class);
        }
        return blogManager;
    }

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

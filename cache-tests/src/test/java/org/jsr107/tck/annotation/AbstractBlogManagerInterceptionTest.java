/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.annotation;

import domain.Blog;
import manager.BlogManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Rick Hightower
 */
public abstract class AbstractBlogManagerInterceptionTest extends AbstractInterceptionTest {
  protected abstract BlogManager getBlogManager();

  @Before
  public void before() {
    this.getBlogManager().clearCache();
  }

  /**
   *
   */
  @Test
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


  /**
   *
   */
  @Test
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

        /* clear from cache using annotation @CacheRemove */
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

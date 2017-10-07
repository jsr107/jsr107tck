/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package manager;

import domain.Blog;

import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rick Hightower
 */
public class CacheNameOnEachMethodBlogManagerImpl implements BlogManager {

  /**
   *
   */
  private static Map<String, Blog> map = new HashMap<String, Blog>();

  /**
   *
   */
  @CacheResult(cacheName = "blogManager")
  public Blog getEntryCached(String title) {
    return map.get(title);
  }

  /**
   *
   */
  public Blog getEntryRaw(String title) {
    return map.get(title);
  }

  /**
   * (non-Javadoc)
   *
   * @see manager.BlogManager#clearEntryFromCache(java.lang.String)
   */
  @CacheRemove(cacheName = "blogManager")
  public void clearEntryFromCache(String title) {
  }

  /**
   * (non-Javadoc)
   *
   * @see manager.BlogManager#clearEntry(java.lang.String)
   */
  public void clearEntry(String title) {
    map.put(title, null);
  }

  /**
   * (non-Javadoc)
   *
   * @see manager.BlogManager#clearCache()
   */
  @CacheRemoveAll(cacheName = "blogManager")
  public void clearCache() {
  }

  /**
   * (non-Javadoc)
   *
   * @see manager.BlogManager#createEntry(domain.Blog)
   */
  public void createEntry(Blog blog) {
    map.put(blog.getTitle(), blog);
  }

  /**
   *
   */
  @CacheResult(cacheName = "blogManager")
  public Blog getEntryCached(String randomArg, @CacheKey String title, String randomArg2) {
    return map.get(title);
  }

}

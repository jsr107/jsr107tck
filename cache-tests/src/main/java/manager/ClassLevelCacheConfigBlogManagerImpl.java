/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package manager;

import domain.Blog;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of BlogManager that uses a variety of annotations
 * @author Rick Hightower
 */
@CacheDefaults(cacheName = "blgMngr")
public class ClassLevelCacheConfigBlogManagerImpl implements BlogManager {

  private static Map<String, Blog> map = new HashMap<String, Blog>();

  @CacheResult
  public Blog getEntryCached(String title) {
    return map.get(title);
  }

  public Blog getEntryRaw(String title) {
    return map.get(title);
  }

  /**
   * @see manager.BlogManager#clearEntryFromCache(java.lang.String)
   */
  @CacheRemove
  public void clearEntryFromCache(String title) {
  }

  public void clearEntry(String title) {
    map.put(title, null);
  }

  @CacheRemoveAll
  public void clearCache() {
  }

  public void createEntry(Blog blog) {
    map.put(blog.getTitle(), blog);
  }

  @CacheResult
  public Blog getEntryCached(String randomArg, @CacheKey String title,
                             String randomArg2) {
    return map.get(title);
  }


}

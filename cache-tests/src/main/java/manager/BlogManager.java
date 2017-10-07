/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package manager;

import domain.Blog;

/**
 * @author Rick Hightower
 */
public interface BlogManager {

  Blog getEntryCached(String title);

  Blog getEntryCached(String randomArg, String title, String randomArg2);

  Blog getEntryRaw(String title);

  void clearEntryFromCache(String title);

  void clearEntry(String title);

  void clearCache();

  void createEntry(Blog blog);

}

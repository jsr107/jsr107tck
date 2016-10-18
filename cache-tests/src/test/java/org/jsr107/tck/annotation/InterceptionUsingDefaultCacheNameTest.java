/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.annotation;

import manager.BlogManager;
import manager.UsingDefaultCacheNameBlogManagerImpl;

/**
 * @author Rick Hightower
 */
public class InterceptionUsingDefaultCacheNameTest extends
    AbstractBlogManagerInterceptionTest {

  /**
   *
   */
  @Override
  protected BlogManager getBlogManager() {
    return getBeanByType(UsingDefaultCacheNameBlogManagerImpl.class);
  }

}

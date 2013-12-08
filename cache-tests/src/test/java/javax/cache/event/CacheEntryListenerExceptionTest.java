/**
 *  Copyright (c) 2011-2013 Terracotta, Inc.
 *  Copyright (c) 2011-2013 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.event;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CacheEntryListenerExceptionTest {

  @Test
  public void testConstructors() {
    final Throwable CAUSE = new IllegalStateException();
    assertNotNull(new CacheEntryListenerException());
    assertNotNull(new CacheEntryListenerException("code coverage test"));
    assertNotNull(new CacheEntryListenerException(CAUSE));
    assertNotNull(new CacheEntryListenerException("code coverage test", CAUSE));
  }
}

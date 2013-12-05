/**
 *  Copyright (c) 2011-2013 Terracotta, Inc.
 *  Copyright (c) 2011-2013 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.integration;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class CacheWriterExceptionTest {

  @Test
  public void testConstructors() {
    final Throwable CAUSE = new IllegalStateException();
    assertNotNull(new CacheWriterException());
    assertNotNull(new CacheWriterException("code coverage test"));
    assertNotNull(new CacheWriterException(CAUSE));
    assertNotNull(new CacheWriterException("code coverage test", CAUSE));
  }
}

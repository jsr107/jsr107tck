/**
 *  Copyright (c) 2011-2013 Terracotta, Inc.
 *  Copyright (c) 2011-2013 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.configuration;


public  class FailOnConstructionClass {
  public FailOnConstructionClass() {
    throw new UnsupportedOperationException("code coverage for failing during construction");
  }
}

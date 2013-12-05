/**
 *  Copyright (c) 2011-2013 Terracotta, Inc.
 *  Copyright (c) 2011-2013 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package javax.cache.configuration;

import org.junit.Test;

import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class MutableConfigurationTest {

  @Test
  public void testCopyConstructor() {
    CompleteConfiguration<String, String> copyConfig =
      new MutableConfiguration<String, String>() {

        @Override
        public Factory<ExpiryPolicy> getExpiryPolicyFactory() {
          return null;
        }
      };
    // code coverage of expiry policy factory as null.
    copyConfig.hashCode();

    assertNull(copyConfig.getExpiryPolicyFactory());
    MutableConfiguration<String, String> config =
      new MutableConfiguration<String, String>(copyConfig);
    assertNotNull(config.getExpiryPolicyFactory());
  }

  @Test
  public void testSetAttributeToNull() {
    MutableConfiguration<String, String> config = new MutableConfiguration<String, String>();
    try {
      config.setTypes(null, String.class);
      fail("null poiner exception expected");
    } catch (NullPointerException e) {
      // ignore this expected exception.
    }
    try {
      config.setTypes(String.class, null);
      fail("null poiner exception expected");
    } catch (NullPointerException e) {
      // ignore this expected exception.
    }
    try {
      config.addCacheEntryListenerConfiguration(null);
      fail("null poiner exception expected");
    } catch (NullPointerException e) {
      // ignore this expected exception.
    }
    try {
      config.setExpiryPolicyFactory(null);
      assertNotNull(config.getExpiryPolicyFactory());
    } catch (NullPointerException e) {
      fail("should not throw NullPointerException when setting ExpiryPolicyFactory to null");
    }
  }

  @Test
  public void testEqualsHashCode() {
    MutableConfiguration<String, String> config1 = new MutableConfiguration<String, String>();
    config1.setTypes(String.class, String.class);
    config1.hashCode();
  }
}

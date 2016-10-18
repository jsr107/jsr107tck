/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.configuration;

import org.junit.Test;

import javax.cache.expiry.Duration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FactoryBuilderTest {

  @Test
  public void testClassFactoryUsingClassName() {
    Factory<String> factory = FactoryBuilder.factoryOf(String.class.getCanonicalName());
    assertNotNull(factory.create());
  }

  @Test
  public void testClassFactoryFailOnCreation() {
    Factory<FailOnConstructionClass> factory = FactoryBuilder.factoryOf(FailOnConstructionClass.class.getCanonicalName());
    assertNotNull(factory);
    try {
      factory.create();
      fail("expected failure creating an instance of FailOnConstructionClass");
    } catch (RuntimeException e) {
      // expected exception passed.
      assertTrue(true);
    }
  }

  @Test
  public void testClassFactoryEqualsHashCode() {
    Factory<String> factory1 = FactoryBuilder.factoryOf(String.class.getCanonicalName());
    Factory<String> factory2 = FactoryBuilder.factoryOf(String.class.getCanonicalName());
    Factory<Integer> factory3 = FactoryBuilder.factoryOf(Integer.class.getCanonicalName());

    assertTrue(factory1.equals(factory1));
    assertTrue(factory1.equals(factory2));
    assertFalse(factory1.equals(factory3));
    assertFalse(factory1.equals(null));

    factory1.hashCode();
  }

  @Test
  public void testSingletonFactoryEqualsHashCode() {
    Factory<Duration> factory1 = FactoryBuilder.factoryOf(Duration.ETERNAL);
    Factory<Duration> factory2 = FactoryBuilder.factoryOf(Duration.ETERNAL);
    Factory<Duration> factory3 = FactoryBuilder.factoryOf(Duration.FIVE_MINUTES);
    Factory<String> factory4 = FactoryBuilder.factoryOf("stringFactory");

    assertTrue(factory1.equals(factory1));
    assertTrue(factory1.equals(factory2));
    assertFalse(factory1.equals(null));
    assertFalse(factory1.equals(factory3));
    assertFalse(factory1.equals(factory4));

    // assert different Singleton instances are not equal.
    assertFalse(FactoryBuilder.factoryOf(new StringBuffer("hello")).equals(FactoryBuilder.factoryOf(new StringBuffer("hello"))));

    assertFalse(factory1.equals(null));

    factory1.hashCode();
  }


}

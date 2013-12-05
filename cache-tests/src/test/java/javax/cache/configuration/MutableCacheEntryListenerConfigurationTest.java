/**
 *  Copyright (c) 2011-2013 Terracotta, Inc.
 *  Copyright (c) 2011-2013 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package javax.cache.configuration;

import org.junit.Test;

import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryListenerException;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Added for code coverage
 */
public class MutableCacheEntryListenerConfigurationTest {

  @Test
  public void testCopyConstructor() {
    MutableCacheEntryListenerConfiguration<String, String> config =
      new MutableCacheEntryListenerConfiguration<String, String>(null, null, true, true);
    MutableCacheEntryListenerConfiguration<String, String> copyConfig =
      new MutableCacheEntryListenerConfiguration<String, String>(config);
    assertTrue(config.equals(copyConfig));
    assertFalse(config.equals(new String("notEqualDifferentClassPath")));
  }

  @Test
  public void testEqualsHashCode() {
    MutableCacheEntryListenerConfiguration<String, String> config1 =
      new MutableCacheEntryListenerConfiguration<String, String>(null, null, false, false);
    MutableCacheEntryListenerConfiguration<String, String> config2 =
      new MutableCacheEntryListenerConfiguration<String, String>(null, null, true, false);
    MutableCacheEntryListenerConfiguration<String, String> config3 =
      new MutableCacheEntryListenerConfiguration<String, String>(null, null, false, true);
    MutableCacheEntryListenerConfiguration<String, String> config4 =
      new MutableCacheEntryListenerConfiguration<String, String>(null, null, true, true);
    assertFalse(config1.equals(config2));
    assertFalse(config1.equals(config3));
    assertFalse(config1.equals(config4));
    assertFalse(config1.equals(null));
    config1.hashCode();
    config2.hashCode();
    config3.hashCode();
    config4.hashCode();

    config1.setSynchronous(config2.isSynchronous());
    config1.setOldValueRequired(config2.isOldValueRequired());
    assertTrue(config1.equals(config2));

    config1.setCacheEntryEventFilterFactory(config2.getCacheEntryEventFilterFactory());
    config1.setCacheEntryListenerFactory(config2.getCacheEntryListenerFactory());

    Factory<? extends CacheEntryListener<String, String>> listenerFactory1 =
      FactoryBuilder.factoryOf(ACacheEntryListener.class);
    Factory<? extends CacheEntryListener<String, String>> listenerFactory2 =
      FactoryBuilder.factoryOf(AnotherCacheEntryListener.class);
    config1.setCacheEntryListenerFactory(listenerFactory1);
    assertFalse(config2.equals(config1));
    config2.setCacheEntryListenerFactory(listenerFactory1);
    assertTrue(config1.equals(config2));
    config1.hashCode();

    config2.setCacheEntryListenerFactory(listenerFactory2);
    assertFalse(config1.equals(config2));

    config2.setCacheEntryListenerFactory(listenerFactory1);
    Factory<? extends CacheEntryEventFilter<String, String>> filterFactory1 =
      FactoryBuilder.factoryOf(ACacheEntryEventFilter.class);
    Factory<? extends CacheEntryEventFilter<String, String>> filterFactory2 =
      FactoryBuilder.factoryOf(AnotherCacheEntryEventFilter.class);
    config1.setCacheEntryEventFilterFactory(filterFactory1);
    config2.setCacheEntryEventFilterFactory(filterFactory1);
    assertTrue(config1.equals(config2));
    config2.setCacheEntryEventFilterFactory(filterFactory2);
    assertFalse(config1.equals(config2));
    config1.hashCode();
  }

  public static class ACacheEntryListener implements CacheEntryListener<String, String> {

  }
  public static class AnotherCacheEntryListener implements CacheEntryListener<String, String> {

  }

  public static class ACacheEntryEventFilter implements CacheEntryEventFilter<String, String> {

    @Override
    public boolean evaluate(CacheEntryEvent<? extends String, ? extends String> event) throws CacheEntryListenerException {
      throw new UnsupportedOperationException("not implemented");
    }
  }

  public static class AnotherCacheEntryEventFilter implements CacheEntryEventFilter<String, String> {

    @Override
    public boolean evaluate(CacheEntryEvent<? extends String, ? extends String> event) throws CacheEntryListenerException {
      throw new UnsupportedOperationException("not implemented");
    }
  }
}

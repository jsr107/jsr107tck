/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache;

import org.junit.After;
import org.junit.Test;

import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * Since Caching implementation only looks up system property for caching provider when there
 * is no resolved CachingProviders yet, each test uses a unique ClassLoader so the SystemProperty
 * is referenced in each individual test case.
 */
public class CachingTest {

  // TODO: test getCachingProviders methods with multiple mock providers.
  // Easy to do in jsr107spec (was already done) but requires more care in tck.

  @After
  public void resetSystemProperty() {
    System.clearProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER);
    assertEquals(null, System.getProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER));
  }

  @Test
  public void testSetDefaultClassLoader() {
    ClassLoader defaultClassLoader = Caching.getDefaultClassLoader();
    ClassLoader uniqueClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());
    Caching.setDefaultClassLoader(uniqueClassLoader);
    assertEquals(uniqueClassLoader, Caching.getDefaultClassLoader());
    Caching.setDefaultClassLoader(null);
    assertEquals(defaultClassLoader, Caching.getDefaultClassLoader());
  }

  @Test
  public void testJCacheCachingProviderSystemProperty() {
    System.setProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER,
      "javax.cache.CachingTest$ACachingProviderImpl");
    ClassLoader uniqueClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());
    CachingProvider provider = Caching.getCachingProvider(uniqueClassLoader);
    assertEquals(provider.getClass(), ACachingProviderImpl.class);
  }

  @Test
  public void testJCacheCachingProviderSystemPropertyUsingClassLoader() {
    System.setProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER,
      "javax.cache.CachingTest$AlternativeCachingProviderImpl");
    ClassLoader alternativeClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());
    CachingProvider provider = Caching.getCachingProvider(alternativeClassLoader);
    assertEquals(provider.getClass(), AlternativeCachingProviderImpl.class);
  }

  @Test
  public void testJCacheCachingProvider() {
    ClassLoader uniqueClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());
    Caching.setDefaultClassLoader(uniqueClassLoader);
    try {
      CachingProvider defaultProvider = Caching.getCachingProvider();
      CachingProvider alternativeProvider =
        Caching.getCachingProvider("javax.cache.CachingTest$AlternativeCachingProviderImpl", null);
      assertNotEquals(defaultProvider, alternativeProvider);
      assertEquals(AlternativeCachingProviderImpl.class, alternativeProvider.getClass());
    } finally {
      Caching.setDefaultClassLoader(null);
    }
  }

  @Test
  public void testJCacheCachingProviderUsingClassLoader() {
    ClassLoader alternativeClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());
    CachingProvider provider = Caching.getCachingProvider( "javax.cache.CachingTest$AlternativeCachingProviderImpl",
      alternativeClassLoader);
    assertEquals(provider.getClass(), AlternativeCachingProviderImpl.class);
  }


  @Test( expected = CacheException.class )
  public void testInvalidJCacheCachingProviderSystemProperty() {
    System.setProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER,
      "java.lang.String");
    ClassLoader uniqueClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());
    CachingProvider provider = Caching.getCachingProvider(uniqueClassLoader);
    fail("expected an exception to be thrown");
  }

  @Test( expected = CacheException.class )
  public void testNonexistentCacheCachingProviderSystemProperty() {
    System.setProperty(Caching.JAVAX_CACHE_CACHING_PROVIDER,
      "NonExistentCachingProvider");

    ClassLoader uniqueClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());
    CachingProvider provider = Caching.getCachingProvider(uniqueClassLoader);
    fail("expected an exception to be thrown");
  }

  @Test(expected = CacheException.class)
  public void testInvalidCacheProvider() {
    Caching.getCachingProvider(CachingTest.class.getCanonicalName());
    fail("expect CacheException");
  }

  @Test
  public void testJCacheCachingProviders() {
    Iterable<CachingProvider> iterable = Caching.getCachingProviders();
    int i = 0;
    for (CachingProvider provider : iterable) {
      System.out.println("provider=" + provider.getClass().getCanonicalName());
      i++;
    }
    assertEquals(1, i);

    i = 0;
    iterable = Caching.getCachingProviders(Caching.getDefaultClassLoader());
    for (CachingProvider provider : iterable) {
      System.out.println("provider=" + provider.getClass().getCanonicalName());
      i++;
    }
    assertEquals(1, i);

    // test case added for code coverage
    i = 0;
    iterable = Caching.getCachingProviders(null);
    for (CachingProvider provider : iterable) {
      System.out.println("provider=" + provider.getClass().getCanonicalName());
      i++;
    }
    assertEquals(1, i);

  }

  @Test(expected = CacheException.class)
  public void testMultipleCacheProvidersWithClassLoader () {

    // configure a unique ClassLoader to have multiple providers needed for this test case.
    ClassLoader uniqueClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());
    CachingProvider provider1 = Caching.getCachingProvider("javax.cache.CachingTest$AlternativeCachingProviderImpl", uniqueClassLoader);
    CachingProvider provider2 = Caching.getCachingProvider("javax.cache.CachingTest$ACachingProviderImpl", uniqueClassLoader);
    assertNotEquals(provider1, provider2);

    // added for code coverage.  needed to lookup provider and it is already in cache.
    CachingProvider provider3 = Caching.getCachingProvider("javax.cache.CachingTest$ACachingProviderImpl", uniqueClassLoader);
    assertEquals(provider2, provider3);

    Caching.getCachingProvider(uniqueClassLoader);
    fail("expected CacheException MultipleCacheProviders");
  }

  //@Test(expected = CacheException.class)
  public void testNoCacheProvidersWithClassLoader () {

    // configure a unique ClassLoader to have multiple providers needed for this test case.
    ClassLoader uniqueClassLoader = new MyClassLoader(Thread.currentThread().getContextClassLoader());

    Caching.getCachingProvider(uniqueClassLoader);
    fail("expected CacheException MultipleCacheProviders");
  }

  public static class MyClassLoader extends ClassLoader {
    public MyClassLoader(ClassLoader parent) {
      super(parent);
    }
  }

  public static class CachingProviderImpl implements CachingProvider {
    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ClassLoader getDefaultClassLoader() {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public URI getDefaultURI() {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Properties getDefaultProperties() {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CacheManager getCacheManager() {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void close() {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void close(ClassLoader classLoader) {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void close(URI uri, ClassLoader classLoader) {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean isSupported(OptionalFeature optionalFeature) {
      throw new UnsupportedOperationException("not implemented");
    }
  }

  static public class ACachingProviderImpl extends CachingProviderImpl {
  }

  static public class AlternativeCachingProviderImpl extends CachingProviderImpl {
  }
}

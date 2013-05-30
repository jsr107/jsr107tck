/**
 *  Copyright 2011 Terracotta, Inc.
 *  Copyright 2011 Oracle, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jsr107.tck;

import org.jsr107.tck.util.ExcludeListExcluder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.OptionalFeature;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * Tests the {@link Caching} class.
 * The tests here implicitly also test the {@link javax.cache.spi.CachingProvider} used by
 * Caching to create instances of {@link CacheManager}
 *
 * @author Yannis Cosmadopoulos
 * @see Caching
 * @since 1.0
 */
public class CachingTest {


  /**
   * Rule used to exclude tests
   */
  @Rule
  public ExcludeListExcluder rule = new ExcludeListExcluder(this.getClass());

  @Test
  public void getCachingProviderSingleton() {
    CachingProvider provider1 = Caching.getCachingProvider();
    CachingProvider provider2 = Caching.getCachingProvider();

    Assert.assertEquals(provider1, provider2);
  }


  /**
   * Multiple invocations of {@link CachingProvider#getCacheManager()} return the same CacheManager
   */
  @Test
  public void getCacheManager_singleton() {
    CachingProvider provider = Caching.getCachingProvider();

    CacheManager manager = provider.getCacheManager();
    assertNotNull(manager);
    assertSame(manager, provider.getCacheManager());
  }

  @Test
  public void getCacheManager_defaultURI() {
    CachingProvider provider = Caching.getCachingProvider();

    assertSame(provider.getCacheManager(),
        provider.getCacheManager(provider.getDefaultURI(), provider.getDefaultClassLoader()));

    CacheManager manager = provider.getCacheManager();
    assertEquals(provider.getDefaultURI(), manager.getURI());
  }

  /**
   * Multiple invocations of {@link CachingProvider#getCacheManager(java.net.URI, ClassLoader)} with the same name
   * return the same CacheManager instance
   */
  @Test
  public void getCacheManager_URI() throws Exception {
    CachingProvider provider = Caching.getCachingProvider();

    URI uri = new URI("javax.cache.MyCache");

    CacheManager manager = provider.getCacheManager(uri, provider.getDefaultClassLoader());
    assertNotNull(manager);
    assertSame(manager, provider.getCacheManager(uri, provider.getDefaultClassLoader()));

    assertEquals(uri, manager.getURI());
  }

  /**
   * Invocations of {@link CachingProvider#getCacheManager(java.net.URI, ClassLoader)} using a name other
   * than the default returns a CacheManager other than the default
   */
  @Test
  public void getCacheManager_URI_notDefault() throws Exception {
    CachingProvider provider = Caching.getCachingProvider();

    URI uri = new URI("javax.cache.MyCache");

    CacheManager manager = provider.getCacheManager(uri, provider.getDefaultClassLoader());
    assertNotNull(manager);
    assertNotSame(manager, provider.getCacheManager());
  }

  /**
   * Invocations of {@link CachingProvider#getCacheManager(java.net.URI, ClassLoader)} using different names return
   * different instances
   */
  @Test
  public void getCacheManager_URI_different() throws Exception {
    CachingProvider provider = Caching.getCachingProvider();

    URI uri1 = new URI("javax.cache.MyCacheOne");
    URI uri2 = new URI("javax.cache.MyCacheTwo");

    assertNotSame(provider.getCacheManager(uri1, provider.getDefaultClassLoader()), provider.getCacheManager(uri2, provider.getDefaultClassLoader()));
  }

  @Test
  public void isSupported() {
    CachingProvider provider = Caching.getCachingProvider();

    OptionalFeature[] features = OptionalFeature.values();
    for (OptionalFeature feature : features) {
      boolean value = provider.isSupported(feature);
      Logger.getLogger(getClass().getName()).info("Optional feature " + feature + " supported=" + value);
    }
  }
}

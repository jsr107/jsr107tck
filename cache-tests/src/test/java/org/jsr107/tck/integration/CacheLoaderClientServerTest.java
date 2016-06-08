/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package org.jsr107.tck.integration;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.fail;

/**
 * Functional Tests for the {@link CacheLoaderClient} and {@link CacheLoaderServer}
 * classes.
 *
 * @author Brian Oliver
 * @author Jens Wilke
 */
public class CacheLoaderClientServerTest {

  /**
   * Ensure that values can be loaded from the {@link CacheLoaderClient} via
   * the {@link CacheLoaderServer}.
   */
  @Test
  public void shouldLoadFromServerWithClient() throws Exception {
    RecordingCacheLoader<String> recordingCacheLoader = new RecordingCacheLoader<String>();
    CacheLoaderServer<String, String> serverCacheLoader = new CacheLoaderServer<String, String>(10000, recordingCacheLoader);
    serverCacheLoader.open();
    CacheLoaderClient<String, String> clientCacheLoader = new CacheLoaderClient<>(serverCacheLoader.getInetAddress(), serverCacheLoader.getPort());
    String value = clientCacheLoader.load("gudday");
    Assert.assertThat(value, is(notNullValue()));
    Assert.assertThat(value, is("gudday"));
    Assert.assertThat(recordingCacheLoader.hasLoaded("gudday"), is(true));
    clientCacheLoader.close();
    serverCacheLoader.close();
  }

  /**
   * Ensure that exceptions thrown by an underlying cache loader are re-thrown.
   */
  @Test
  public void shouldRethrowExceptions() throws Exception {
    FailingCacheLoader<String, String> failingCacheLoader = new FailingCacheLoader<>();
    CacheLoaderServer<String, String> serverCacheLoader = new CacheLoaderServer<String, String>(10000, failingCacheLoader);
    serverCacheLoader.open();
    CacheLoaderClient<String, String> clientCacheLoader = new CacheLoaderClient<>(serverCacheLoader.getInetAddress(), serverCacheLoader.getPort());
    try {
      String value = clientCacheLoader.load("gudday");
      fail("An UnsupportedOperationException should have been thrown");
    } catch (UnsupportedOperationException e) {
     // expected
    }
    clientCacheLoader.close();
    serverCacheLoader.close();
  }

  /**
   * Ensure that <code>null</code> entries can be passed from the
   * {@link CacheLoaderServer} back to the {@link CacheLoaderClient}.
   */
  @Test
  public void shouldLoadNullValuesFromServerWithClient() throws Exception {
    NullValueCacheLoader<String, String> nullCacheLoader = new NullValueCacheLoader<>();
    CacheLoaderServer<String, String> serverCacheLoader = new CacheLoaderServer<String, String>(10000, nullCacheLoader);
    serverCacheLoader.open();
    CacheLoaderClient<String, String> clientCacheLoader = new CacheLoaderClient<>(serverCacheLoader.getInetAddress(), serverCacheLoader.getPort());
    String value = clientCacheLoader.load("gudday");
    Assert.assertThat(value, is(nullValue()));
    clientCacheLoader.close();
    serverCacheLoader.close();
  }

  /**
   * Assert that the server checks correctly whether open clients exists when close
   * is called.
   *
   * @see <a href="https://github.com/jsr107/jsr107tck/issues/100">Customizations may implement Closeable</a>
   */
  @Test
  public void clientMustBeClosedBeforeServer() throws Exception {
    NullValueCacheLoader<String, String> nullCacheLoader = new NullValueCacheLoader<>();
    CacheLoaderServer<String, String> serverCacheLoader = new CacheLoaderServer<String, String>(10000, nullCacheLoader);
    serverCacheLoader.open();
    CacheLoaderClient<String, String> clientCacheLoader = new CacheLoaderClient<>(serverCacheLoader.getInetAddress(), serverCacheLoader.getPort());
    clientCacheLoader.load("hi");
    try {
      serverCacheLoader.close();
    } catch (IllegalStateException e) {
      // expected
    }
  }

}

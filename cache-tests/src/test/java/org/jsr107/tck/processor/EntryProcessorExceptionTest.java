/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package org.jsr107.tck.processor;

import org.junit.Test;

import javax.cache.processor.EntryProcessorException;

/**
 * Tests the exception for completeness
 * @author Greg Luck
 */
public class EntryProcessorExceptionTest {


  @Test
  public void testEntryProcessorException() {
    try {
      throw new EntryProcessorException();
    } catch (EntryProcessorException e) {
      //
    }
  }

  @Test
  public void testEntryProcessorExceptionCause() {
    try {
      throw new EntryProcessorException(new NullPointerException());
    } catch (EntryProcessorException e) {
      //
    }
  }

  @Test
  public void testEntryProcessorExceptionCauseAndMessage() {
    try {
      throw new EntryProcessorException("Doh!", new NullPointerException());
    } catch (EntryProcessorException e) {
      //
    }
  }

  @Test
  public void testEntryProcessorExceptionMessage() {
    try {
      throw new EntryProcessorException("Doh!");
    } catch (EntryProcessorException e) {
      //
    }
  }

}

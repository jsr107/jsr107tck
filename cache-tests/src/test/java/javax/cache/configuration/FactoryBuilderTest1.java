/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package javax.cache.configuration;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Functional tests for the {@link FactoryBuilder} class.
 *
 * @author Brian Oliver
 */
public class FactoryBuilderTest1 {

  /**
   * Ensure that a {@link FactoryBuilder} can create a regular class.
   */
  @Test
  public void shouldCreateRegularClass() {

    Factory<AnOuterClass> factory = FactoryBuilder.factoryOf(AnOuterClass.class);

    AnOuterClass anOuterClass = factory.create();

    assertThat(anOuterClass, is(not(nullValue())));
    assertThat(anOuterClass, is(instanceOf(AnOuterClass.class)));
  }

  /**
   * Ensure that a {@link FactoryBuilder} can create a static inner class.
   */
  @Test
  public void shouldBuildStaticInnerClass() {

    Factory<AnInnerClass> factory = FactoryBuilder.factoryOf(AnInnerClass.class);

    AnInnerClass anInnerClass = factory.create();

    assertThat(anInnerClass, is(not(nullValue())));
    assertThat(anInnerClass, is(instanceOf(AnInnerClass.class)));
  }

  /**
   * A simple inner class for testing purposes.
   */
  public static class AnInnerClass {

  }
}

/**
 *  Copyright 2012 Terracotta, Inc.
 *  Copyright 2012 Oracle, Inc.
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

package javax.cache.configuration;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Functional tests for the {@link FactoryBuilder} class.
 *
 * @author Brian Oliver
 */
public class FactoryBuilderTest {

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

/**
 *  Copyright 2011-2013 Terracotta, Inc.
 *  Copyright 2011-2013 Oracle, Inc.
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

package javax.cache.annotation;

/**
 * SPI used by an annotation implementation test harness to make testable beans available to the TCK
 *
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface BeanProvider {
  /**
   * Load the specified bean from the test-domain project configured appropriately for annotation testing
   *
   * @param <T>        bean type
   * @param beanClass  the bean class
   *
   * @return instance of Bean Provider
   */
  <T> T getBeanByType(Class<T> beanClass);
}

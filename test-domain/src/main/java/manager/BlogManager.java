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
package manager;

import domain.Blog;

/**
 * @author Rick Hightower
 */
public interface BlogManager {

  /**
   * @param title
   * @return
   */
  Blog getEntryCached(String title);

  /**
   * @param title
   * @return
   */
  Blog getEntryCached(String randomArg, String title, String randomArg2);

  /**
   * @param title
   * @return
   */
  Blog getEntryRaw(String title);

  /**
   * @param title
   */
  void clearEntryFromCache(String title);

  /**
   * @param title
   */
  void clearEntry(String title);

  /**
   *
   */
  void clearCache();

  /**
   * @param blog
   */
  void createEntry(Blog blog);

}

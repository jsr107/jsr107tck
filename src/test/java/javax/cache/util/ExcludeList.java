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
package javax.cache.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For the TCK we need to have an exclude list of bad tests so that disabling tests
 * can be done without changing code. This is one mechanism to do this.
 *
 * @author Yannis Cosmadopoulos
 * @since 1.7
 */
public enum ExcludeList {
    /**
     * The singleton
     */
    instance;

    private final HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();

    private ExcludeList() {
        String fileName = System.getProperty("ExcludeList", "ExcludeList");

        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if (url != null) {
            Logger logger = Logger.getLogger("org.junit");
            logger.info("===== ExcludeList url=" + url);
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while((line = in.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        int dot = line.lastIndexOf(".");
                        String className = line.substring(0, dot);
                        String methodName = line.substring(dot + 1);
                        Set<String> entry = map.get(className);
                        if (entry == null) {
                            entry = new HashSet<String>();
                            map.put(className, entry);
                        }
                        entry.add(methodName);
                    }
                }
                in.close();
            } catch (IOException e) {
                logger.config(e.toString());
                logger.log(Level.SEVERE, "ExcludeList file:" + fileName, e);
            }
        }
    }

    public Set<String> getExcludes(String className) {
        return map.get(className);
    }
}

/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.testutil;

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
 * can be done without changing code.
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public enum ExcludeList {
  /**
   * The singleton.
   * The exclude list is obtained by reading from a resource in the classpath.
   * The default name of the resource is "ExcludeList", but can be overridden using
   * the system property "ExcludeList".
   * The resource should contain one entry per line with a classname and method name separated by a #
   * There is a sample ExcludeList file in the resource area of the project
   */
  INSTANCE(System.getProperty("ExcludeList", "ExcludeList"));

  private final Logger logger = Logger.getLogger(getClass().getName());

  private final HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();

  private ExcludeList(String fileName) {
    URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
    if (url != null) {
      logger.info("===== ExcludeList url=" + url);
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = in.readLine()) != null) {
          line = line.trim();
          if (line.length() > 0 && !line.startsWith("#")) {
            handleLine(line);
          }
        }
        in.close();
      } catch (IOException e) {
        logger.config(e.toString());
        logger.log(Level.SEVERE, "ExcludeList file:" + fileName, e);
      }
    }
  }

  private void handleLine(String line) {
    int dot = line.lastIndexOf("#");
    if (dot > 0) {
      String className = line.substring(0, dot);
      String methodName = line.substring(dot + 1);
      Set<String> entry = map.get(className);
      if (entry == null) {
        entry = new HashSet<String>();
        map.put(className, entry);
      }
      entry.add(methodName);
    } else {
      logger.log(Level.WARNING, "===== ExcludeList bad entry: " + line);
    }
  }

  private String getFileName() {
    return System.getProperty("ExcludeList", "ExcludeList");
  }

  public Set<String> getExcludes(String className) {
    return map.get(className);
  }
}

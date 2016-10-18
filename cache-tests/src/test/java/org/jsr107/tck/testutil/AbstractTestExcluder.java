/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.testutil;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.logging.Logger;

/**
 * For the TCK we need to have an exclude list of bad tests so that disabling tests
 * can be done without changing code.
 * <p>
 * This class creates a rule for the class provided
 * </p>
 * The exclude list is created by {@link ExcludeList} by creating a file in the root of your classpath called
 * "ExcludeList". There is an example in the testRI module for testing the RI.
 *
 * @author Yannis Cosmadopoulos
 * @since 1.0
 */
public abstract class AbstractTestExcluder implements MethodRule {
  private final Logger logger = Logger.getLogger(getClass().getName());

  /**
   * {@inheritDoc}
   */
  public Statement apply(Statement statement, FrameworkMethod frameworkMethod, Object o) {
    final String methodName = frameworkMethod.getName();
    final String className = frameworkMethod.getMethod().getDeclaringClass().getName();
    if (isExcluded(methodName)) {
      return new ExcludedStatement(className, methodName, logger);
    } else {
      return statement;
    }
  }

  protected abstract boolean isExcluded(String methodName);

  protected Logger getLogger() {
    return logger;
  }

  /**
   * Statement for excluded methods
   */
  private static final class ExcludedStatement extends Statement {
    private final String methodName;
    private final String className;
    private final Logger logger;

    private ExcludedStatement(String className, String methodName, Logger logger) {
      this.className = className;
      this.methodName = methodName;
      this.logger = logger;
    }

    @Override
    public void evaluate() throws Throwable {
      logger.info("===== EXCLUDING TEST '" + className + "'\t'" + methodName + "'.");
    }
  }
}

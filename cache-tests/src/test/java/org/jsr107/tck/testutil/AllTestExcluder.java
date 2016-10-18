/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package org.jsr107.tck.testutil;

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
public class AllTestExcluder extends AbstractTestExcluder {
  @Override
  protected boolean isExcluded(String methodName) {
    return true;
  }
}

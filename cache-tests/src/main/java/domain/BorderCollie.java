/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

/**
 * @author Greg Luck
 */
public class BorderCollie extends Dog implements Collie {

  /**
   * Tells the Collie to herd
   */
  @Override
  public void herd() {

  }

  protected BorderCollie getThis() {
    return this;
  }
}


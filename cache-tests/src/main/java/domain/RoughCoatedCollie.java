/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

/**
 * These are Lassie dogs.
 *
 * Incidentally though Lassie was a she, the actors were male dogs
 * as they have the bigger coats.
 * @author Greg Luck
 */
public class RoughCoatedCollie extends Dog implements Collie {

  /**
   * Tells the Collie to herd
   */
  @Override
  public void herd() {

  }

  protected RoughCoatedCollie getThis() {
    return this;
  }

}


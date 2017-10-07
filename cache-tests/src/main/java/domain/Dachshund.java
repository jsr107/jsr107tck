/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

/**
 * Poor old Dachshund is not Serializable
 * @author Greg Luck
 */
public class Dachshund extends Dog implements Hound {

  /**
   * Tells the hound to bay
   *
   * @param loudness 0 for mute, 1 is the softest and 255 is the loudest
   * @param duration the duraction of the bay in seconds
   */
  @Override
  public void bay(int loudness, int duration) {

  }

  protected Dachshund getThis() {
    return this;
  }

}


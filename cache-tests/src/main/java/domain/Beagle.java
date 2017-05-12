/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

import java.io.Serializable;

/**
 * A Beagle is a Dog and a type of hound.
 *
 * @author Greg Luck
 */
public class Beagle extends Dog implements Hound, Serializable {


  public Beagle getThis() {
    return this;
  }

  /**
   * Tells the hound to bay
   *
   * @param loudness 0 for mute, 1 is the softest and 255 is the loudest
   * @param duration the duraction of the bay in seconds
   */
  public void bay(int loudness, int duration) {

  }




}

/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

/**
 * A hound which is a dog most characterised by its sound, a cross between a howl and a bark.
 *
 * @author Greg Luck
 */
public interface Hound {

  /**
   * Tells the hound to bay
   *
   * @param loudness 0 for mute, 1 is the softest and 255 is the loudest
   * @param duration the duraction of the bay in seconds
   */
  void bay(int loudness, int duration);


}

/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

import java.io.Serializable;

/**
 * A Collie which is can be characterised by its herding instinct.
 *
 * @author Greg Luck
 */
public interface Collie extends Serializable {

  /**
   * Tells the hound to bay
   */
  void herd();


}

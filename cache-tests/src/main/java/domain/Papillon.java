/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

import java.io.Serializable;

/**
 * A Papillon is a Dog but not a Hound or a Collie.
 *
 * @author Greg Luck
 */
public class Papillon extends Dog implements Serializable {


  protected Papillon getThis() {
    return this;
  }

}

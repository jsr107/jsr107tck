/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

import java.io.Serializable;

/**
 * @author Greg Luck
 */
public class Identifier implements Serializable {

  private final String name;

  /**
   * Constructor
   *
   * @param name name
   */
  public Identifier(String name) {
    this.name = name;
  }

  /**
   * Implemented without class checking
   *
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    return o.toString().equals(this.toString());
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }
}

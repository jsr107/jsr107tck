/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */

package domain;

/**
 * This is a key type with a transient field. Per the spec portability recommendations
 * transient fields should not be taken into account in equals and hashcode.
 *
 * @author Greg Luck
 */
public class Identifier2 {

  private final String name;

  private transient long timeStamp;

  /**
   * Constructor
   *
   * @param name name
   */
  public Identifier2(String name) {
    this.name = name;
    timeStamp = System.currentTimeMillis();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Identifier2)) return false;

    Identifier2 that = (Identifier2) o;

    if (name != null ? !name.equals(that.name) : that.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public String toString() {
    return name;
  }
}

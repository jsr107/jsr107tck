/**
 *  Copyright 2011 Terracotta, Inc.
 *  Copyright 2011 Oracle, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package domain;

/**
 * A Dog.
 *
 * @author Greg Luck
 */
public class Dog {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Dog)) return false;

    Dog dog = (Dog) o;

    if (heightInCm != dog.heightInCm) return false;
    if (lengthInCm != dog.lengthInCm) return false;
    if (neutered != dog.neutered) return false;
    if (weighInKg != dog.weighInKg) return false;
    if (color != null ? !color.equals(dog.color) : dog.color != null) return false;
    if (name != null ? !name.equals(dog.name) : dog.name != null) return false;
    if (sex != dog.sex) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (color != null ? color.hashCode() : 0);
    result = 31 * result + weighInKg;
    result = 31 * result + (int) (lengthInCm ^ (lengthInCm >>> 32));
    result = 31 * result + (int) (heightInCm ^ (heightInCm >>> 32));
    result = 31 * result + (sex != null ? sex.hashCode() : 0);
    result = 31 * result + (neutered ? 1 : 0);
    return result;
  }

  public Identifier getName() {

    return name;
  }

  public void setName(Identifier name) {
    this.name = name;
  }

  private Identifier name;
  private String color;
  private int weighInKg;
  private long lengthInCm;
  private long heightInCm;
  private Sex sex;
  private boolean neutered;


  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int getWeighInKg() {
    return weighInKg;
  }

  public void setWeighInKg(int weighInKg) {
    this.weighInKg = weighInKg;
  }

  public long getLengthInCm() {
    return lengthInCm;
  }

  public void setLengthInCm(long lengthInCm) {
    this.lengthInCm = lengthInCm;
  }

  public long getHeightInCm() {
    return heightInCm;
  }

  public void setHeightInCm(long heightInCm) {
    this.heightInCm = heightInCm;
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }

  public boolean isNeutered() {
    return neutered;
  }

  public void setNeutered(boolean neutered) {
    this.neutered = neutered;
  }

}

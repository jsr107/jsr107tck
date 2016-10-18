/**
 *  Copyright (c) 2011-2016 Terracotta, Inc.
 *  Copyright (c) 2011-2016 Oracle and/or its affiliates.
 *
 *  All rights reserved. Use is subject to license terms.
 */
package javax.cache.expiry;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Functional tests for the {@link Duration} class.
 *
 * @author Brian Oliver
 */
public class DurationTest {

  /**
   * Ensure that we can create an eternal {@link Duration} using the default
   * constructor.
   */
  @Test
  public void shouldCreateEternalDurationUsingDefaultConstructor() {
    Duration duration = new Duration();

    assertThat(duration.isEternal(), is(true));
    assertThat(duration.isZero(), is(false));
  }

  /**
   * Ensure that we can create an eternal {@link Duration} using a
   * <code>null</code> {@link TimeUnit} and zero amount.
   */
  @Test
  public void shouldCreateEternalDurationWithNullTimeUnitAndAmount() {
    Duration duration = new Duration(null, 0);

    assertThat(duration.isEternal(), is(true));
    assertThat(duration.isZero(), is(false));
  }

  /**
   * Ensure that we can't create a {@link Duration} using a
   * <code>null</code> {@link TimeUnit}.
   */
  @Test(expected = NullPointerException.class)
  public void shouldNotCreateDurationWithNullTimeUnit() {
    new Duration(null, 1);
  }

  /**
   * Ensure that we can't create a {@link Duration} using a
   * negative duration amount
   */
  @Test(expected = IllegalArgumentException.class)
  public void shouldNotCreateDurationWithNegativeAmount() {
    new Duration(TimeUnit.MINUTES, -1);
  }

  /**
   * Ensure that we can't create a {@link Duration} using a
   * negative duration start range amount
   */
  @Test(expected = IllegalArgumentException.class)
  public void shouldNotCreateDurationWithNegativeStartRangeAmount() {
    new Duration(-1, 1);
  }

  /**
   * Ensure that we can't create a {@link Duration} using a
   * negative duration end range amount
   */
  @Test(expected = IllegalArgumentException.class)
  public void shouldNotCreateDurationWithNegativeEndRangeAmount() {
    new Duration(1, -1);
  }

  /**
   * Ensure that two {@link Duration}s with the same {@link TimeUnit}
   * and amount are equal and have the same hashcode.
   */
  @Test
  public void shouldBeEqualWithSameTimeUnitAndAmount() {
    Duration duration1 = new Duration(TimeUnit.DAYS, 2);
    Duration duration2 = new Duration(TimeUnit.DAYS, 2);

    assertThat(duration1.equals(duration2), is(true));
    assertThat(duration1.hashCode(), equalTo(duration2.hashCode()));
  }

  /**
   * Ensure that two {@link Duration}s with the same {@link TimeUnit}
   * but different amounts are not equal.
   */
  @Test
  public void shouldNotBeEqualWithSameTimeUnitAndDifferentAmount() {
    Duration duration1 = new Duration(TimeUnit.DAYS, 2);
    Duration duration2 = new Duration(TimeUnit.DAYS, 3);

    assertThat(duration1.equals(duration2), is(false));
  }

  /**
   * Ensure that two {@link Duration}s with the different {@link TimeUnit}s
   * and different amounts are not equal.
   */
  @Test
  public void shouldNotBeEqualWithDifferentTimeUnitAndAmount() {
    Duration duration1 = new Duration(TimeUnit.DAYS, 2);
    Duration duration2 = new Duration(TimeUnit.MINUTES, 2);

    assertThat(duration1.equals(duration2), is(false));
  }

  /**
   * Ensure that two {@link Duration}s are semantically equal even though
   * the time units are different.
   */
  @Test
  public void shouldCompareSemanticallyEquivalentDurationsWithDifferentDurations() {
    Duration duration1 = new Duration(TimeUnit.SECONDS, 120);
    Duration duration2 = new Duration(TimeUnit.MINUTES, 2);

    assertThat(duration1, equalTo(duration2));
    assertThat(duration1.hashCode(), equalTo(duration2.hashCode()));
  }

  /**
   * Ensure that a {@link Duration} of zero is created when a time range is empty.
   */
  @Test
  public void shouldCreateZeroDurationWithNoRange() {
    Duration duration = new Duration(0, 0);

    assertThat(duration.isZero(), is(true));
  }

  /**
   * Ensure that a {@link Duration} created using a range is of the expected
   * size.
   */
  @Test
  public void shouldCreateDurationWithRangeOfCorrectSize() {
    Duration duration = new Duration(0, 10);

    assertThat(duration.isZero(), is(false));
    assertThat(duration.getTimeUnit(), is(TimeUnit.MILLISECONDS));
    assertThat(duration.getDurationAmount(), is(10L));
  }

  /**
   * Ensure that all zero {@link Duration} are the same.
   */
  @Test
  public void shouldCompareZeroDurations() {
    //create a map of legal zero time unit durations
    HashMap<TimeUnit, Duration> durations = new HashMap<>();
    for (TimeUnit timeUnit : TimeUnit.values()) {
      if (timeUnit != TimeUnit.MICROSECONDS && timeUnit != TimeUnit.NANOSECONDS) {
        durations.put(timeUnit, new Duration(timeUnit, 0));
      }
    }

    //ensure that that compare against each other
    for (TimeUnit timeUnit : durations.keySet()) {
      for (Duration duration : durations.values()) {
        assertThat(duration, equalTo(durations.get(timeUnit)));
        assertThat(duration.isZero(), is(true));
        assertThat(duration.isEternal(), is(false));
      }
    }
  }

  /**
   * Ensure that we can create non-zero {@link Duration}.
   */
  @Test
  public void shouldCreateNonZeroDurations() {
    //create a map of non-zero durations for each time units
    HashMap<TimeUnit, Duration> durations = new HashMap<>();
    for (TimeUnit timeUnit : TimeUnit.values()) {
      if (timeUnit != TimeUnit.MICROSECONDS && timeUnit != TimeUnit.NANOSECONDS) {
        durations.put(timeUnit, new Duration(timeUnit, 42L));
      }
    }

    //ensure that that compare against each other
    for (TimeUnit timeUnit : durations.keySet()) {
      Duration duration = durations.get(timeUnit);
      assertThat(duration.getDurationAmount(), is(42L));
      assertThat(duration.getTimeUnit(), equalTo(timeUnit));
      assertThat(duration.isZero(), is(false));
      assertThat(duration.isEternal(), is(false));
    }
  }

  /**
   * Ensure that we can't create a nanosecond based {@link Duration}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void shouldNotCreateNanosecondBasedDuration() {
    new Duration(TimeUnit.NANOSECONDS, 0);
  }

  /**
   * Ensure that we can't create a microsecond based {@link Duration}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void shouldNotCreateMicrosecondBasedDuration() {
    new Duration(TimeUnit.MICROSECONDS, 0);
  }

  /**
   * Ensure that we can compare a {@link Duration} with <code>null</code>.
   */
  @Test
  public void shouldCompareDurationWithNull() {
    Duration duration = new Duration(TimeUnit.MINUTES, 42);
    assertThat(duration.equals(null), is(false));
  }

  /**
   * Ensure that we can compare a {@link Duration} with another type.
   */
  @Test
  public void shouldCompareDurationWithAnotherType() {
    Duration duration = new Duration(TimeUnit.MINUTES, 42);
    assertThat(duration.equals("Hello World"), is(false));
  }

  /**
   * Ensure that the statically declared constants are as expected.
   */
  @Test
  public void shouldDefineExpectedStaticConstantDurations() {
    assertThat(Duration.ONE_MINUTE.getTimeUnit(), is(TimeUnit.MINUTES));
    assertThat(Duration.ONE_MINUTE.getDurationAmount(), is(1L));

    assertThat(Duration.FIVE_MINUTES.getTimeUnit(), is(TimeUnit.MINUTES));
    assertThat(Duration.FIVE_MINUTES.getDurationAmount(), is(5L));

    assertThat(Duration.TEN_MINUTES.getTimeUnit(), is(TimeUnit.MINUTES));
    assertThat(Duration.TEN_MINUTES.getDurationAmount(), is(10L));

    assertThat(Duration.TWENTY_MINUTES.getTimeUnit(), is(TimeUnit.MINUTES));
    assertThat(Duration.TWENTY_MINUTES.getDurationAmount(), is(20L));

    assertThat(Duration.THIRTY_MINUTES.getTimeUnit(), is(TimeUnit.MINUTES));
    assertThat(Duration.THIRTY_MINUTES.getDurationAmount(), is(30L));

    assertThat(Duration.ONE_HOUR.getTimeUnit(), is(TimeUnit.HOURS));
    assertThat(Duration.ONE_HOUR.getDurationAmount(), is(1L));

    assertThat(Duration.ONE_DAY.getTimeUnit(), is(TimeUnit.DAYS));
    assertThat(Duration.ONE_DAY.getDurationAmount(), is(1L));
  }

  /**
   * Ensure that statically declared constant {@link Duration}s are different.
   */
  @Test
  public void shouldHaveDifferentValuesForStaticallyDeclaredDurations() throws IllegalAccessException {
    List<Duration> durations = getStaticallyDeclaredDurations();

    assertThat(durations.size(), is(not(0)));

    for (Duration a : durations) {
      for (Duration b : durations) {
        if (a != b) {
          assertThat(a.equals(b), is(false));
          assertThat(a.hashCode(), is(not(b.hashCode())));
        }
      }
    }
  }

  /**
   * Ensure that statically declared constant {@link Duration}s
   * can be serialized and deserialized.
   */
  @Test
  public void shouldSerializeAndDeserializeAllStaticallyDeclaredDurations()
      throws IllegalAccessException, IOException, ClassNotFoundException {

    List<Duration> durations = getStaticallyDeclaredDurations();

    //output the durations
    ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
    ObjectOutputStream oos = new ObjectOutputStream(bos);

    for(Duration duration : durations) {
      oos.writeObject(duration);
    }

    oos.close();

    //read and compare the durations
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);

    for(Duration duration : durations) {

      Object object = ois.readObject();
      assertThat(duration, equalTo(object));
    }

    ois.close();
  }

  @Test
  public void testDurationConstructor() {
    assertNotNull(new Duration(Long.MAX_VALUE, 0L));
    assertNotNull(new Duration(0L, Long.MAX_VALUE));
  }

  /**
   * Obtains a {@link List} of the statically declared constant
   * {@link Duration}s.
   *
   * @return  a list of {@link Duration}s
   */
  public List<Duration> getStaticallyDeclaredDurations() throws IllegalAccessException {
    //determine all of the statically declared Durations
    List<Duration> durations = new ArrayList<>();

    Class<Duration> durationClass = Duration.class;
    for(Field field : durationClass.getDeclaredFields()) {
      if (field.getType().isAssignableFrom(Duration.class) &&
          Modifier.isStatic(field.getModifiers()) &&
          Modifier.isPublic(field.getModifiers())) {
        durations.add((Duration)field.get(null));
      }
    }

    return durations;
  }
}

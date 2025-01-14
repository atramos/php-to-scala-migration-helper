/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.env.sample;

import java.util.concurrent.atomic.AtomicLong;

import com.caucho.env.sample.ActiveTimeSample;
import com.caucho.env.sample.Probe;
import com.caucho.util.Alarm;

public final class ActiveTimeProbe extends Probe implements ActiveTimeSample {
  private final double _scale;

  private final Object _lock = new Object();

  // sample data
  private final AtomicLong _activeCount = new AtomicLong();
  private final AtomicLong _activeCountMax = new AtomicLong();
  private final AtomicLong _totalCount = new AtomicLong();
  private final AtomicLong _sum = new AtomicLong();
  private final AtomicLong _max = new AtomicLong();
  private double _sumSquare;

  private long _lastTotalCount;

  private long _lastAvgTotalCount;
  private long _lastAvgSum;

  // for 95%
  private long _lastStdTotalCount;
  private double _lastStdSum;

  public ActiveTimeProbe(String name)
  {
    super(name);

    _scale = 1.0;
  }

  public final long start()
  {
    long startTime = Alarm.getCurrentTime();

    long activeCount = _activeCount.incrementAndGet();

    long max;

    while ((max = _activeCountMax.get()) < activeCount
           && ! _activeCountMax.compareAndSet(max, activeCount)) {
    }

    return startTime;
  }

  public final void end(long startTime)
  {
    _totalCount.incrementAndGet();
    _activeCount.decrementAndGet();

    long endTime = Alarm.getCurrentTime();

    long value = endTime - startTime;

    double sqValue = value * value;
    _sum.addAndGet(value);
    _sumSquare += sqValue;

    long max;
    while ((max = _max.get()) < value
           && ! _max.compareAndSet(max, value)) {
    }
  }

  public Probe createActiveCount(String name)
  {
    return new ActiveCountProbe(name);
  }

  public Probe createActiveCountMax(String name)
  {
    return new ActiveCountMaxProbe(name);
  }

  public Probe createTotalCount(String name)
  {
    return new TotalCountProbe(name);
  }

  public Probe createMax(String name)
  {
    return new MaxProbe(name);
  }

  public Probe createSigma(String name, int n)
  {
    return new SigmaProbe(name, n);
  }

  /**
   * Return the probe's next average.
   */
  public final double sample()
  {
    synchronized (_lock) {
      long count = _totalCount.get();
      long lastCount = _lastAvgTotalCount;
      _lastAvgTotalCount = count;

      long sum = _sum.get();
      double lastSum = _lastAvgSum;
      _lastAvgSum = sum;

      if (count == lastCount)
        return 0;
      else
        return _scale * (sum - lastSum) / (double) (count - lastCount);
    }
  }

  /**
   * Sample the active count
   */
  public final double sampleActiveCount()
  {
    return _activeCount.get();
  }

  /**
   * Sample the active count
   */
  public final double sampleActiveCountMax()
  {
    return _activeCountMax.getAndSet(_activeCount.get());
  }

  /**
   * Sample the total count
   */
  public final double sampleTotalCount()
  {
    long count = _totalCount.get();
    long lastCount = _lastTotalCount;
    _lastTotalCount = count;

    return count - lastCount;
  }

  /**
   * Return the probe's next 2-sigma
   */
  public final double sampleSigma(int n)
  {
    synchronized (_lock) {
      long count = _totalCount.get();
      long lastCount = _lastStdTotalCount;
      _lastStdTotalCount = count;

      double sum = _sum.get();
      double lastSum = _lastStdSum;
      _lastStdSum = sum;

      double sumSquare = _sumSquare;
      _sumSquare = 0;

      if (count == lastCount)
        return 0;

      double avg = (sum - lastSum) / (count - lastCount);
      double part = (count - lastCount) * sumSquare - sum * sum;

      if (part < 0)
        part = 0;

      double std = Math.sqrt(part) / (count - lastCount);

      return _scale * (avg + n * std);
    }
  }

  /**
   * Return the probe's next sample.
   */
  public final double sampleMax()
  {
    long max = _max.getAndSet(0);

    return _scale * max;
  }

  class ActiveCountProbe extends Probe {
    ActiveCountProbe(String name)
    {
      super(name);
    }

    public double sample()
    {
      return sampleActiveCount();
    }
  }

  class ActiveCountMaxProbe extends Probe {
    ActiveCountMaxProbe(String name)
    {
      super(name);
    }

    public double sample()
    {
      return sampleActiveCountMax();
    }
  }

  class TotalCountProbe extends Probe {
    TotalCountProbe(String name)
    {
      super(name);
    }

    public double sample()
    {
      return sampleTotalCount();
    }
  }

  class MaxProbe extends Probe {
    MaxProbe(String name)
    {
      super(name);
    }

    public double sample()
    {
      return sampleMax();
    }
  }

  class SigmaProbe extends Probe {
    private final int _n;

    SigmaProbe(String name, int n)
    {
      super(name);

      _n = n;
    }

    public double sample()
    {
      return sampleSigma(_n);
    }
  }
}

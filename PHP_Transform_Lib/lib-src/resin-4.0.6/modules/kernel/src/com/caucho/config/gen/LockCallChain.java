/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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
 * @author Reza Rahman
 */
package com.caucho.config.gen;

import static javax.ejb.ConcurrencyManagementType.CONTAINER;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Lock;
import javax.ejb.LockType;

import com.caucho.config.Configurable;
import com.caucho.config.types.Period;
import com.caucho.java.JavaWriter;
import com.caucho.util.L10N;

/**
 * Represents EJB lock type specification interception. The specification gears
 * it towards EJB singletons, but it can be used for other bean types.
 */
public class LockCallChain extends AbstractCallChain {
  private static final int DEFAULT_TIMEOUT = 10000;

  private EjbCallChain _next;

  private boolean _isContainerManaged;
  private LockType _lockType;
  private long _lockTimeout;
  private TimeUnit _lockTimeoutUnit;

  public LockCallChain(BusinessMethodGenerator businessMethod, EjbCallChain next)
  {
    super(next);

    _next = next;

    _isContainerManaged = true;
    _lockType = null;
    _lockTimeout = DEFAULT_TIMEOUT;
    _lockTimeoutUnit = TimeUnit.MILLISECONDS;
  }

  /**
   * Sets the lock timeout.
   *
   * @param timeout
   *          The timeout period.
   */
  @Configurable
  public void setTimeout(Period timeout)
  {
    _lockTimeout = timeout.getPeriod();
  }

  /**
   * Returns true if the business method has a lock annotation.
   */
  @Override
  public boolean isEnhanced()
  {
    return (_isContainerManaged && (_lockType != null));
  }

  /**
   * Introspects the method for locking attributes.
   */
  @Override
  public void introspect(ApiMethod apiMethod, ApiMethod implementationMethod)
  {
    ApiClass apiClass = apiMethod.getDeclaringClass();

    ConcurrencyManagement concurrencyManagementAnnotation
      = apiClass.getAnnotation(ConcurrencyManagement.class);

    if ((concurrencyManagementAnnotation != null)
        && (concurrencyManagementAnnotation.value() != CONTAINER)) {
      _isContainerManaged = false;
      return;
    }

    ApiClass implementationClass = null;

    if (implementationMethod != null) {
      implementationClass = implementationMethod.getDeclaringClass();
    }

    Lock lockAttribute = getAnnotation(Lock.class, apiMethod, apiClass,
        implementationMethod, implementationClass);

    if (lockAttribute != null) {
      _lockType = lockAttribute.value();
    }

    AccessTimeout accessTimeoutAttribute
      = getAnnotation(AccessTimeout.class,
                      apiMethod, apiClass,
                      implementationMethod, implementationClass);

    if (accessTimeoutAttribute != null) {
      _lockTimeout = accessTimeoutAttribute.timeout();
      _lockTimeoutUnit = accessTimeoutAttribute.unit();
    }
  }

  /**
   * Generates the class prologue.
   */
  @Override
  public void generateMethodPrologue(JavaWriter out,
                                     HashMap<String,Object> map)
    throws IOException
  {
    if ((_isContainerManaged && (_lockType != null))
        && (map.get("caucho.ejb.lock") == null)) {
      map.put("caucho.ejb.lock", "done");

      out.println();
      out.println("private transient final java.util.concurrent.locks.ReentrantReadWriteLock _readWriteLock = new java.util.concurrent.locks.ReentrantReadWriteLock();");
    }

    _next.generateMethodPrologue(out, map);
  }

  /**
   * Generates the method interception code.
   */
  @Override
  public void generatePreTry(JavaWriter out) throws IOException
  {
    // TODO Is this too much code to be in-lined?
    if (_isContainerManaged && (_lockType != null)) {
      out.println();

      switch (_lockType) {
      case READ:
        if (_lockTimeout != -1) {
          out.println("com.caucho.config.util.LockUtil.lockRead("
                      + "_readWriteLock.readLock(),"
                      + _lockTimeoutUnit.toMillis(_lockTimeout)
                      + ");");
        } else {
          out.println("_readWriteLock.readLock().lock();");
        }
        break;

      case WRITE:
        if (_lockTimeout != -1) {
          out.println("com.caucho.config.util.LockUtil.lockWrite("
                      + "_readWriteLock,"
                      + _lockTimeoutUnit.toMillis(_lockTimeout)
                      + ");");
        } else {
          out.println("com.caucho.config.util.LockUtil.lockWrite("
                      + "_readWriteLock);");
        }
        break;
      }
    }

    super.generatePreTry(out);
  }

  /**
   * Generates the method interception code.
   */
  @Override
  public void generateFinally(JavaWriter out) throws IOException
  {
    super.generateFinally(out);

    if (_isContainerManaged && (_lockType != null)) {
      switch (_lockType) {
      case READ:
        out.println();
        out.println("_readWriteLock.readLock().unlock();");

        break;
      case WRITE:
        out.println();
        out.println("_readWriteLock.writeLock().unlock();");
        break;
      }
    }
  }

  protected void generateNext(JavaWriter out) throws IOException
  {
    _next.generateCall(out);
  }
}
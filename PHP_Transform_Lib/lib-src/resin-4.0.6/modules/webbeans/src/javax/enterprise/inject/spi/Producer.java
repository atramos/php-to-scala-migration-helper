/*
 * Copyright (c) 1998-2007 Caucho Technology -- all rights reserved
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
 * @author Scott Ferguson
 */

package javax.enterprise.inject.spi;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;

/**
 * Abstract introspected view of a Bean injectible field
 */
public interface Producer<T>
{
  /**
   * Instantiate the bean.
   */
  public T produce(CreationalContext<T> ctx);

  /**
   * Dispose the bean.
   */
  public void dispose(T instance);

  /**
   * Returns the injection points.
   */
  public Set<InjectionPoint> getInjectionPoints();
}

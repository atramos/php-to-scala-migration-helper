/*
 * Copyright (c) 1998-2009 Caucho Technology -- all rights reserved
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

package com.caucho.config.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.Member;
import java.util.Set;
import java.util.HashSet;
import javax.enterprise.inject.spi.*;
import javax.inject.Named;
import javax.inject.Qualifier;

/**
 */
public class InjectionPointImpl<T> implements InjectionPoint
{
  private final InjectManager _manager;
  
  private final Bean<T> _bean;
  private final Annotated _annotated;
  private final Member _member;
  private final HashSet<Annotation> _bindings = new HashSet<Annotation>();

  InjectionPointImpl(InjectManager manager,
                     Bean<T> bean,
                     AnnotatedField<T> field)
  {
    this(manager, bean, field, field.getJavaMember());
  }

  InjectionPointImpl(InjectManager manager,
                     Bean<T> bean,
                     AnnotatedParameter<?> param)
  {
    this(manager, bean, param, param.getDeclaringCallable().getJavaMember());
  }

  InjectionPointImpl(InjectManager manager,
                     Bean<T> bean,
                     Annotated annotated,
                     Member member)
  {
    _manager = manager;
    _bean = bean;
    _annotated = annotated;
    _member = member;
    
    boolean isQualifier = false;

    for (Annotation ann : annotated.getAnnotations()) {
      if (_manager.isQualifier(ann.annotationType())) {
        _bindings.add(ann);
        
        // ioc/5006
        /*
        if (! Named.class.equals(ann.annotationType()))
          isQualifier = true;
          */
        isQualifier = true;
      }
    }

    if (! isQualifier) {
      _bindings.add(DefaultLiteral.DEFAULT);
    }
  }

  /**
   * Returns the declared type of the injection point, e.g. an
   * injected field's type.
   */
  public Type getType()
  {
    return _annotated.getBaseType();
  }

  /**
   * Returns the declared bindings on the injection point.
   */
  public Set<Annotation> getQualifiers()
  {
    return _bindings;
  }

  /**
   * Returns the owning bean for the injection point.
   */
  public Bean<?> getBean()
  {
    return _bean;
  }

  /**
   * Returns the Field for field injection, the Method for method injection,
   * and Constructor for constructor injection.
   */
  public Member getMember()
  {
    return _member;
  }

  /**
   * Returns all annotations on the injection point.
   */
  public Annotated getAnnotated()
  {
    return _annotated;
  }

  public boolean isDelegate()
  {
    return false;
  }

  public boolean isTransient()
  {
    return false;
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[" + getMember() + "]";
  }
}

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
 * @author Scott Ferguson
 */

package com.caucho.config.inject;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Scope;

import com.caucho.config.ConfigException;
import com.caucho.config.Names;
import com.caucho.config.reflect.BaseType;
import com.caucho.inject.Module;
import com.caucho.util.L10N;

/**
 * Common bean introspection for Produces and ManagedBean.
 */
//  implements ObjectProxy
@Module
public class AbstractIntrospectedBean<T> extends AbstractBean<T>
  implements PassivationCapable, PassivationSetter
{
  private static final L10N L = new L10N(AbstractIntrospectedBean.class);
  private static final Logger log
    = Logger.getLogger(AbstractIntrospectedBean.class.getName());

  private static final HashSet<Class<?>> _reservedTypes
    = new HashSet<Class<?>>();

  public static final Annotation []CURRENT_ANN
    = new Annotation[] { CurrentLiteral.CURRENT };

  // AnnotatedType for ManagedBean, AnnotatedMethod for produces
  private Annotated _annotated;

  private BaseType _baseType;

  private Set<BaseType> _types;

  private Set<Type> _typeClasses;

  private ArrayList<Annotation> _qualifiers
    = new ArrayList<Annotation>();

  private Class<? extends Annotation> _scope;

  private ArrayList<Annotation> _stereotypes
    = new ArrayList<Annotation>();

  private String _name;

  private String _passivationId;

  public AbstractIntrospectedBean(InjectManager manager,
                                  Type type,
                                  Annotated annotated)
  {
    super(manager);
    _annotated = annotated;
    
    if (type instanceof Class<?>) {
      // ioc/024d
      _baseType = manager.createClassBaseType((Class<?>) type);
    }
    else
      _baseType = manager.createBaseType(type);
    
    _typeClasses = _baseType.getTypeClosure(manager);
  }

  public BaseType getBaseType()
  {
    return _baseType;
  }

  public Class<?> getBeanClass()
  {
    return _baseType.getRawClass();
  }

  public Type getTargetType()
  {
    return _baseType.toType();
  }

  public String getTargetSimpleName()
  {
    return _baseType.getSimpleName();
  }

  public String getTargetName()
  {
    return _baseType.toString();
  }

  public Class<?> getTargetClass()
  {
    return _baseType.getRawClass();
  }

  @Override
  public Annotated getAnnotated()
  {
    return _annotated;
  }

  /*
  protected AnnotatedType getAnnotatedType()
  {
    return  new BeanTypeImpl(getTargetType(), getIntrospectionClass());
  }
  */

  protected Class<?> getIntrospectionClass()
  {
    return getTargetClass();
  }

  /**
   * Gets the bean's EL qualifier name.
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Returns the bean's qualifier types
   */
  public Set<Annotation> getQualifiers()
  {
    Set<Annotation> set = new LinkedHashSet<Annotation>();

    for (Annotation qualifier : _qualifiers) {
      set.add(qualifier);
    }

    return set;
  }

  public String getId()
  {
    if (_passivationId == null)
      _passivationId = calculatePassivationId();

    return _passivationId;
  }

  public void setPassivationId(String passivationId)
  {
    _passivationId = passivationId;
  }

  /**
   * Returns the bean's stereotypes
   */
  public Set<Class<? extends Annotation>> getStereotypes()
  {
    Set<Class<? extends Annotation>> set
      = new LinkedHashSet<Class<? extends Annotation>>();

    for (Annotation stereotype : _stereotypes) {
      set.add(stereotype.annotationType());
    }

    return set;
  }

  /**
   * Returns an array of the qualifier annotations
   */
  public Annotation []getQualifierArray()
  {
    if (_qualifiers == null || _qualifiers.size() == 0)
      return new Annotation[] { CurrentLiteral.CURRENT };

    Annotation []qualifiers = new Annotation[_qualifiers.size()];
    _qualifiers.toArray(qualifiers);

    return qualifiers;
  }

  /**
   * Returns the scope
   */
  @Override
  public Class<? extends Annotation> getScope()
  {
    return _scope;
  }

  /**
   * Returns the types that the bean implements
   */
  public Set<Type> getTypes()
  {
    return _typeClasses;
  }

  /**
   * Returns the types that the bean implements
   */
  public Set<BaseType> getGenericTypes()
  {
    return _types;
  }

  /**
   * Introspects all the types implemented by the class
   */
  protected void introspectTypes(Type type)
  {
    introspectTypes(type, null);
  }

  /**
   * Introspects all the types implemented by the class
   */
  private void introspectTypes(Type type, HashMap<String,BaseType> paramMap)
  {
    if (type == null || _reservedTypes.contains(type))
      return;
    
    BaseType baseType = addType(type, paramMap);

    if (baseType == null)
      return;

    HashMap<String,BaseType> newParamMap = baseType.getParamMap();
    Class<?> cl = baseType.getRawClass();

    introspectTypes(cl.getGenericSuperclass(), newParamMap);

    for (Type iface : cl.getGenericInterfaces()) {
      introspectTypes(iface, newParamMap);
    }
  }

  protected BaseType addType(Type type, HashMap<String,BaseType> paramMap)
  {
    BaseType baseType = BaseType.create(type, paramMap);

    if (baseType == null)
      return null;

    if (_types.contains(baseType))
      return null;

    _types.add(baseType);

    /*
    if (! _typeClasses.contains(baseType.getRawClass()))
      _typeClasses.add(baseType.getRawClass());
    */
    if (! _typeClasses.contains(baseType.toType()))
      _typeClasses.add(baseType.toType());

    return baseType;
  }

  public void introspect()
  {
    super.introspect();

    introspect(getIntrospectedAnnotated());
  }

  protected Annotated getIntrospectedAnnotated()
  {
    return _annotated;
  }

  protected void introspect(Annotated annotated)
  {
    introspectScope(annotated);
    introspectQualifiers(annotated);
    introspectName(annotated);
    introspectStereotypes(annotated);

    introspectDefault();
  }

  /**
   * Called for implicit introspection.
   */
  protected void introspectScope(Annotated annotated)
  {
    if (_scope != null)
      return;

    BeanManager inject = getBeanManager();

    for (Annotation ann : annotated.getAnnotations()) {
      if (inject.isScope(ann.annotationType())) {
        if (_scope != null && _scope != ann.annotationType())
          throw new ConfigException(L.l("{0}: @Scope annotation @{1} conflicts with @{2}.  Java Injection components may only have a single @Scope.",
                                        getTargetName(),
                                        _scope.getName(),
                                        ann.annotationType().getName()));

        _scope = ann.annotationType();
      }
    }
  }

  /**
   * Introspects the qualifier annotations
   */
  protected void introspectQualifiers(Annotated annotated)
  {
    if (_qualifiers.size() > 0)
      return;

    BeanManager inject = getBeanManager();

    for (Annotation ann : annotated.getAnnotations()) {
      if (inject.isQualifier(ann.annotationType())) {
        if (ann.annotationType().equals(Named.class)) {
          String namedValue = getNamedValue(ann);

          if ("".equals(namedValue)) {
            ann = Names.create(getDefaultName());
          }
        }

        _qualifiers.add(ann);
      }
    }
  }

  /**
   * Introspects the qualifier annotations
   */
  protected void introspectName(Annotated annotated)
  {
    if (_name != null)
      return;

    Annotation ann = annotated.getAnnotation(Named.class);

    if (ann != null) {
      String value = getNamedValue(ann);

      if (value == null)
        value = "";

      _name = value;
    }
  }

  /**
   * Adds the stereotypes from the bean's annotations
   */
  protected void introspectStereotypes(Annotated annotated)
  {
    Class<? extends Annotation> scope = null;

    for (Annotation stereotype : annotated.getAnnotations()) {
      Class<?> stereotypeType = stereotype.annotationType();

      if (stereotypeType.isAnnotationPresent(Stereotype.class))
        _stereotypes.add(stereotype);

      for (Annotation ann : stereotypeType.getDeclaredAnnotations()) {
        Class<? extends Annotation> annType = ann.annotationType();

        if (annType.isAnnotationPresent(Scope.class)
            || annType.isAnnotationPresent(NormalScope.class)) {
          if (_scope == null && scope != null && ! scope.equals(annType)) {
            throw new ConfigException(L.l("'{0}' is an invalid @Scope because a scope '{1}' has already been defined.  Only one @Scope or @NormalScope is allowed on a bean.",
                                          scope.getName(), annType.getName()));
          }

          scope = annType;
        }

        if (annType.equals(Named.class) && _name == null) {
          String namedValue = getNamedValue(ann);
          _name = "";

          if (! "".equals(namedValue))
            throw new ConfigException(L.l("@Named must not have a value in a @Stereotype definition, because @Stereotypes are used with multiple beans."));
        }

        if (annType.isAnnotationPresent(Qualifier.class)
            && ! annType.equals(Named.class)) {
          throw new ConfigException(L.l("'{0}' is not allowed on @Stereotype '{1}' because stereotypes may not have @Qualifier annotations",
                                        ann, stereotype));
        }
      }
    }

    if (_scope == null)
      _scope = scope;
}

  protected void introspectDefault()
  {
    // if (_qualifiers.size() == 0)
    
    // _qualifiers.add(AnyLiteral.ANY);
    boolean isQualifier = false;
    for (Annotation ann : _qualifiers) {
      if (! Named.class.equals(ann.annotationType())
          && ! Any.class.equals(ann.annotationType())) {
        isQualifier = true;
      }
    }
    
    if (! isQualifier)
      _qualifiers.add(DefaultLiteral.DEFAULT);
      
    _qualifiers.add(AnyLiteral.ANY);

    if (_scope == null)
      _scope = Dependent.class;

    if ("".equals(_name))
      _name = getDefaultName();
  }

  protected String getDefaultName()
  {
    String name = getTargetSimpleName();
    
    if ("".equals(name)) {
      log.info("TYPE:" + name + " " + this + " " + _baseType);
    }

    return Character.toLowerCase(name.charAt(0)) + name.substring(1);
  }

  protected void bind()
  {
  }

  /**
   * Returns true if the bean can be null
   */
  public boolean isNullable()
  {
    return false;
  }

  /**
   * Returns true if the bean is serializable
   */
  public boolean isPassivationCapable()
  {
    return Serializable.class.isAssignableFrom(getTargetClass());
  }

  /**
   * Instantiate the bean.
   */
  public T create(CreationalContext<T> env)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  /**
   * Call destroy
   */
  @Override
  public void destroy(T instance, CreationalContext<T> env)
  {
  }

  /**
   * Call destroy
   */
  /*
  public void destroy(T instance)
  {
  }
  */

  /**
   * Inject the bean.
   */
/*
  public void inject(T instance)
  {
  }
*/

  /**
   * Call post-construct
   */
/*
  public void postConstruct(T instance)
  {
  }
*/

  /**
   * Call pre-destroy
   */
/*
  public void preDestroy(T instance)
  {
  }
*/

  public void dispose(T instance)
  {
  }

  /**
   * Returns the set of injection points, for validation.
   */
  public Set<InjectionPoint> getInjectionPoints()
  {
    return new HashSet<InjectionPoint>();
  }

  protected String getNamedValue(Annotation ann)
  {
    try {
      if (ann instanceof Named) {
        return ((Named) ann).value();
      }

      Method method = ann.getClass().getMethod("value");
      method.setAccessible(true);

      return (String) method.invoke(ann);
    } catch (NoSuchMethodException e) {
      // ioc/0m04
      log.log(Level.FINE, e.toString(), e);

      return "";
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String toDebugString()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(getTargetSimpleName());
    sb.append("[");

    if (_name != null) {
      sb.append("name=");
      sb.append(_name);
    }

    for (Annotation qualifier : _qualifiers) {
      sb.append(",");
      sb.append(qualifier);
    }

    if (_scope != null && _scope != Dependent.class) {
      sb.append(", @");
      sb.append(_scope.getSimpleName());
    }

    sb.append("]");

    return sb.toString();
  }

  static class MethodNameComparator implements Comparator<AnnotatedMethod<?>> {
    public int compare(AnnotatedMethod<?> a, AnnotatedMethod<?> b)
    {
      return a.getJavaMember().getName().compareTo(b.getJavaMember().getName());
    }
  }

  static class AnnotationComparator implements Comparator<Annotation> {
    public int compare(Annotation a, Annotation b)
    {
      Class<?> annTypeA = a.annotationType();
      Class<?> annTypeB = b.annotationType();

      return annTypeA.getName().compareTo(annTypeB.getName());
    }
  }

  static {
    _reservedTypes.add(java.io.Closeable.class);
    _reservedTypes.add(java.io.Serializable.class);
    _reservedTypes.add(Cloneable.class);
    _reservedTypes.add(Object.class);
    _reservedTypes.add(Comparable.class);

    Method namedValueMethod = null;

    try {
      namedValueMethod = Named.class.getMethod("value");
      namedValueMethod.setAccessible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

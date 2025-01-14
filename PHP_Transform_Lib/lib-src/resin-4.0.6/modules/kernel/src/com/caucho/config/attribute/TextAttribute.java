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

package com.caucho.config.attribute;

import java.lang.reflect.*;

import com.caucho.config.*;
import com.caucho.config.type.*;
import com.caucho.util.L10N;
import com.caucho.xml.QName;

public class TextAttribute extends Attribute {
  private final Method _setter;
  private final Class _type;
  private ConfigType _configType;

  public TextAttribute(Method setter, Class type)
  {
    _setter = setter;
    _type = type;
  }
  
  /**
   * Returns the config type of the attribute value.
   */
  public ConfigType getConfigType()
  {
    if (_configType == null)
      _configType = TypeFactory.getType(_type);
    
    return _configType;
  }
  
  /**
   * Sets the value of the attribute
   */
  @Override
  public void setText(Object bean, QName name, String value)
    throws ConfigException
  {
    try {
      _setter.invoke(bean, getConfigType().valueOf(value));
    } catch (Exception e) {
      throw ConfigException.create(_setter, e);
    }
  }
  
  /**
   * Sets the value of the attribute
   */
  @Override
  public void setValue(Object bean, QName name, Object value)
    throws ConfigException
  {
    try {
      _setter.invoke(bean, value);
    } catch (Exception e) {
      throw ConfigException.create(_setter, e);
    }
  }

  /**
   * Creates the child bean.
   */
  @Override
  public Object create(Object parent, QName name)
    throws ConfigException
  {
    return null;
  }
}

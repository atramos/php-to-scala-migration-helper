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

package com.caucho.amber.type;

import com.caucho.amber.AmberRuntimeException;
import com.caucho.amber.entity.Embeddable;
import com.caucho.amber.manager.AmberPersistenceUnit;
import com.caucho.amber.table.AmberTable;
import com.caucho.amber.field.*;
import com.caucho.amber.gen.*;
import com.caucho.java.*;
import com.caucho.java.gen.ClassComponent;
import com.caucho.util.L10N;

import java.sql.SQLException;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Represents an element-map value
 */
public class ElementType extends BeanType {
  private static final Logger log
    = Logger.getLogger(ElementType.class.getName());
  private static final L10N L = new L10N(ElementType.class);

  private AmberField _valueField;

  public ElementType(AmberPersistenceUnit amberPersistenceUnit,
		     AmberField valueField)
  {
    super(amberPersistenceUnit);

    _valueField = valueField;
  }

  /**
   * Returns the element's value field.
   */
  public AmberField getField()
  {
    return _valueField;
  }

  /**
   * Generates the select clause for a load.
   */
  public String generateLoadSelect(AmberTable table, String id)
  {
    return _valueField.generateLoadSelect(table, id);
  }
}

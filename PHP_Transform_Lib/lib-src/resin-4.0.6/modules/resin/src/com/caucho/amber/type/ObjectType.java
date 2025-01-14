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
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.amber.type;

import com.caucho.java.JavaWriter;
import com.caucho.util.L10N;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The type of a property.
 */
public class ObjectType extends AmberType {
  private static final L10N L = new L10N(ObjectType.class);

  private static final ObjectType OBJECT_TYPE = new ObjectType();

  private ObjectType()
  {
  }

  /**
   * Returns the object type.
   */
  public static ObjectType create()
  {
    return OBJECT_TYPE;
  }

  /**
   * Returns the type name.
   */
  public String getName()
  {
    return "java.lang.Object";
  }

  /**
   * Generates a string to load the property.
   */
  public int generateLoad(JavaWriter out, String rs,
			  String indexVar, int index)
    throws IOException
  {
    out.print(rs + ".getObject(" + indexVar + " + " + index + ")");

    return index + 1;
  }

  /**
   * Generates a string to set the property.
   */
  public void generateSet(JavaWriter out, String pstmt,
			  String index, String value)
    throws IOException
  {
    out.println(pstmt + ".setObject(" + index + "++, " + value + ");");
  }

  /**
   * Sets the value.
   */
  public void setParameter(PreparedStatement pstmt, int index, Object value)
    throws SQLException
  {
    pstmt.setObject(index, value);
  }

  /**
   * Gets the value.
   */
  public Object getObject(ResultSet rs, int index)
    throws SQLException
  {
    return rs.getObject(index);
  }
}

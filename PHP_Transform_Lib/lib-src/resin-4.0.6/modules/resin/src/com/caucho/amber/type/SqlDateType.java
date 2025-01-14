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

import com.caucho.amber.manager.AmberPersistenceUnit;
import com.caucho.java.JavaWriter;
import com.caucho.util.L10N;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * The type of a property.
 */
public class SqlDateType extends AmberType {
  private static final L10N L = new L10N(SqlDateType.class);

  private static final SqlDateType SQL_DATE_TYPE = new SqlDateType();

  private SqlDateType()
  {
  }

  /**
   * Returns the singleton SqlDate type.
   */
  public static SqlDateType create()
  {
    return SQL_DATE_TYPE;
  }

  /**
   * Returns the type name.
   */
  public String getName()
  {
    return "java.sql.Date";
  }

  /**
   * Returns true if the value is assignable to the Java type.
   */
  @Override
  public boolean isAssignableTo(Class javaType)
  {
    return javaType.isAssignableFrom(java.sql.Date.class);
  }

  /**
   * Generates the type for the table.
   */
  @Override
  public String generateCreateColumnSQL(AmberPersistenceUnit manager, int length, int precision, int scale)
  {
    return manager.getCreateColumnSQL(Types.DATE, length, precision, scale);
  }

  /**
   * Generates a string to load the property.
   */
  @Override
  public int generateLoad(JavaWriter out, String rs,
			  String indexVar, int index)
    throws IOException
  {
    out.print(rs + ".getDate(" + indexVar + " + " + index + ")");

    return index + 1;
  }

  /**
   * Generates a string to load the property.
   */
  @Override
  public int generateLoadNative(JavaWriter out, int index)
    throws IOException
  {
    out.print("rs.getDate(columnNames[" + index + "])");

    return index + 1;
  }

  /**
   * Generates a string to set the property.
   */
  @Override
  public void generateSet(JavaWriter out, String pstmt,
			  String index, String value)
    throws IOException
  {
    out.println("if (" + value + " == null)");
    out.println("  " + pstmt + ".setNull(" + index + "++, java.sql.Types.DATE);");
    out.println("else");
    out.println("  " + pstmt + ".setDate(" + index + "++, " + value + ");");
  }

  /**
   * Sets the value.
   */
  @Override
  public void setParameter(PreparedStatement pstmt, int index, Object value)
    throws SQLException
  {
    pstmt.setDate(index, (Date) value);
  }

  /**
   * Gets the value.
   */
  @Override
  public Object getObject(ResultSet rs, int index)
    throws SQLException
  {
    return rs.getDate(index);
  }
}

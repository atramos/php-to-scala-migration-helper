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

package com.caucho.amber.field;

import com.caucho.bytecode.JClass;
import com.caucho.bytecode.JMethod;
import com.caucho.config.ConfigException;
import com.caucho.java.JavaWriter;
import com.caucho.util.L10N;
import com.caucho.util.Log;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * The stub method is needed in particular to support EJB/CMP and ejbSelect
 */
public class StubMethod {
  private static final L10N L = new L10N(StubMethod.class);
  protected static final Logger log = Log.open(StubMethod.class);

  private JMethod _method;

  public StubMethod(JMethod method)
  {
    _method = method;
  }

  /**
   * Initializes the method.
   */
  public void init()
    throws ConfigException
  {
  }

  /**
   * Generates the stub
   */
  public void generate(JavaWriter out)
    throws IOException
  {
    out.println();
    out.print("public ");
    out.print(_method.getReturnType().getPrintName());
    out.print(" " + _method.getName() + "(");

    JClass []paramTypes = _method.getParameterTypes();
    for (int i = 0; i < paramTypes.length; i++) {
      if (i != 0)
	out.print(", ");

      out.print(paramTypes[i].getPrintName());
      out.print(" a" + i);
    }
    out.println(")");

    JClass []exnTypes = _method.getExceptionTypes();
    for (int i = 0; i < exnTypes.length; i++) {
      if (i == 0)
	out.print("  throws ");
      else
	out.print(", ");

      out.print(exnTypes[i].getPrintName());
    }
    if (exnTypes.length > 0)
      out.println();
    
    out.println("{");
    JClass retType = _method.getReturnType();

    if ("void".equals(retType.getName())) {
    }
    else if (retType.isPrimitive())
      out.println("  return 0;");
    else
      out.println("  return null;");
    
    out.println("}");
  }
}

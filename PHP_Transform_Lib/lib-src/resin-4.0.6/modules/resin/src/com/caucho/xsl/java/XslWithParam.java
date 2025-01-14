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
 *   Free SoftwareFoundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.xsl.java;

import com.caucho.java.JavaWriter;
import com.caucho.xml.QName;
import com.caucho.xsl.XslParseException;

/**
 * Represents an xsl:with-param node from the stylesheet.
 */
public class XslWithParam extends XslNode {
  private String _name;
  private String _select;

  /**
   * returns the tag name.
   */
  public String getTagName()
  {
    return "xsl:with-param";
  }
  
  /**
   * Adds an attribute.
   */
  public void addAttribute(QName name, String value)
    throws XslParseException
  {
    if (name.getName().equals("name"))
      _name = value;
    else if (name.getName().equals("select"))
      _select = value;
    else
      super.addAttribute(name, value);
  }

  /**
   * Ends the attributes.
   */
  public void endAttributes()
    throws XslParseException
  {
    if (_name == null)
      throw error(L.l("xsl:with-param needs a 'name' attribute."));
  }

  /**
   * Ends the element.
   */
  public void endElement()
  {
    addVariableCount();
  }

  /**
   * Generates the code for the tag
   *
   * @param out the output writer for the generated java.
   */
  public void generate(JavaWriter out)
    throws Exception
  {
    if (_select != null) {
      out.print("_exprs[" + addExpr(_select) + "]");
      out.println(".addVar(_xsl_arg" + _gen.getCallDepth() + ", \"" +
		  _name + "\", " +
		  "node, env);");
    }
    else {
      String frag = "_xsl_frag_" + _gen.generateId();

      out.println("Node " + frag + ";");
      
      printFragmentValue(out, frag);
      
      out.print("_xsl_arg" + _gen.getCallDepth() + ".addVar(\"");
      out.print(_name);
      out.println("\", " + frag + ");");
    }
  }
}

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
 * Represents the xtp:directive.page node from the stylesheet.
 */
public class XtpDirectiveCache extends XslNode implements XslTopNode {
  /**
   * Returns the tag name.
   */
  public String getTagName()
  {
    return "xtp:directive.cache";
  }

  /**
   * Adds an attribute.
   */
  public void addAttribute(QName name, String value)
    throws XslParseException
  {
  }

  /**
   * Adds a child node.
   */
  public void addChild(XslNode node)
    throws XslParseException
  {
    if (node == null)
      return;

    if (node instanceof TextNode) {
    }
    else
      throw error(L.l("'{0}' is not allowed as an xtp:directive.page child.",
		      node));
  }

  /**
   * Generates the prelude code for the tag
   *
   * @param out the output writer for the generated java.
   */
  public void generateDeclaration(JavaWriter out)
    throws Exception
  {
    // out.print(_text);
  }

  /**
   * Generates the code for the tag
   *
   * @param out the output writer for the generated java.
   */
  public void generate(JavaWriter out)
    throws Exception
  {
  }
}

/*
 * Copyright (c) 1998-1999 Caucho Technology -- all rights reserved
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
 *
 * $Id: HtmlEcmaWrap.java,v 1.1 2010-04-18 01:14:44 alex Exp $
 */

package com.caucho.eswrap.com.caucho.xml;

import com.caucho.vfs.Path;
import com.caucho.vfs.ReadStream;
import com.caucho.vfs.StringStream;
import com.caucho.xml.Html;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public class HtmlEcmaWrap {
  public static Document parse(InputStream is)
    throws IOException, SAXException
  {
    if (is instanceof ReadStream)
      return new Html().parseDocument((ReadStream) is);
    else
      return new Html().parseDocument(is);
  }
  
  public static Document parseString(String s)
    throws IOException, SAXException
  {
    return new Html().parseDocument(StringStream.open(s));
  }
  
  public static Document parseFile(Path path)
    throws IOException, SAXException
  {
    return new Html().parseDocument(path);
  }
}

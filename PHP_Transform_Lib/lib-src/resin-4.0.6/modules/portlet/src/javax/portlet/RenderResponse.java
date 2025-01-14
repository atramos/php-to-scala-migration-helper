/*
 * Copyright (c) 1998-2004 Caucho Technology -- all rights reserved
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
 * @author Sam 
 */


package javax.portlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

public interface RenderResponse extends PortletResponse
{
  public static final String EXPIRATION_CACHE = "portlet.expiration-cache";

  public String getContentType();

  public PortletURL createRenderURL();

  public PortletURL createActionURL();

  public String getNamespace();

  public void setTitle(String title);

  public void setContentType(String type);

  public String getCharacterEncoding();

  public PrintWriter getWriter() 
    throws IOException;

  public Locale getLocale();

  public void setBufferSize(int size);

  public int getBufferSize();

  public void flushBuffer() 
    throws IOException;

  public void resetBuffer();

  public boolean isCommitted();

  public void reset();

  public OutputStream getPortletOutputStream() 
    throws IOException;
}


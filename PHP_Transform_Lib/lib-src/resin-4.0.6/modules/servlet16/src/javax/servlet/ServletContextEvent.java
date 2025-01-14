/*
 * Copyright (c) 1998-2001 Caucho Technology -- all rights reserved
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
 * $Id: ServletContextEvent.java,v 1.1 2010-04-18 01:14:12 alex Exp $
 */

package javax.servlet;

import java.util.EventObject;

/**
 * The event class for changes to the servlet context.
 *
 * @since Servlet 2.3
 */
public class ServletContextEvent extends EventObject {
  // The servlet context that changed.
  private ServletContext application;
  /**
   * Creates a ServletContextEvent from the changed application.
   *
   * @param application the servlet context that has changed.
   */
  public ServletContextEvent(ServletContext application)
  {
    super(application);

    this.application = application;
  }
  /**
   * Returns the ServletContext that changed.
   *
   * @return the changed application
   */
  public ServletContext getServletContext()
  {
    return application;
  }
}

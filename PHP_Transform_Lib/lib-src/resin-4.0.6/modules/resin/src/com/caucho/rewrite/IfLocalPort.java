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
 * @author Sam
 */

package com.caucho.rewrite;

import com.caucho.config.ConfigException;
import com.caucho.config.Configurable;
import com.caucho.util.L10N;

import javax.servlet.http.HttpServletRequest;
import javax.annotation.PostConstruct;

/**
 * Matches if request.getLocalPort() matches the 'value' attribute.
 *
 * <pre>
 * &lt;resin:Allow url-pattern="/admin/*"&gt;
 *                  xmlns:resin="urn:java:com.caucho.resin"&gt;
 *   &lt;resin:IfLocalPort value="8080"/>
 * &lt;/resin:Allow>
 * </pre>
 *
 * <p>RequestPredicates may be used for security and rewrite actions.
 */
@Configurable
public class IfLocalPort implements RequestPredicate
{
  private static final L10N L = new L10N(IfLocalPort.class);
  
  private int _localPort;

  /**
   * Sets the local port to compare
   */
  @Configurable
  public void setValue(int value)
  {
    _localPort = value;
  }

  @PostConstruct
  public void init()
  {
    if (_localPort == 0)
      throw new ConfigException(L.l("'localPort' is a required attribute for {0}",
				    getClass().getSimpleName()));
  }

  /**
   * True if the predicate matches.
   *
   * @param request the servlet request to test
   */
  public boolean isMatch(HttpServletRequest request)
  {
    return _localPort == request.getLocalPort();
  }
}

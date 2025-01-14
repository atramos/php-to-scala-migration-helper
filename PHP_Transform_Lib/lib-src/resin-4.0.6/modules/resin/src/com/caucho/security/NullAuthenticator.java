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

package com.caucho.security;

import java.io.*;
import java.util.*;
import java.security.*;

import javax.servlet.http.*;
import javax.servlet.*;

import com.caucho.util.*;
import com.caucho.vfs.*;

import com.caucho.security.BasicPrincipal;

/**
 * The null authenticator creates a dummy user.
 */
public class NullAuthenticator extends AbstractAuthenticator {
  @Override
  protected Principal authenticate(Principal user,
				   PasswordCredentials cred,
				   Object details)
  {
    return new BasicPrincipal(user.getName());
  }

  /**
   * Returns true if the user plays the named role.
   *
   * @param user the user to test
   * @param role the role to test
   */
  @Override
  public boolean isUserInRole(Principal user, String role)
  {
    return user != null && "user".equals(role);
  }
}

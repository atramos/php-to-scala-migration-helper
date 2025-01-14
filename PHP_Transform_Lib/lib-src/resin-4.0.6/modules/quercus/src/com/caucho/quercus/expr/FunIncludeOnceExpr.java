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

package com.caucho.quercus.expr;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.StringValue;
import com.caucho.vfs.Path;

/**
 * Represents a PHP include statement
 */
public class FunIncludeOnceExpr extends AbstractUnaryExpr {
  protected Path _dir;
  protected boolean _isRequire;
  
  public FunIncludeOnceExpr(Location location, Path sourceFile, Expr expr)
  {
    super(location, expr);

    // XXX: issues with eval
    if (! sourceFile.getScheme().equals("string"))
      _dir = sourceFile.getParent();
  }
  
  public FunIncludeOnceExpr(Location location, Path sourceFile, Expr expr, boolean isRequire)
  {
    this(location, sourceFile, expr);

    _isRequire = isRequire;
  }
  
  public FunIncludeOnceExpr(Path sourceFile, Expr expr)
  {
    this(Location.UNKNOWN, sourceFile, expr);
  }
  
  public FunIncludeOnceExpr(Path sourceFile, Expr expr, boolean isRequire)
  {
    this(Location.UNKNOWN, sourceFile, expr, isRequire);
  }
  
  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @return the expression value.
   */
  public Value eval(Env env)
  {
    StringValue name = _expr.eval(env).toStringValue();

    // return env.include(_dir, name);
    
    env.pushCall(this, NullValue.NULL, new Value[] { name });
    
    try {
      if (_dir != null)
        return env.includeOnce(_dir, name, _isRequire);
      else if (_isRequire)
        return env.requireOnce(name);
      else
        return env.includeOnce(name);
    }
    finally {
      env.popCall();
    }
  }
  
  public boolean isRequire()
  {
    return _isRequire;
  }
  
  public String toString()
  {
    return _expr.toString();
  }
}


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

package com.caucho.jsp;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * A buffered JSP writer encapsulating a Writer.
 */
abstract class AbstractBodyContent extends AbstractJspWriter {
  private static final char []_trueChars = "true".toCharArray();
  private static final char []_falseChars = "false".toCharArray();
  private static final char []_nullChars = "null".toCharArray();

  private final char []_tempCharBuffer = new char[256];

  private boolean _isPrintNullAsBlank;

  public void setPrintNullAsBlank(boolean enable)
  {
    _isPrintNullAsBlank = enable;
  }

  /**
   * Writes a character array to the writer.
   *
   * @param buf the buffer to write.
   * @param off the offset into the buffer
   * @param len the number of characters to write
   */
  abstract public void write(char []buf, int offset, int length)
    throws IOException;
  
  /**
   * Writes a character to the output.
   *
   * @param buf the buffer to write.
   */
  abstract public void write(int ch) throws IOException;

  /**
   * Writes a char buffer to the output.
   *
   * @param buf the buffer to write.
   */
  final public void write(char []buf) throws IOException
  {
    write(buf, 0, buf.length);
  }

  /**
   * Writes a string to the output.
   */
  final public void write(String s) throws IOException
  {
    write(s, 0, s.length());
  }

  /**
   * Writes a subsection of a string to the output.
   */
  public void write(String s, int off, int len) throws IOException
  {
    while (len > 0) {
      int sublen = _tempCharBuffer.length;
      
      if (len < sublen)
	sublen = len;
      
      s.getChars(off, off + sublen, _tempCharBuffer, 0);

      write(_tempCharBuffer, 0, sublen);

      len -= sublen;
      off += sublen;
    }
  }

  /**
   * Writes the newline character.
   */
  public void newLine() throws IOException
  {
    write('\n');
  }
  
  /**
   * Prints a boolean.
   */
  final public void print(boolean b) throws IOException
  {
    write(b ? _trueChars : _falseChars);
  }

  /**
   * Prints a character.
   */
  public void print(char ch) throws IOException
  {
    write(ch);
  }
  
  public void print(int i) throws IOException
  {
    if (i == 0x80000000) {
      print("-2147483648");
      return;
    }

    if (i < 0) {
      write('-');
      i = -i;
    } else if (i < 9) {
      write('0' + i);
      return;
    }

    int length = 0;
    int exp = 10;

    if (i >= 1000000000)
      length = 9;
    else {
      for (; i >= exp; length++)
        exp = 10 * exp;
    }

    int j = 31;
    
    while (i > 0) {
      _tempCharBuffer[--j] = (char) ((i % 10) + '0');
      i /= 10;
    }

    write(_tempCharBuffer, j, 31 - j);
  }
  
  public void print(long v) throws IOException
  {
    if (v == 0x8000000000000000L) {
      print("-9223372036854775808");
      return;
    }

    if (v < 0) {
      write('-');
      v = -v;
    } else if (v == 0) {
      write('0');
      return;
    }

    int j = 31;
    
    while (v > 0) {
      _tempCharBuffer[--j] = (char) ((v % 10) + '0');
      v /= 10;
    }

    write(_tempCharBuffer, j, 31 - j);
  }
  
  final public void print(float f) throws IOException
  {
    write(String.valueOf(f));
  }
  
  final public void print(double d) throws IOException
  {
    write(String.valueOf(d));
  }

  /**
   * Prints a character array
   */
  final public void print(char []s) throws IOException
  {
    write(s, 0, s.length);
  }

  /**
   * Prints a string.
   */
  final public void print(String s) throws IOException
  {
    if (s != null)
      write(s, 0, s.length());
    else if (_isPrintNullAsBlank) {
    }
    else
      write(_nullChars, 0, _nullChars.length);
  }

  /**
   * Prints the value of the object.
   */
  final public void print(Object v) throws IOException
  {
    if (v != null) {
      String s = v.toString();
      
      write(s, 0, s.length());
    }
    else if (_isPrintNullAsBlank) {
    }
    else
      write(_nullChars, 0, _nullChars.length);
  }

  /**
   * Prints the newline.
   */
  public void println() throws IOException
  {
    write('\n');
  }
  
  /**
   * Prints the boolean followed by a newline.
   *
   * @param v the value to print
   */
  final public void println(boolean v) throws IOException
  {
    print(v);
    println();
  }

  /**
   * Prints a character followed by a newline.
   *
   * @param v the value to print
   */
  final public void println(char v) throws IOException
  {
    print(v);
    println();
  }
  
  /**
   * Prints an integer followed by a newline.
   *
   * @param v the value to print
   */
  final public void println(int v) throws IOException
  {
    print(v);
    println();
  }
  
  /**
   * Prints a long followed by a newline.
   *
   * @param v the value to print
   */
  final public void println(long v) throws IOException
  {
    print(v);
    println();
  }
  
  /**
   * Prints a float followed by a newline.
   *
   * @param v the value to print
   */
  final public void println(float v) throws IOException
  {
    String s = String.valueOf(v);
    
    write(s, 0, s.length());
    println();
  }
  
  
  /**
   * Prints a double followed by a newline.
   *
   * @param v the value to print
   */
  final public void println(double v) throws IOException
  {
    String s = String.valueOf(v);
    
    write(s, 0, s.length());
    
    println();
  }

  /**
   * Writes a character array followed by a newline.
   */
  final public void println(char []s) throws IOException
  {
    write(s, 0, s.length);
    println();
  }

  /**
   * Writes a string followed by a newline.
   */
  final public void println(String s) throws IOException
  {
    if (s != null)
      write(s, 0, s.length());
    else if (_isPrintNullAsBlank) {
    }
    else
      write(_nullChars, 0, _nullChars.length);

    println();
  }
  
  /**
   * Writes an object followed by a newline.
   */
  final public void println(Object v) throws IOException
  {
    if (v != null) {
      String s = String.valueOf(v);

      write(s, 0, s.length());
    }
    else if (_isPrintNullAsBlank) {
    }
    else
      write(_nullChars, 0, _nullChars.length);
    
    println();
  }

  abstract public void clear() throws IOException;

  abstract public void clearBuffer() throws IOException;

  abstract public void flushBuffer()
    throws IOException;

  abstract public void flush() throws IOException;

  abstract public void close() throws IOException;

  abstract public int getBufferSize();

  abstract public int getRemaining();

  public void writeOut(Writer writer) throws IOException
  {
    throw new UnsupportedOperationException();
  }

  public String getString()
  {
    throw new UnsupportedOperationException();
  }

  public Reader getReader()
  {
    throw new UnsupportedOperationException();
  }

  public void clearBody()
  {
    throw new UnsupportedOperationException();
  }
}

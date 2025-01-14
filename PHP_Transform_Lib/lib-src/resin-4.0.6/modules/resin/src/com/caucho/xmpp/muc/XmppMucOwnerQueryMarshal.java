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

package com.caucho.xmpp.muc;

import com.caucho.xmpp.data.DataForm;
import com.caucho.vfs.*;
import com.caucho.xmpp.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.xml.stream.*;

/**
 * MucOwner query
 *
 * XEP-0045: http://www.xmpp.org/extensions/xep-0045.html
 *
 * <code><pre>
 * namespace = http://jabber.org/protocol/muc#owner
 *
 * element query {
 *   x{jabber:x:data}
 *   | destroy?
 * }
 *
 * element destroy {
 *   attribute jid?,
 *
 *   password?
 *   &amp; reason?
 * }
 * </pre></code>
 */
public class XmppMucOwnerQueryMarshal extends AbstractXmppMarshal {
  private static final Logger log
    = Logger.getLogger(XmppMucOwnerQueryMarshal.class.getName());
  private static final boolean _isFinest = log.isLoggable(Level.FINEST);

  /**
   * Returns the namespace uri for the XMPP stanza value
   */
  public String getNamespaceURI()
  {
    return "http://jabber.org/protocol/muc#owner";
  }

  /**
   * Returns the local name for the XMPP stanza value
   */
  public String getLocalName()
  {
    return "query";
  }

  /**
   * Returns the java classname of the object
   */
  public String getClassName()
  {
    return null;
  }
  
  /**
   * Serializes the object to XML
   */
  public void toXml(XmppStreamWriter out, Serializable object)
    throws IOException, XMLStreamException
  {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Deserializes the object from XML
   */
  public Serializable fromXml(XmppStreamReader in)
    throws IOException, XMLStreamException
  {
    boolean isFinest = log.isLoggable(Level.FINEST);
    int tag = in.nextTag();
    
    MucOwnerQuery mucOwner = null;
    
    while (tag > 0) {
      if (isFinest)
	debug(in);

      if (XMLStreamReader.END_ELEMENT == tag) {
	return mucOwner;
      }

      if (XMLStreamReader.START_ELEMENT == tag
	  && "jabber:x:data".equals(in.getNamespaceURI())) {
	DataForm form = (DataForm) in.readValue();

	mucOwner = new MucOwnerFormQuery(form);
      }
      else if (XMLStreamReader.START_ELEMENT == tag
	       && "destroy".equals(in.getLocalName())) {
	mucOwner = parseDestroy(in);
      }
      else if (XMLStreamReader.START_ELEMENT == tag) {
	log.finer(this + " <" + in.getLocalName() + "> is an unknown tag");
	
	skipToEnd(in, in.getLocalName());
      }

      tag = in.nextTag();
    }

    return null;
  }
  
  /**
   * Deserializes the object from XML
   */
  private MucOwnerDestroyQuery parseDestroy(XmppStreamReader in)
    throws IOException, XMLStreamException
  {
    boolean isFinest = log.isLoggable(Level.FINEST);

    String jid = in.getAttributeValue(null, "jid");
    String password = in.getAttributeValue(null, "password");
    String reason = in.getAttributeValue(null, "reason");

    skipToEnd(in, "destroy");

    return new MucOwnerDestroyQuery(jid, password, reason);
  }
}

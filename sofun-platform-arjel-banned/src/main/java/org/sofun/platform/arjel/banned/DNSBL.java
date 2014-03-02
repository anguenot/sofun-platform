/*
 * Copyright (c)  Sofun Gaming SAS.
 * Copyright (c)  Julien Anguenot <julien@anguenot.org>
 * Copyright (c)  Julien De Preaumont <juliendepreaumont@gmail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Julien Anguenot <julien@anguenot.org> - initial API and implementation
*/

package org.sofun.platform.arjel.banned;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.sofun.platform.arjel.banned.api.exception.ARJELBannedException;

/**
 * DNSBL (DNS BlackList)
 * 
 * <p>
 * 
 * Provides DNSBL queries on a given DNS server.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class DNSBL {

    private static String[] RECORD_TYPES = { "A", "TXT" };

    private final DirContext ictx;

    public DNSBL(String dns) throws NamingException {

        StringBuilder dnsServers = new StringBuilder("");
        dnsServers.append("dns://").append(dns).append(" ");

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.dns.DnsContextFactory");
        env.put("com.sun.jndi.dns.timeout.initial", "4000");
        env.put("com.sun.jndi.dns.timeout.retries", "1");
        env.put(Context.PROVIDER_URL, dnsServers.toString());

        ictx = new InitialDirContext(env);
    }

    /**
     * Returns a TXT attribute record from a DNS server.
     * 
     * @param name: domain to check.
     * @return TXT attribute record or null if not found
     * @throws ARJELBannedException
     */
    public String getTXTAttributeRecordFor(String name)
            throws ARJELBannedException {
        Attribute attribute;
        Attributes attributes;
        try {
            attributes = ictx.getAttributes(name, RECORD_TYPES);
            attribute = attributes.get("TXT");
            if (attribute != null) {
                return (String) attribute.get();
            }
            return null;
        } catch (NameNotFoundException e) {
            return null;
        } catch (NamingException e) {
            throw new ARJELBannedException(e);
        }
    }

}

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

package org.sofun.platform.opta.ftp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.sofun.platform.opta.api.OptaException;

/**
 * FTP client.
 * 
 * <p>
 * 
 * Wrapper around an {@link FTPClient} instance holding FTP server information
 * defined externally (configuration file) It defines primitives to connect,
 * disconnect and retrieve files.
 * 
 * Note the FTP repository might store a LOT of files so make sure to not
 * iterate over it or crazy things like that.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class FTPClientWrapper {

    private FTPClient client;

    private String host;

    private int port;

    private String username;

    private String password;

    public FTPClientWrapper(String host, int port, String username,
            String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Establishes a connection w/ the FTP server.
     * 
     * @throws OptaException
     */
    public void connect() throws OptaException {
        if (client == null || !client.isAvailable()) {
            client = new FTPClient();
        }
        try {
            client.connect(host, port);
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                throw new OptaException("FTP server refused connection.");
            }
        } catch (SocketException e) {
            throw new OptaException(e.getMessage());
        } catch (IOException e) {
            if (client.isConnected()) {
                try {
                    client.disconnect();
                } catch (IOException f) {
                    // do nothing: just spit stack trace.
                    f.printStackTrace();
                }
            }
            throw new OptaException(e.getMessage());
        }
        try {
            if (!client.login(username, password)) {
                client.logout();
                throw new OptaException("Cannot login...");
            }
            client.enterLocalPassiveMode();
        } catch (Exception e) {
            throw new OptaException(e.getMessage());
        }
    }

    /**
     * Disconnect the client from the FTP server.
     * 
     * @throws OptaException
     */
    public void disconnect() throws OptaException {
        try {
            client.logout();
            if (client.isConnected()) {
                client.disconnect();
            }
        } catch (IOException e) {
            throw new OptaException(e.getMessage());
        }
    }

    /**
     * Retrieve a remote FTP file.
     * 
     * @param out: an {@link OutputStream}
     * @param remote: a remote filename
     * @return a {@link OutputStream} if the target remote file is retrieved
     *         correctly. null if this is not the case.
     * @throws OptaException
     */
    public OutputStream getFile(OutputStream out, String remote)
            throws OptaException {
        boolean success = false;
        try {
            // true if successfully completed, false if not.
            success = client.retrieveFile(remote, out);
            out.close();
        } catch (IOException e) {
            throw new OptaException(e.getMessage());
        }
        if (!success) {
            return null;
        }
        return out;
    }

    /**
     * Returns the underlying FTP client.
     * 
     * @return a {@link FTPClient} instance.
     */
    public FTPClient getClient() {
        return client;
    }

}

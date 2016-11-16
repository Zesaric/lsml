/*
 * @formatter:off
 * Li Song Mechlab - A 'mech building tool for PGI's MechWarrior: Online.
 * Copyright (C) 2013  Emily Björk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
//@formatter:on
package org.lisoft.lsml.model.export;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.LoadoutBuilder.ErrorReportingCallback;

/**
 * Will listen on a local socket for messages to open up "lsml://" links
 *
 * @author Emily Björk
 */
public class LsmlProtocolIPC implements Runnable {
    private static final String CHARSET_NAME = "UTF-8";
    // In the private (ephemeral) ports
    public static final int DEFAULT_PORT = 63782;

    /**
     * @param aLsmlUrl
     *            The LSML URL to send.
     * @param aPort
     *            The port to send on.
     * @return <code>true</code> if the message was sent (some one listened to the socket) <code>false</code> if the
     *         message couldn't be sent.
     */
    static public boolean sendLoadout(String aLsmlUrl, int aPort) {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), aPort);
                Writer writer = new OutputStreamWriter(socket.getOutputStream(), CHARSET_NAME);
                BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(aLsmlUrl);
        }
        catch (final IOException e) {
            return false;
        }
        return true;
    }

    private final ServerSocket serverSocket;
    private final Thread thread;
    private final Consumer<String> openLoadoutCallback;

    private boolean done = false;

    /**
     * Creates a new IPC server that can receive messages on the local loopback.
     *
     * @param aPort
     *            The port to listen to.
     * @param aOpenLoadoutCallback
     *            A callback to call when a new {@link Loadout} is received.
     * @throws IOException
     *             if the socket couldn't be opened.
     */
    public LsmlProtocolIPC(int aPort, Consumer<String> aOpenLoadoutCallback) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), aPort));

        thread = new Thread(this);
        thread.setName("IPC THREAD");
        openLoadoutCallback = aOpenLoadoutCallback;
    }

    public void close(ErrorReportingCallback aCallback) {
        final List<Throwable> errors = new ArrayList<>();
        done = true;
        if (null != serverSocket) {
            try {
                serverSocket.close(); // Will throw an SocketException in the
                                      // server thread.
            }
            catch (final IOException e) {
                errors.add(e);
            }
        }

        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            }
            catch (final InterruptedException e) {
                errors.add(e);
            }
        }
        aCallback.report(Optional.empty(), errors);
    }

    @Override
    public void run() {
        while (!done) {
            try (Socket client = serverSocket.accept();
                    Reader reader = new InputStreamReader(client.getInputStream(), CHARSET_NAME);
                    BufferedReader in = new BufferedReader(reader)) {
                final String url = in.readLine();
                openLoadoutCallback.accept(url);
            }
            catch (final IOException e) {
                // Unknown error, probably some random program sending data to
                // us.
            }
        }
    }

    /**
     * Starts the server listener thread.
     */
    public void startServer() {
        thread.start();
    }
}

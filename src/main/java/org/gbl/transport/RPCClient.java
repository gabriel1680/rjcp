package org.gbl.transport;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gbl.protocol.MessageType;
import org.gbl.protocol.NetworkProtocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RPCClient implements AutoCloseable {

    private static final Logger LOG = LogManager.getLogger(RPCClient.class);

    private final NetworkProtocol protocol;
    private final String host;
    private final int port;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public RPCClient(String host, int port, NetworkProtocol protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    private void connect() {
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            return;
        }
        try {
            this.socket = new Socket(this.host, this.port);
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            LOG.error("Failed to connect the socket");
            throw new RuntimeException(e);
        }
    }

    public byte[] sendMessage(String text) {
        connect();
        try {
            protocol.send(out, MessageType.MESSAGE, text.getBytes(StandardCharsets.UTF_8));
            return protocol.receive(in).data();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void ping() {
        try {
            connect();
            protocol.send(out, MessageType.PING, new byte[0]);
            final var response = protocol.receive(in);
            if (response.type() != MessageType.PONG) {
                LOG.error("PING does not PONG");
                throw new RuntimeException("Invalid response, expected PONG");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }
}
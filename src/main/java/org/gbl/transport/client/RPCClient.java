package org.gbl.transport.client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gbl.protocol.MessageType;
import org.gbl.protocol.RPCMessage;
import org.gbl.transport.connection.RCPConnection;
import org.gbl.transport.connection.RCPConnectionFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// TODO: configure timeouts and create pool
public class RPCClient implements AutoCloseable {

    private static final Logger LOG = LogManager.getLogger(RPCClient.class);

    private final RCPConnectionFactory connectionFactory;
    private final InetSocketAddress address;

    private Socket socket;
    private RCPConnection connection;

    public RPCClient(RCPConnectionFactory connectionFactory, InetSocketAddress address) {
        this.connectionFactory = connectionFactory;
        this.address = address;
    }

    private void connect() {
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            LOG.debug("No connected socket");
            return;
        }
        try {
            LOG.debug("Connecting client socket...");
            this.socket = new Socket(address.getHostName(), address.getPort());
            this.connection = connectionFactory.createFrom(socket);
        } catch (IOException e) {
            LOG.error("Failed to connect the socket");
            throw new RuntimeException(e);
        }
    }

    public byte[] sendMessage(String text) {
        connect();
        try {
            LOG.debug("Sending message: " + text);
            connection.send(RPCMessage.message(text.getBytes(StandardCharsets.UTF_8)));
            final var message = connection.receive();
            LOG.debug("Message received. Type:" + message.type());
            return message.data();
        } catch (IOException e) {
            LOG.error("Failed to send or receive message from server");
            throw new RuntimeException(e);
        }
    }

    public void ping() {
        try {
            connect();
            connection.send(RPCMessage.ping());
            final var response = connection.receive();
            if (response.type() != MessageType.PONG) {
                LOG.error("PING does not PONG");
                throw new RuntimeException("Invalid response, expected PONG");
            }
            LOG.debug("PONG received");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            LOG.debug("Closing client socket...");
            socket.close();
        }
        if (connection != null) {
            LOG.debug("Closing connection...");
            connection.close();
        }
    }
}
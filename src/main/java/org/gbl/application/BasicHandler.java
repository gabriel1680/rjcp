package org.gbl.application;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gbl.protocol.RPCMessage;
import org.gbl.transport.server.MessageHandler;
import org.gbl.transport.connection.RCPConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BasicHandler implements MessageHandler {

    private static final Logger LOG = LogManager.getLogger(BasicHandler.class);

    @Override
    public void handle(RPCMessage message, RCPConnection connection) throws IOException {
        switch (message.type()) {
            case PING -> {
                try {
                    LOG.debug("PING received");
                    connection.send(RPCMessage.pong());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case MESSAGE -> {
                final var text = new String(message.data(), StandardCharsets.UTF_8);
                LOG.debug("MESSAGE: " + text);
                connection.send(RPCMessage.message("ACK".getBytes(StandardCharsets.UTF_8)));
            }
            case PONG -> LOG.warn("PONG received (unexpected)");
            case ERROR -> LOG.error("ERROR: " + new String(message.data(), StandardCharsets.UTF_8));
            default ->
                    connection.send(RPCMessage.error("Unknown message type".getBytes(StandardCharsets.UTF_8)));
        }
    }
}

package org.gbl.application;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gbl.transport.message.RPCMessage;
import org.gbl.transport.connection.RCPConnection;
import org.gbl.transport.server.MessageHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BasicHandler implements MessageHandler {

    private static final Logger LOG = LogManager.getLogger(BasicHandler.class);

    @Override
    public void handle(RPCMessage message, RCPConnection connection) throws IOException {
        switch (message.type()) {
            case PING -> {
                LOG.debug("PING received");
                connection.sendAndFlush(RPCMessage.pong());
            }
            case MESSAGE -> {
                final var text = new String(message.data(), StandardCharsets.UTF_8);
                LOG.debug("MESSAGE: " + text);
                connection.sendAndFlush(RPCMessage.message(getBytes("ACK")));
            }
            case PONG -> LOG.warn("PONG received (unexpected)");
            case ERROR -> LOG.error("ERROR: " + new String(message.data(), StandardCharsets.UTF_8));
            default -> connection.sendAndFlush(RPCMessage.error(getBytes("Unknown message type")));
        }
    }

    private static byte[] getBytes(String data) {
        return data.getBytes(StandardCharsets.UTF_8);
    }
}

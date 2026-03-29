package org.gbl.application;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gbl.protocol.MessageType;
import org.gbl.protocol.NetworkProtocol;
import org.gbl.protocol.RPCMessage;
import org.gbl.transport.server.MessageHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DummyHandler implements MessageHandler {

    private static final Logger LOG = LogManager.getLogger(DummyHandler.class);

    @Override
    public void handle(NetworkProtocol protocol, RPCMessage message, OutputStream out) throws IOException {
        switch (message.type()) {
            case PING -> {
                try {
                    LOG.debug("PING received");
                    protocol.send(out, MessageType.PONG, new byte[0]);
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case MESSAGE -> {
                final var text = new String(message.data(), StandardCharsets.UTF_8);
                LOG.debug("MESSAGE: " + text);
                protocol.send(out, MessageType.MESSAGE, "ACK");
            }
            case PONG -> LOG.warn("PONG received (unexpected)");
            case ERROR -> LOG.error("ERROR: " + new String(message.data(),
                                                           StandardCharsets.UTF_8));
            default -> protocol.send(out, MessageType.ERROR, "Unknown message type");
        }
    }
}

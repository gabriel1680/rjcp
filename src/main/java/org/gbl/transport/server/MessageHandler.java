package org.gbl.transport.server;

import org.gbl.protocol.NetworkProtocol;
import org.gbl.protocol.RPCMessage;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface MessageHandler {
//    TODO: Review the checked exceptions
    void handle(NetworkProtocol protocol, RPCMessage message, OutputStream out) throws IOException;
}

package org.gbl.transport.server;

import org.gbl.transport.message.RPCMessage;
import org.gbl.transport.connection.RCPConnection;

import java.io.IOException;

@FunctionalInterface
public interface MessageHandler {
    //    TODO: Review the checked exceptions
    void handle(RPCMessage message, RCPConnection connection) throws IOException;
}

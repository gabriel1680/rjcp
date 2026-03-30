package org.gbl.transport.connection;

import org.gbl.protocol.RPCMessage;

import java.io.IOException;

public interface RCPConnection extends AutoCloseable {
    void send(RPCMessage message) throws IOException;

    RPCMessage receive() throws IOException;

    void close() throws IOException;
}

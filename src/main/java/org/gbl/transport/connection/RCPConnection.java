package org.gbl.transport.connection;

import org.gbl.transport.message.RPCMessage;

import java.io.IOException;

public interface RCPConnection extends AutoCloseable {
    void sendAndFlush(RPCMessage message) throws IOException;

    void send(RPCMessage message) throws IOException;

    RPCMessage receive() throws IOException;

    void close() throws IOException;

    void flush() throws IOException;
}

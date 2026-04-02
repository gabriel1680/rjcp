package org.gbl.protocol;

import org.gbl.transport.message.RPCMessage;
import org.gbl.transport.message.RPCMessageType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NetworkProtocol {
//    TODO: add requestId for out of order processing
    void send(OutputStream out, RPCMessageType type, byte[] data) throws IOException;
    RPCMessage receive(InputStream in) throws IOException;
}

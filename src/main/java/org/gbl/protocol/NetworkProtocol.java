package org.gbl.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NetworkProtocol {
//    TODO: add requestId for out of order processing
    void send(OutputStream out, MessageType type, byte[] data) throws IOException;
    void send(OutputStream out, MessageType type, String data) throws IOException;
    RPCMessage receive(InputStream in) throws IOException;
}

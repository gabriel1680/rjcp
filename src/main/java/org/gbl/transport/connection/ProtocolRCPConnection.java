package org.gbl.transport.connection;

import org.gbl.protocol.NetworkProtocol;
import org.gbl.transport.message.RPCMessage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProtocolRCPConnection implements RCPConnection {

    private final InputStream in;
    private final OutputStream out;
    private final NetworkProtocol protocol;

    public ProtocolRCPConnection(InputStream in, OutputStream out, NetworkProtocol protocol) {
        this.in = new DataInputStream(new BufferedInputStream(in));
        this.out = new DataOutputStream(new BufferedOutputStream(out));
        this.protocol = protocol;
    }

    @Override
    public void sendAndFlush(RPCMessage message) throws IOException {
        send(message);
        flush();
    }

    @Override
    public void send(RPCMessage message) throws IOException {
        protocol.send(out, message.type(), message.data());
    }

    @Override
    public RPCMessage receive() throws IOException {
        return protocol.receive(in);
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }
}

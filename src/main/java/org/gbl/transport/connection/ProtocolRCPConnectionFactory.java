package org.gbl.transport.connection;

import org.gbl.protocol.NetworkProtocol;

import java.io.IOException;
import java.net.Socket;

public class ProtocolRCPConnectionFactory implements RCPConnectionFactory {

    private final NetworkProtocol protocol;

    public ProtocolRCPConnectionFactory(NetworkProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public RCPConnection createFrom(Socket socket) throws IOException {
        return new ProtocolRCPConnection(
                socket.getInputStream(),
                socket.getOutputStream(),
                protocol
        );
    }
}

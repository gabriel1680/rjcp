package org.gbl.transport.connection;

import java.io.IOException;
import java.net.Socket;

@FunctionalInterface
public interface RCPConnectionFactory {
    RCPConnection createFrom(Socket socket) throws IOException;
}

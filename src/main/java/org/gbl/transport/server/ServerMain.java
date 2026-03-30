package org.gbl.transport.server;

import org.apache.log4j.BasicConfigurator;
import org.gbl.application.BasicHandler;
import org.gbl.protocol.rjcp.RJCP;
import org.gbl.transport.connection.ProtocolRCPConnectionFactory;

public class ServerMain {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        final var protocol = new RJCP();
        final var connectionFactory = new ProtocolRCPConnectionFactory(protocol);
        final var handler = new BasicHandler();
        final var server = new RPCServer(connectionFactory, handler);
        server.start(8080);
    }
}
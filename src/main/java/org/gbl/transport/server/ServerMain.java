package org.gbl.transport.server;

import org.apache.log4j.BasicConfigurator;
import org.gbl.protocol.rjcp.RJCP;
import org.gbl.application.DummyHandler;

public class ServerMain {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        final var protocol = new RJCP();
        final var handler = new DummyHandler();
        final var server = new RPCServer(protocol, handler);
        server.start(8080);
    }
}
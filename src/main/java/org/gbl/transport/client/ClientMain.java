package org.gbl.transport.client;

import org.apache.log4j.BasicConfigurator;
import org.gbl.protocol.rjcp.RJCP;
import org.gbl.transport.connection.ProtocolRCPConnectionFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        final var protocol = new RJCP();
        final var connectionFactory = new ProtocolRCPConnectionFactory(protocol);
        final var address = new InetSocketAddress("localhost", 8080);
        try (var client = new RPCClient(connectionFactory, address)) {
            client.ping();
            final var bytes = client.sendMessage("Hello!");
            System.out.println("Server response:" + new String(bytes, StandardCharsets.UTF_8));
        }
        System.out.println("request ended");
    }
}

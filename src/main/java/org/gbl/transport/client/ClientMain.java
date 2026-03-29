package org.gbl.transport.client;

import org.apache.log4j.BasicConfigurator;
import org.gbl.protocol.rjcp.RJCP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        try (var client = new RPCClient("localhost", 8080, new RJCP())) {
            client.ping();
            final var bytes = client.sendMessage("Hello!");
            System.out.println("Server response:" + new String(bytes, StandardCharsets.UTF_8));
        }
        System.out.println("request ended");
    }
}

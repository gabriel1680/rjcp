package org.gbl.transport.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gbl.protocol.NetworkProtocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RPCServer {
    private static final Logger LOG = LogManager.getLogger(RPCServer.class);

    private final NetworkProtocol protocol;
    private final MessageHandler handler;

    private boolean running;

    private ServerSocket serverSocket;
    private ExecutorService executor;

    public RPCServer(NetworkProtocol protocol, MessageHandler handler) {
        this.handler = handler;
        this.running = false;
        this.protocol = protocol;
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
            if (executor != null) executor.shutdown();
        } catch (Exception e) {
            LOG.error("Error on stopping the server", e);
        }
    }

    public void start(int port) {
        running = true;
        try (var server = new ServerSocket(port)) {
            serverSocket = server;
            LOG.info("Server running on port:" + port);
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                this.executor = executor;
                while (running) {
                    final var clientSocket = server.accept();
                    executor.submit(() -> handle(clientSocket));
                }
            } catch (Exception e) {
                LOG.error("Failed during execution", e);
            }
        } catch (Exception e) {
            LOG.error("Failed to create the socket", e);
        }
    }

    private void handle(Socket clientSocket) {
        LOG.info("Client connected: " + clientSocket);
        try (
                var socket = clientSocket;
                var in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                var out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))
        ) {
            while (running) {
                final var message = protocol.receive(in);
                handler.handle(protocol, message, out);
//                TODO: add flush mode to the protocol and remove this line
            }
        } catch (EOFException e) {
            LOG.info("Client disconnected: " + clientSocket.getInetAddress() + clientSocket.getPort());
        } catch (Exception e) {
            LOG.error("Error with client: " + clientSocket, e);
        }
    }
}
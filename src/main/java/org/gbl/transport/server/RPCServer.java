package org.gbl.transport.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gbl.transport.connection.RCPConnectionFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RPCServer {
    private static final Logger LOG = LogManager.getLogger(RPCServer.class);

    private final RCPConnectionFactory connectionFactory;
    private final MessageHandler handler;
    private final ExecutorService executor;

    private volatile boolean running;

    private ServerSocket server;

    public RPCServer(RCPConnectionFactory connectionFactory, MessageHandler handler) {
        this.connectionFactory = connectionFactory;
        this.handler = handler;
        this.running = false;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        if (!running) return;

        running = false;
        try {
            server.close();
            executor.shutdown();
        } catch (Exception e) {
            LOG.error("Error on stopping the server", e);
            throw new RuntimeException("Failed to stop server", e);
        }
    }

    public void start(int port) {
        if (running)
            throw new IllegalStateException("server already running");

        try {
            running = true;
            server = new ServerSocket(port);
        } catch (IOException e) {
            LOG.error("Failed to create the socket", e);
            throw new RuntimeException("Failed to create server", e);
        }

        LOG.info("Server is running on %s".formatted(toCanonicalAddress(server)));
        while (running) {
            try {
                final var socket = server.accept();
                executor.submit(() -> handle(socket)); // TODO: limit VT creation - semaphore?
            } catch (IOException e) {
                if (running) {
                    LOG.error("Failed to accept new connection", e);
                } else {
                    LOG.debug("Server socket closed, stopping accept loop...");
                }
            }
        }
    }

    private void handle(Socket socket) {
        final var canonicalAddress = toCanonicalAddress(socket);
        LOG.info("Client connected: %s".formatted(canonicalAddress));
        try (socket; var connection = connectionFactory.createFrom(socket)) {
            while (running) {
                final var message = connection.receive();
                handler.handle(message, connection);
            }
        } catch (EOFException e) {
            LOG.info("Client disconnected: %s".formatted(canonicalAddress));
        } catch (Exception e) {
            LOG.error("Error with client: %s".formatted(socket), e);
        }
    }

    private static String toCanonicalAddress(Socket socket) {
        return "%s:%d".formatted(socket.getInetAddress(), socket.getPort());
    }

    private static String toCanonicalAddress(ServerSocket socket) {
        return "%s:%d".formatted(socket.getInetAddress(), socket.getLocalPort());
    }
}
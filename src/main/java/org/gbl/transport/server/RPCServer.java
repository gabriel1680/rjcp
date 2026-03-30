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

    private boolean running;

    private ServerSocket serverSocket;
    private ExecutorService executor;

    public RPCServer(RCPConnectionFactory connectionFactory, MessageHandler handler) {
        this.connectionFactory = connectionFactory;
        this.handler = handler;
        this.running = false;
    }

    public boolean isRunning() {
        return running;
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
                    final var socket = server.accept();
                    executor.submit(() -> handle(socket));
                }
            } catch (IOException e) {
                LOG.error("Failed during execution", e);
            }
        } catch (IOException e) {
            LOG.error("Failed to create the socket", e);
        }
    }

    private void handle(Socket socket) {
        LOG.info("Client connected: " + socket.getInetAddress());
        try (var connection = connectionFactory.createFrom(socket)) {
            while (running) {
                final var message = connection.receive();
                handler.handle(message, connection);
            }
        } catch (EOFException e) {
            LOG.info("Client disconnected: " + socket.getInetAddress() + socket.getPort());
        } catch (Exception e) {
            LOG.error("Error with client: " + socket, e);
        }
    }
}
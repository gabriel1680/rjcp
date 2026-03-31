package org.gbl;

import org.apache.log4j.helpers.LogLog;
import org.gbl.application.BasicHandler;
import org.gbl.protocol.MessageType;
import org.gbl.protocol.rjcp.RJCP;
import org.gbl.transport.client.RPCClient;
import org.gbl.transport.connection.ProtocolRCPConnectionFactory;
import org.gbl.transport.server.RPCServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

    @BeforeAll
    static void beforeAll() {
        LogLog.setQuietMode(true);
    }

    private CountDownLatch countDownLatch;
    private Thread serverThread;
    private RPCServer server;
    private RPCClient client;

    @BeforeEach
    void setUp() throws InterruptedException {
        final var protocol = new RJCP();
        final var connectionFactory = new ProtocolRCPConnectionFactory(protocol);
        final var handler = new BasicHandler();
        server = new RPCServer(connectionFactory, handler);
        final var port = 8080;
        client = new RPCClient(connectionFactory, new InetSocketAddress("localhost", port));
        countDownLatch = new CountDownLatch(1);
        serverThread = Thread.ofVirtual().start(() -> {
            countDownLatch.countDown();
            server.start(port);
        });
        countDownLatch.await();
    }

    @AfterEach
    void tearDown() throws InterruptedException, IOException {
        client.close();
        server.stop();
        serverThread.join(1000);
    }

    @Test
    void start_and_stop_is_running_flag() {
        assertThat(server.isRunning()).isTrue();
        server.stop();
        assertThat(server.isRunning()).isFalse();
    }

    @Test
    void on_ping_should_pong() {
        final var response = client.ping();
        assertThat(response.type()).isEqualTo(MessageType.PONG);
    }

    @Test
    void on_message_should_ack() {
        final var response = client.sendMessage("Hi!");
        assertThat(response.type()).isEqualTo(MessageType.MESSAGE);
        assertThat(new String(response.data(), StandardCharsets.UTF_8)).isEqualTo("ACK");
    }

//    TODO: error and pong cases
}

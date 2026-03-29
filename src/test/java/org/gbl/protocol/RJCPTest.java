package org.gbl.protocol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class RJCPTest {

    private RJCP protocol;

    @BeforeEach
    void setUp() {
        protocol = new RJCP();
    }

    private static void assertMessage(RJCPMessage message, int version, MessageType type,
                                      String data) {
        assertThat(message).isNotNull();
        assertThat(message.version()).isEqualTo((byte) version);
        assertThat(message.type()).isEqualTo(type);
        assertThat(message.data()).isEqualTo(data.getBytes(StandardCharsets.UTF_8));
    }

    private ByteArrayInputStream createInput(int version, int type, String data) throws IOException {
        final var payloadBytes = data.getBytes(StandardCharsets.UTF_8);
        final var messageBytes = buildMessage((byte) version, (byte) type, payloadBytes);
        return new ByteArrayInputStream(messageBytes);
    }

    private byte[] buildMessage(byte version, byte type, byte[] payload) throws IOException {
        var out = new ByteArrayOutputStream();
        var data = new DataOutputStream(out);
        data.writeByte(version);
        data.writeByte(type);
        data.writeInt(payload.length);
        data.write(payload);
        return out.toByteArray();
    }

    @Test
    void receive() throws Exception {
        var version = 1;
        var type = MessageType.MESSAGE;
        var data = "Hello";
        var in = createInput(version, type.code(), data);
        var message = protocol.receive(in);
        assertMessage(message, version, type, data);
    }

    @Test
    void receive_max_length() throws Exception {
        var version = 1;
        var type = MessageType.PONG;
        var data = "Hello";
        var in = createInput(version, type.code(), data);
        var message = protocol.receive(in);
        assertMessage(message, version, type, data);
    }

    @Test
    void send() {
        byte version = 1;
        var type = MessageType.ERROR;
        byte[] data = "Hello".getBytes(StandardCharsets.UTF_8);
        var out = new ByteArrayOutputStream();
        protocol.send(out, type, data);
        byte[] bytes = out.toByteArray();
        assertThat(bytes[0]).isEqualTo(version);
        assertThat(bytes[1]).isEqualTo(type.code());
        int length = ByteBuffer.wrap(bytes, 2, 4).getInt();
        assertThat(length).isEqualTo(5);
        byte[] payload = Arrays.copyOfRange(bytes, 6, bytes.length);
        assertThat(payload).isEqualTo(data);
    }

    @Test
    void send_and_receive() {
        var out = new ByteArrayOutputStream();
        byte[] payload = "Hello".getBytes(StandardCharsets.UTF_8);
        var type = MessageType.MESSAGE;
        protocol.send(out, type, payload);
        var in = new ByteArrayInputStream(out.toByteArray());
        var message = protocol.receive(in);
        assertMessage(message, 1, type, "Hello");
    }
}
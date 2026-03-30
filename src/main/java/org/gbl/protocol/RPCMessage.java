package org.gbl.protocol;

public record RPCMessage(double version, MessageType type, byte[] data) {

    public static RPCMessage ping() {
        return new RPCMessage((byte) 1, MessageType.PING, new byte[0]);
    }

    public static RPCMessage pong() {
        return new RPCMessage((byte) 1, MessageType.PONG, new byte[0]);
    }

    public static RPCMessage message(byte[] data) {
        return new RPCMessage((byte) 1, MessageType.MESSAGE, data);
    }

    public static RPCMessage error(byte[] data) {
        return new RPCMessage((byte) 1, MessageType.ERROR, data);
    }
}

package org.gbl.transport.message;

public record RPCMessage(double version, RPCMessageType type, byte[] data) {

    public static RPCMessage ping() {
        return new RPCMessage((byte) 1, RPCMessageType.PING, new byte[0]);
    }

    public static RPCMessage pong() {
        return new RPCMessage((byte) 1, RPCMessageType.PONG, new byte[0]);
    }

    public static RPCMessage message(byte[] data) {
        return new RPCMessage((byte) 1, RPCMessageType.MESSAGE, data);
    }

    public static RPCMessage error(byte[] data) {
        return new RPCMessage((byte) 1, RPCMessageType.ERROR, data);
    }
}

package org.gbl.protocol;

public enum RPCMessageType {
    PING(1),
    PONG(2),
    MESSAGE(3),
    ERROR(4);

    private final byte code;

    RPCMessageType(int code) {
        this.code = (byte) code;
    }

    public byte code() {
        return code;
    }

    private static final RPCMessageType[] LOOKUP_TABLE = new RPCMessageType[256];

    static {
        for (var type : values()) {
            LOOKUP_TABLE[type.code() & 0xFF] = type;
        }
    }

    public static RPCMessageType from(byte code) {
        final var type = LOOKUP_TABLE[code & 0xFF]; // O(1)
        if (type == null) {
            throw new IllegalArgumentException("Unknown type: " + code);
        }
        return type;
    }
}
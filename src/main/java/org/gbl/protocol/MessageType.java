package org.gbl.protocol;

public enum MessageType {
    PING(1),
    PONG(2),
    MESSAGE(3),
    ERROR(4);

    private final byte code;

    MessageType(int code) {
        this.code = (byte) code;
    }

    public byte code() {
        return code;
    }

    private static final MessageType[] LOOKUP_TABLE = new MessageType[256];

    static {
        for (var type : values()) {
            LOOKUP_TABLE[type.code() & 0xFF] = type;
        }
    }

    public static MessageType from(byte code) {
        final var type = LOOKUP_TABLE[code & 0xFF]; // O(1)
        if (type == null) {
            throw new IllegalArgumentException("Unknown type: " + code);
        }
        return type;
    }
}
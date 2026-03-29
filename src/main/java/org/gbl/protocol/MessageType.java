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

    private static final MessageType[] LOOKUP = new MessageType[256];

    static {
        for (MessageType t : values()) {
            LOOKUP[t.code() & 0xFF] = t;
        }
    }

    public static MessageType from(byte code) {
        // O(1)
        MessageType type = LOOKUP[code & 0xFF];
        if (type == null) {
            throw new IllegalArgumentException("Unknown type: " + code);
        }
        return type;
    }
}
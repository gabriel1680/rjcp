package org.gbl.protocol;

public record RPCMessage(double version, MessageType type, byte[] data) {
}

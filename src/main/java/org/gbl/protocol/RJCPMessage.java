package org.gbl.protocol;

public record RJCPMessage(double version, MessageType type, byte[] data) {
}

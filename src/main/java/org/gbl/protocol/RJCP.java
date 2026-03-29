package org.gbl.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RJCP {

    private static final byte VERSION = 1;

    public RJCPMessage receive(InputStream in) {
        var dataInput = new DataInputStream(in);
        try {
            byte version = dataInput.readByte();
            byte type = dataInput.readByte();
            int length = dataInput.readInt();
            // TODO: Fix the readNBytes - can read more or less than the given length
            byte[] data = dataInput.readNBytes(length);
            return new RJCPMessage(version, MessageType.from(type), data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(ByteArrayOutputStream out, MessageType type, byte[] data) {
        try {
            DataOutputStream dataOut = new DataOutputStream(out);
            dataOut.writeByte(VERSION);
            dataOut.writeByte(type.code());
            dataOut.writeInt(data.length);
            dataOut.write(data);
            dataOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

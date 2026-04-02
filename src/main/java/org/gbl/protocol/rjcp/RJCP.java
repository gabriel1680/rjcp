package org.gbl.protocol.rjcp;

import org.gbl.protocol.RPCMessageType;
import org.gbl.protocol.NetworkProtocol;
import org.gbl.protocol.RPCMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RJCP implements NetworkProtocol {

    private static final byte VERSION = 1;

    public RPCMessage receive(InputStream in) throws IOException {
        var dataInput = new DataInputStream(in);
        byte version = dataInput.readByte();
        byte type = dataInput.readByte();
        int length = dataInput.readInt();
        // TODO: Fix the readNBytes - can read more or less than the given length
        byte[] data = dataInput.readNBytes(length);
        return new RPCMessage(version, RPCMessageType.from(type), data);
    }

    public void send(OutputStream out, RPCMessageType type, byte[] data) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeByte(VERSION);
        dataOut.writeByte(type.code());
        dataOut.writeInt(data.length);
        dataOut.write(data);
    }
}

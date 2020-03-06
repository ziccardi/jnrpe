package it.jnrpe.network.protocol.v3;

import it.jnrpe.network.execution.ExecutionResult;
import it.jnrpe.network.protocol.ProtocolPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.CRC32;

public class NRPEV3Response extends NRPEV3AbstractPacket {
    public NRPEV3Response(ExecutionResult result) {
        super(2, 0, result.getStatus().ordinal(), 0, messageToBuffer(result.getMessage()), new byte[]{0,0,0});
        updateCRC();
    }

    private static byte[] messageToBuffer(String msg) {
        byte[] msgBytes = msg.getBytes();
        return Arrays.copyOf(msg.getBytes(), Math.max(1024, msgBytes.length + 1));
    }
}

package it.jnrpe.network.protocol.v4;

import it.jnrpe.network.execution.ExecutionResult;

import java.util.Arrays;

public class NRPEV4Response extends NRPEV4AbstractPacket {
    public NRPEV4Response(ExecutionResult result) {
        super(2, 0, result.getStatus().ordinal(), 0, messageToBuffer(result.getMessage()), new byte[]{0,0,0});
        updateCRC();
    }

    private static byte[] messageToBuffer(String msg) {
        byte[] msgBytes = msg.getBytes();
        return Arrays.copyOf(msg.getBytes(), Math.max(1024, msgBytes.length + 1));
    }
}

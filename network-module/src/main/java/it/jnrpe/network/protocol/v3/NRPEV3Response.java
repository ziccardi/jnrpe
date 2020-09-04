package it.jnrpe.network.protocol.v3;

import it.jnrpe.command.execution.ExecutionResult;
import java.util.Arrays;

public class NRPEV3Response extends NRPEV3AbstractPacket {
    public NRPEV3Response(ExecutionResult result) {
        super(2, 0, result.getStatus().ordinal(), 0, messageToBuffer(result.getMessage()), new byte[]{0,0,0});
        updateCRC();
    }

    private static byte[] messageToBuffer(String msg) {
        byte[] msgBytes = msg.getBytes();
        return Arrays.copyOf(msg.getBytes(), msgBytes.length + 1);
    }
}

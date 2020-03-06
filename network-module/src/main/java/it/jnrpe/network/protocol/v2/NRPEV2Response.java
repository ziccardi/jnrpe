package it.jnrpe.network.protocol.v2;

import it.jnrpe.network.execution.ExecutionResult;
import java.util.Arrays;

public class NRPEV2Response extends NRPEV2AbstractPacket {
    public NRPEV2Response(ExecutionResult result) {
        super(2, 0, result.getStatus().ordinal(), messageToBuffer(result.getMessage()), new byte[]{0,0});
        updateCRC();
    }

    private static byte[] messageToBuffer(String msg) {
        byte[] buffer = Arrays.copyOf(msg.getBytes(), 1024);
        buffer[Math.min(1023, msg.length())] = 0;
        return buffer;
    }
}

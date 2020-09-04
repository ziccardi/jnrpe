package it.jnrpe.network.encoders;

import it.jnrpe.command.execution.ExecutionResult;
import it.jnrpe.network.protocol.ProtocolPacket;
import it.jnrpe.network.protocol.v2.NRPEV2Response;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

class V2Encoder implements IResponseEncoder {
    private final ProtocolPacket response;

    public V2Encoder(final ExecutionResult result) {
        this.response = new NRPEV2Response(result);
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeShort(response.getVersion()); // Version 2
            dout.writeShort(response.getPacketType()); // Type: Response
            dout.writeInt((int) response.getCrc32()); // Type: Response
            dout.writeShort(response.getResultCode());
            dout.write(response.getBuffer());
            dout.write(response.getPadding());
            dout.flush();
        } catch (Exception e) {

        }

        return bout.toByteArray();
    }
}

package it.jnrpe.network.encoders;

import it.jnrpe.command.execution.ExecutionResult;
import it.jnrpe.network.protocol.ProtocolPacket;
import it.jnrpe.network.protocol.v4.NRPEV4Response;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

class V4Encoder implements IResponseEncoder {
    private final ProtocolPacket response;

    public V4Encoder(final ExecutionResult result) {
        this.response = new NRPEV4Response(result);
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeShort(response.getVersion()); // Version 3
            dout.writeShort(response.getPacketType()); // Type: Response
            dout.writeInt((int) response.getCrc32()); // Type: Response
            dout.writeShort(response.getResultCode());
            dout.writeShort(response.getAlignment());
            dout.writeInt(response.getBuffer().length);
            dout.write(response.getBuffer());
            dout.write(response.getPadding());
            dout.flush();
        } catch (Exception e) {

        }
        return bout.toByteArray();
    }
}

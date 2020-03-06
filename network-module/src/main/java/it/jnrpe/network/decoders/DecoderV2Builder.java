package it.jnrpe.network.decoders;

import it.jnrpe.network.protocol.ProtocolPacket;
import it.jnrpe.network.protocol.v2.NRPEV2Request;

class DecoderV2Builder {
    private static class RequestV2Builder extends AbstractPacketBuilder {
        protected void loadRequestFromBuffer() {
            byte[] reqBuffer = new byte[1024];
            this.buffer.readBytes(reqBuffer);
            this.setRequestBuffer(reqBuffer);

            byte[] padding = new byte[2];
            this.buffer.readBytes(padding);
            this.setPadding(padding);
        }

        @Override
        protected ProtocolPacket buildPacket() {
            return new NRPEV2Request(getCrc32(), getResultCode(), getRequestBuffer(), getPadding());
        }
    }

    public static IPacketBuilder forPacket(int type) {
        switch(type) {
            case 1:
                return new RequestV2Builder();
            default: // FIXME: throw an exception
                return null;
        }
    }
}

package it.jnrpe.network.decoders;

import it.jnrpe.network.protocol.ProtocolPacket;
import it.jnrpe.network.protocol.v3.NRPEV3Request;

class DecoderV3Builder {
    protected static class RequestV3Builder extends AbstractPacketBuilder {
        public void loadRequestFromBuffer() {
            setAlignment(this.buffer.readUnsignedShort());

            long bufLength = this.buffer.readUnsignedInt();
            if (bufLength < 0 || bufLength > 65536) {
                // FIXME: throw an exception
            }
            byte[] requestBuffer = new byte[(int) bufLength];
            this.buffer.readBytes(requestBuffer);
            setRequestBuffer(requestBuffer);

            byte[] padding = new byte[(int)(1020 - bufLength)];
            this.buffer.readBytes(padding);
            setPadding(padding);
        }

        @Override
        protected ProtocolPacket buildPacket() {
            return new NRPEV3Request(getCrc32(), getAlignment(), getRequestBuffer(), getPadding());
        }
    }

    public static IPacketBuilder forPacket(int type) {
        switch(type) {
            case 1:
                return new RequestV3Builder();
            default: // FIXME: throw an exception
                return null;
        }
    }
}

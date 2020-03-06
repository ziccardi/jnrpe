package it.jnrpe.network.decoders;

import it.jnrpe.network.protocol.ProtocolPacket;
import it.jnrpe.network.protocol.v4.NRPEV4Request;

class DecoderV4Builder extends DecoderV3Builder {
    private static class RequestV4Builder extends DecoderV3Builder.RequestV3Builder {
        @Override
        protected ProtocolPacket buildPacket() {
            return new NRPEV4Request(getCrc32(), getAlignment(), getRequestBuffer(), getPadding());
        }
    }

    public static IPacketBuilder forPacket(int type) {
        switch(type) {
            case 1:
                return new DecoderV4Builder.RequestV4Builder();
            default: // FIXME: throw an exception
                return null;
        }
    }
}

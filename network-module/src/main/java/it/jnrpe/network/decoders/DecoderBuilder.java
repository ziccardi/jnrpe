package it.jnrpe.network.decoders;

public class DecoderBuilder {
    public static IPacketBuilder forPacket(int version, int type) {
        switch (version) {
            case 2:
                return DecoderV2Builder.forPacket(type);
            case 3:
                return DecoderV3Builder.forPacket(type);
            case 4:
                return DecoderV4Builder.forPacket(type);
            default:
                // fixme throw exception
                throw new IllegalStateException("Returning null - " + version + " - type " + type);
        }
    }
}

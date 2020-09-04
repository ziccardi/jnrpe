package it.jnrpe.network.encoders;

import it.jnrpe.command.execution.ExecutionResult;

public class EncoderFactory {
    private EncoderFactory() {
    }

    public static IResponseEncoder produceEncoder(int version, ExecutionResult res) {
        switch (version) {
            case 2:
                return new V2Encoder(res);
            case 3:
                return new V3Encoder(res);
            case 4:
                return new V4Encoder(res);
            case 5:
                // FIXME: throw exception
                return null;

        }
        return new V2Encoder(res);
    }
}

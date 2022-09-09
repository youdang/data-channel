package me.progape.java.datachannel.protocol;

import me.progape.java.datachannel.protocol.shared.ErrorResponse;

/**
 * @author progape
 * @date 2022-01-23
 */
public class ProtocolException extends RuntimeException {
    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(ErrorResponse errorResponse) {
        super(errorResponse.getErrorMessage());
    }
}

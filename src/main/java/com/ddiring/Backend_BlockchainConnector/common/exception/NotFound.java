package com.ddiring.Backend_BlockchainConnector.common.exception;

public class NotFound extends ClientError{
    public NotFound(String message) {
        this.errorCode = "NotFound";
        this.errorMessage = message;
    }
}

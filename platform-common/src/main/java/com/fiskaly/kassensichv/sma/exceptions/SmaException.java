package com.fiskaly.kassensichv.sma.exceptions;

import java.io.IOException;

public class SmaException extends IOException {
    private int code;

    public SmaException(String message, int code) {
        super(message);

        this.code = code;
    }

    @Override
    public String getMessage() {
        return super.getMessage()
                + "\nError Code: "
                + this.getCode();
    }

    /**
     * @return Error codes as defined by JSON-RPC
     */
    public int getCode() {
        return this.code;
    }
}

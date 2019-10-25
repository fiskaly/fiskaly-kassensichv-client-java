package com.fiskaly.kassensichv.sma.exceptions;

import java.io.IOException;

public class SmaException extends IOException {
    private int code;

    public SmaException(String message, int code) {
        super(message);

        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

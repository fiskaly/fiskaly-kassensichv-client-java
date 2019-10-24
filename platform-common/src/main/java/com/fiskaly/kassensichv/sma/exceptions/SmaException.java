package com.fiskaly.kassensichv.sma.exceptions;

public class SmaException extends Exception {
    private int code;

    public SmaException(String message, int code) {
        super(message);

        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

package com.fiskaly.kassensichv.sma;

public class AndroidSma implements SmaInterface {
    @Override
    public String invoke(String payload) {
        return Sma.invoke(payload);
    }
}

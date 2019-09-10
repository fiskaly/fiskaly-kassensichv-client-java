package com.fiskaly.kassensichv.sma;

public class AndroidSMA implements SMA {
    @Override
    public String invoke(String payload) {
        return Sma.invoke(payload);
    }
}

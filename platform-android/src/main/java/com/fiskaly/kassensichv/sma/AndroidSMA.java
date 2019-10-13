package com.fiskaly.kassensichv.sma;

public class AndroidSMA implements SMAInterface {
    @Override
    public String invoke(String payload) {
        return Sma.invoke(payload);
    }
}

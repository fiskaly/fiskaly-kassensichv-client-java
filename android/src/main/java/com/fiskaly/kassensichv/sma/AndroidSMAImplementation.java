package com.fiskaly.kassensichv.sma;

public class AndroidSMAImplementation implements SMAImplementation {
    @Override
    public String invoke(String payload) {
        return Sma.invoke(payload);
    }
}

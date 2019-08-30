package com.fiskaly.kassensichv.sma;

import com.fiskaly.kassensichv.sma.mobile.Mobile;

public class AndroidSMA implements SMA {
    @Override
    public String invoke(String payload) {
        return Mobile.invoke(payload);
    }
}

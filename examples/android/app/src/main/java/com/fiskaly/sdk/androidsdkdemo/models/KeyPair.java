package com.fiskaly.sdk.androidsdkdemo.models;

public class KeyPair {
    private String apiKey;
    private String apiSecret;

    public KeyPair(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }
}

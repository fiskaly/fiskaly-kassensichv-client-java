package com.fiskaly.kassensichv.client;

import com.fiskaly.kassensichv.client.authentication.TokenManager;
import com.fiskaly.kassensichv.client.interceptors.AuthenticationInterceptor;
import com.fiskaly.kassensichv.client.interceptors.HeaderInterceptor;
import com.fiskaly.kassensichv.client.interceptors.TransactionInterceptor;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

import java.util.Arrays;

public class ClientFactory {
    private final static String BASE_URL = System.getenv("BASE_URL") != null ?
            System.getenv("BASE_URL") : "https://kassensichv.io/api";

    /**
     * Creates an OkHttpClient ready for use for the KassenSichV API
     * @param apiKey Your KassenSichV API key
     * @param secret Your KassenSichV secret
     * @return An instance of an OkHttpClient with necessary configuration and interceptors in order to work
     * work with the KassenSichV API
     */
    public static OkHttpClient getClient(String apiKey, String secret) {
        TokenManager tokenManager = new TokenManager(
                new OkHttpClient
                        .Builder()
                        .build(),
                apiKey,
                secret);

        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new AuthenticationInterceptor(tokenManager))
                .addInterceptor(new TransactionInterceptor())
                .build();

        return client;
    }
}

package com.fiskaly.kassensichv.client.authentication;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Runs token refreshment logic in case a 401 response occurs
 */
public class Authenticator implements okhttp3.Authenticator {
    private TokenManager tokenManager;

    public Authenticator(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public synchronized Request authenticate(Route route, Response response) {
        // Force refresh
        tokenManager.fetchTokenPair();

        return response.request().newBuilder()
                .header("Authorization", "Bearer" + tokenManager.getAccessToken())
                .build();
    }
}

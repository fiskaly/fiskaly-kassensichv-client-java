package com.fiskaly.kassensichv.client.authentication;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import java.io.IOException;

/**
 * Runs token refreshment logic in case a 401 response occurs
 */
public class Authenticator implements okhttp3.Authenticator {
    private TokenManager tokenManager;

    public Authenticator(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public synchronized Request authenticate(Route route, Response response) throws IOException {
        // Give up, we've already failed to authenticate in the last try
        if (response.request().header("Authorization") != null &&
                response.request().header("X-Authorization-Retry") != null) {
            throw new IOException("Bad Credentials. Authorization Failed.");
        }

        // Force refresh
        tokenManager.fetchTokenPair();

        return response.request().newBuilder()
                .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                .header("X-Authorization-Retry", "1")
                .build();
    }
}

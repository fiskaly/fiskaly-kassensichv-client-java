package com.fiskaly.kassensichv.client.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenManager implements Runnable {
    private OkHttpClient client;

    private final String apiKey;
    private final String secret;

    private String accessToken;
    private String refreshToken;

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final ObjectMapper mapper = new ObjectMapper();

    public TokenManager(OkHttpClient client, String apiKey, String secret) {
        this.client = client;

        this.apiKey = apiKey;
        this.secret = secret;
    }

    private void setTokenPair(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    // Gets called when token needs to be refreshed
    @Override
    public void run() {
        Request request;
        RequestBody body;

        // First fetch
        if (this.accessToken == null) {
            String jsonBody = "{" +
                    "\"api_key\":\"" + this.apiKey + "\"," +
                    "\"api_secret\": \"" + this.secret + "\"" +
                    "}";

            body = RequestBody.create(jsonBody, JSON);
        // Token needs to be refreshed
        } else {
            String jsonBody = "{" +
                    "\"refresh_token\": \"" + this.refreshToken + "\"" +
                    "}";

            body = RequestBody.create(jsonBody, JSON);
        }

        request = new Request
                .Builder()
                .url("https://kassensichv.io/api/v0/auth")
                .post(body)
                .build();

        try(Response response = client.newCall(request).execute()) {
            final String responseBody = response.body().string();
            final TypeReference<HashMap<String, String>> typeReference =
                    new TypeReference<HashMap<String, String>>() {};

            Map<String, String> decodedBody = mapper.readValue(responseBody, typeReference);

            String accessToken = decodedBody.get("access_token");
            String refreshToken = decodedBody.get("refresh_token");

            this.setTokenPair(accessToken, refreshToken);
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}

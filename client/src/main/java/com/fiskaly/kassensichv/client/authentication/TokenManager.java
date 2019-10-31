package com.fiskaly.kassensichv.client.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenManager implements Runnable {
    public static class TokenHolder {
        public String accessToken;
        public String refreshToken;
    }

    private OkHttpClient client;

    private final String apiKey;
    private final String secret;

    private TokenHolder tokenHolder;

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final ObjectMapper mapper = new ObjectMapper();

    public TokenManager(OkHttpClient client, String apiKey, String secret) {
        this(client, apiKey, secret, new TokenHolder());
    }

    public TokenManager(OkHttpClient client, String apiKey, String secret, TokenHolder tokenHolder) {
        this.client = client;
        this.apiKey = apiKey;
        this.secret = secret;
        this.tokenHolder = tokenHolder;
    }

    private synchronized void setTokenPair(String accessToken, String refreshToken) {
        this.tokenHolder.accessToken = accessToken;
        this.tokenHolder.refreshToken = refreshToken;
    }

    public synchronized String getAccessToken() {
        return this.tokenHolder.accessToken;
    }

    /**
     * Parses the response of the authentication request
     * and extracts the token pair
     * @param responseBody Response body of the authentication request
     * @throws IOException In case of the body not being parsable
     */
    private void setTokenPairFromResponseBody(String responseBody) throws IOException {
        final TypeReference<HashMap<String, String>> typeReference =
                new TypeReference<>() {};

        Map<String, String> decodedBody = mapper.readValue(responseBody, typeReference);

        String accessToken = decodedBody.get("access_token");
        String refreshToken = decodedBody.get("refresh_token");

        this.setTokenPair(accessToken, refreshToken);
    }

    public synchronized void fetchTokenPair() {
        Request request;
        RequestBody body;

        // First fetch
        if (this.tokenHolder.accessToken == null) {
            String jsonBody =
                "{" +
                    "\"api_key\":\"" + this.apiKey + "\"," +
                    "\"api_secret\": \"" + this.secret + "\"" +
                "}";

            body = RequestBody.create(jsonBody, JSON);
        // Token needs to be refreshed
        } else {
            String jsonBody =
                "{" +
                    "\"refresh_token\": \"" + this.tokenHolder.refreshToken + "\"" +
                "}";

            body = RequestBody.create(jsonBody, JSON);
        }

        request = new Request
                .Builder()
                .url("https://kassensichv.io/api/v0/auth")
                .post(body)
                .build();

        try(Response response = client.newCall(request).execute()) {
            if(response.isSuccessful()) {
                this.setTokenPairFromResponseBody(
                    response
                        .body()
                        .string()
                );
            } else if(this.tokenHolder.accessToken != null){
                System.err.println("refresh_token seems to be incorrect or expired. Falling back to authorization with API-Key and API-Secret.");
                this.tokenHolder.accessToken = null;
                this.fetchTokenPair();
            } else {
                System.err.println("Bad Credentials.");
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }


    }

    // Gets called when token needs to be refreshed
    @Override
    public void run() {
        fetchTokenPair();
    }
}

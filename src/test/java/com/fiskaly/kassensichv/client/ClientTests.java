package com.fiskaly.kassensichv.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class ClientTests {
    private static String apiKey = System.getenv("API_KEY");
    private static String secret = System.getenv("API_SECRET");

    private static OkHttpClient client = ClientFactory.getClient(apiKey, secret);
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    // Necessary in order to avoid race condition (test calls start before TokenManager set token pair)
    public static void waitForInitialTokenFetch() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test
    public void environment() {
        assertNotNull(this.apiKey);
        assertNotNull(this.secret);
    }

    @Test
    public void instantiateClient() {
        assertNotNull(this.client);
    }

    @Test
    public void createTss() throws IOException {
        Map<String, String> tssMap = new HashMap<>();

        tssMap.put("state", "INITIALIZED");
        tssMap.put("description", "TSS created by test case createTSS");

        String jsonBody = mapper.writeValueAsString(tssMap);

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        UUID uuid = UUID.randomUUID();

        final Request request = new Request
                .Builder()
                .url("https://kassensichv.io/api/v0/tss/" + uuid)
                .put(body)
                .build();

        final Response response = client
                .newCall(request)
                .execute();

        assertEquals(200, response.code());

        final Request listRequest = new Request
                .Builder()
                .url("https://kassensichv.io/api/v0/tss")
                .get()
                .build();

        final Response listResponse = client
                .newCall(listRequest)
                .execute();

        assertEquals(200, listResponse.code());
        assertTrue(listResponse.body().string().contains(uuid.toString()));
    }

    @Test
    public void listAllTss() throws IOException {
        final Request request = new Request
                .Builder()
                .url("https://kassensichv.io/api/v0/tss")
                .get()
                .build();

        final Response response = client
                .newCall(request)
                .execute();

        assertEquals(200, response.code());
    }
}

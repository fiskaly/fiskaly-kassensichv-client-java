import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.sma.GeneralSMA;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.fiskaly.kassensichv.client.ClientFactory.*;
import static org.junit.Assert.*;

/**
 * Set of integration tests for the modified OkHttp client
 */
public class ClientTests {
    private static String apiKey = System.getenv("API_KEY");
    private static String secret = System.getenv("API_SECRET");

    private static OkHttpClient client;

    static {
        try {
            client = getClient(apiKey, secret, new GeneralSMA());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final ObjectMapper mapper = new ObjectMapper();

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
                .url("https://kassensichv.io/api/v0/tss?order_by=time_creation&order=desc")
                .get()
                .build();

        final Response listResponse = client
                .newCall(listRequest)
                .execute();


        String listResponseBody = listResponse.body().string();

        assertEquals(200, listResponse.code());
        assertTrue(listResponseBody.contains(uuid.toString()));
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

    @Test
    public void createTransaction() throws IOException {
        Map<String, String> tssMap = new HashMap<>();

        tssMap.put("state", "INITIALIZED");
        tssMap.put("description", "TSS created by test case createTSS");

        String jsonBody = mapper.writeValueAsString(tssMap);

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        UUID tssId = UUID.randomUUID();

        final Request request = new Request
                .Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId)
                .put(body)
                .build();

        final Response response = client
                .newCall(request)
                .execute();

        assertEquals(200, response.code());

        UUID clientId = UUID.randomUUID();

        RequestBody clientBody = RequestBody.create(
                "{ \"serial_number\": " + "\"" + clientId + "\" }"
                , MediaType.parse("application/json"));

        final Request clientRequest = new Request
                .Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId + "/client/" + clientId)
                .put(clientBody)
                .build();

        final Response clientResponse = client
                .newCall(clientRequest)
                .execute();

        assertEquals(200, clientResponse.code());

        UUID txId = UUID.randomUUID();

        Map<String, Object> txMap = new HashMap<>();

        Map<String,  String> dataMap = new HashMap<>();
        dataMap.put("binary", "dGVzdA==");

        txMap.put("type", "RECEIPT");
        txMap.put("state", "ACTIVE");
        txMap.put("client_id", clientId.toString());
        txMap.put("data", dataMap);

        String txBodyJson = mapper.writeValueAsString(txMap);
        RequestBody txBody = RequestBody.create(txBodyJson, MediaType.parse("application/json"));

        final Request txRequest = new Request
                .Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId + "/tx/" + txId)
                .put(txBody)
                .build();

        final Response txResponse = client
                .newCall(txRequest)
                .execute();

        assertEquals(200, txResponse.code());
    }
}

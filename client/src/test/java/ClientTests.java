import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.client.ClientFactory;
import com.fiskaly.kassensichv.persistence.PersistenceStrategy;
import com.fiskaly.kassensichv.persistence.SqliteStrategy;
import com.fiskaly.kassensichv.sma.GeneralSma;
import okhttp3.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.fiskaly.kassensichv.client.ClientFactory.getClient;
import static org.junit.Assert.*;

/**
 * Set of integration tests for the modified OkHttp client
 */
public class ClientTests {
    private static String apiKey = System.getenv("API_KEY");
    private static String secret = System.getenv("API_SECRET");

    private static OkHttpClient client;
    private static GeneralSma sma;

    static {
        try {
            sma = SmaProvider.getSma();
            client = getClient(apiKey, secret, sma);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void environment() {
        assertNotNull(apiKey);
        assertNotNull(secret);
    }

    @Test
    public void instantiateClient() {
        assertNotNull(client);
        assertNotNull(sma);
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

    @Test (expected = ProtocolException.class)
    public void faultyAuthenticationShouldPersist() throws SQLException, IOException {
        final String directoryId = UUID.randomUUID().toString();
        final String requestUrl = "https://kassensichv.io/api/v0/tss";

        final File directory = new File(System.getProperty("java.io.tmpdir") + File.separator + directoryId);
        final boolean created = directory.mkdir();

        assertTrue(created);

        final PersistenceStrategy strategy = new SqliteStrategy(directory);

        OkHttpClient faultyClient = ClientFactory.getPersistingClient("invalid", "also-invalid",
                sma, strategy);

        // List TSS request
        final Request request = new Request
                .Builder()
                .url(requestUrl)
                .get()
                .build();

        faultyClient
                .newCall(request)
                .execute();

        List<com.fiskaly.kassensichv.persistence.Request> requests =
                strategy.loadRequests();

        assertTrue(requests.size() == 1);

        com.fiskaly.kassensichv.persistence.Request persisted = requests.get(0);

        assertEquals("", persisted.getBody());
        assertEquals("GET", persisted.getMethod());
        assertEquals(requestUrl, persisted.getUrl());
    }
}

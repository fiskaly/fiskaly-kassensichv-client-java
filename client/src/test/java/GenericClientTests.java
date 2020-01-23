import com.fiskaly.kassensichv.client.Client;
import com.fiskaly.kassensichv.sma.GeneralSma;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GenericClientTests {

    private static String apiKey = System.getenv("API_KEY");
    private static String secret = System.getenv("API_SECRET");

    // Please note that this will not be needed anymore if the new go-client is published
    // as the go-client will implement the sma in the future
    private static GeneralSma sma;
    private static Client client;

    @Before
    public void setup() throws Exception {
        sma = SmaProvider.getSma();

        client = new Client(apiKey, secret, "https://kassensichv.io/api/v0/", sma);
    }



    @Test
    public void createTss() throws Exception {

        UUID tssUUID = UUID.randomUUID();

        Map<String, String> body = new HashMap<>();
        body.put("description", "Generic Java Client Test TSS");
        body.put("state", "INITIALIZED");

        String path = "tss/"+ tssUUID;

        String response = client.request("PUT", path, body);

        System.out.println(response);

    }

    @Test
    public void createClient() throws Exception {

        UUID tssUUID = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();

        Map<String, String> body = new HashMap<>();
        body.put("description", "Generic Java Client Test TSS");
        body.put("state", "INITIALIZED");

        String path = "tss/"+ tssUUID;

        String response = client.request("PUT", path, body);

        System.out.println(response);

        body = new HashMap<>();
        body.put("serial_number", clientUUID.toString());

        path = "tss/"+ tssUUID + "/client/" + clientUUID;

        response = client.request("PUT", path, body);

        System.out.println(response);

    }

    @Test
    public void createTx() throws Exception {

        UUID tssUUID = UUID.randomUUID();
        UUID clientUUID = UUID.randomUUID();
        UUID txUUID = UUID.randomUUID();

        Map<String, Object> body = new HashMap<>();
        body.put("description", "Generic Java Client Test TSS");
        body.put("state", "INITIALIZED");

        String path = "tss/"+ tssUUID;

        String response = client.request("PUT", path, body);

        System.out.println(response);

        body = new HashMap<>();
        body.put("serial_number", clientUUID.toString());

        path = "tss/"+ tssUUID + "/client/" + clientUUID;

        response = client.request("PUT", path, body);

        System.out.println(response);

        Map<String,  String> dataMap = new HashMap<>();
        dataMap.put("binary", "dGVzdA==");

        body = new HashMap<>();
        body.put("type", "RECEIPT");
        body.put("state", "ACTIVE");
        body.put("client_id", clientUUID);
        body.put("data", dataMap);

        Map<String, String> query = new HashMap<>();
        query.put("last_revision", "0");

        path = "tss/"+ tssUUID + "/tx/" + txUUID;

        response = client.request("PUT", path, body, query);

        System.out.println(response);

    }

}

import com.fiskaly.kassensichv.sma.GeneralSMA;
import okhttp3.*;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static com.fiskaly.kassensichv.client.ClientFactory.getClient;
import static org.junit.Assert.*;

public class ApiCodeExamplesTests {
  private static final String BASE_URL = "https://kassensichv.io/api/v0/";
  private static UUID tssId;
  private static UUID clientId;
  private static UUID txId;
  private static OkHttpClient client;

  private static void setupTss() throws IOException {
    tssId = UUID.randomUUID();
    final String payload = new JSONObject()
      .put("state", "INITIALIZED")
      .put("description", "Java client test TSS")
      .toString();
    final RequestBody body = RequestBody.create(payload, MediaType.parse("application/json"));
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId)
      .put(body)
      .build();
    client.newCall(request).execute();
  }

  private static void setupClient() throws IOException {
    clientId = UUID.randomUUID();
    final String payload = new JSONObject()
      .put("serial_number", tssId.toString())
      .toString();
    final RequestBody body = RequestBody.create(payload, MediaType.parse("application/json"));
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/client/" + clientId)
      .put(body)
      .build();
    client.newCall(request).execute();
  }

  private static void setupTx() throws IOException {
    txId = UUID.randomUUID();
    final String payload = new JSONObject()
      .put("state", "ACTIVE")
      .put("type", "OTHER")
      .put("client_id", clientId)
      .put("data", new JSONObject()
        .put("aeao", new JSONObject()
          .put("other", new JSONObject())
        )
      )
      .toString();
    final RequestBody body = RequestBody.create(payload, MediaType.parse("application/json"));
    final Request request = new Request.Builder().url(BASE_URL + "tss/" + tssId + "/tx/" + txId).put(body)
      .build();
    client.newCall(request).execute();
  }

  @BeforeClass
  public static void setup() throws IOException {
    final String apiKey = System.getenv("API_KEY");
    final String apiSecret = System.getenv("API_SECRET");
    client = getClient(apiKey, apiSecret, SmaProvider.getSma());

    setupTss();
    setupClient();
    setupTx();
  }

  /*

  Every code example is assumed to start with the following code:

  final String apiKey = System.getenv("API_KEY");
  final String apiSecret = System.getenv("API_SECRET");
  final OkHttpClient client = getClient(apiKey, apiSecret, new GeneralSMA());

  */

  @Test
  public void listAllClients() throws IOException {
    final Request request = new Request.Builder()
      .url(BASE_URL + "client")
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assertEquals("CLIENT_LIST", obj.getString("_type"));
  }
}

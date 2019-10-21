import okhttp3.*;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.fiskaly.kassensichv.client.ClientFactory.getClient;
import static org.junit.Assert.*;

public class ApiCodeExamplesTests {
  private static final String BASE_URL = "https://kassensichv.io/api/v0/";
  private static UUID tssId;
  private static UUID clientId;
  private static UUID txId;
  private static UUID exportId;
  private static OkHttpClient client;
  private static String serialNumber;

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
    serialNumber = clientId.toString();
    final String payload = new JSONObject()
      .put("serial_number", serialNumber)
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

  private static void setupExport() throws IOException {
    exportId = UUID.randomUUID();
    final RequestBody body = RequestBody.create("{}", MediaType.parse("application/json"));
    final Request request = new Request.Builder().url(BASE_URL + "tss/" + tssId + "/export/" + exportId).put(body)
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
    setupExport();
  }

  /*

  Every code example is assumed to start with the following code:

  final String apiKey = System.getenv("API_KEY");
  final String apiSecret = System.getenv("API_SECRET");
  final OkHttpClient client = getClient(apiKey, apiSecret, new GeneralSMA());

  */

  @Test
  public void listAllClients() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "client")
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals("CLIENT_LIST", obj.getString("_type"));
  }

  @Test
  public void listClients() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/client")
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals("CLIENT_LIST", obj.getString("_type"));
  }

  @Test
  public void retrieveClient() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    // final String clientId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/client/" + clientId)
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals(clientId.toString(), obj.getString("_id"));
    assertEquals("CLIENT", obj.getString("_type"));
  }

  @Test
  public void upsertClient() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    // final String clientId = "...";
    // final String serialNumber = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final String payload = new JSONObject()
      .put("serial_number", serialNumber)
      .toString();
    final RequestBody body = RequestBody.create(payload, MediaType.parse("application/json"));
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/client/" + clientId)
      .put(body)
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals(clientId.toString(), obj.getString("_id"));
    assertEquals("CLIENT", obj.getString("_type"));
  }

  @Test
  public void cancelExport() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    // final String exportId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final RequestBody body = RequestBody.create("{}", MediaType.parse("application/json"));
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/export/" + exportId)
      .delete(body)
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals(exportId.toString(), obj.getString("_id"));
    assertEquals("EXPORT", obj.getString("_type"));
  }

  @Test
  public void listAllExports() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "export")
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();

    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals("EXPORT_LIST", obj.getString("_type"));
  }

  @Test
  public void listExports() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/export")
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals("EXPORT_LIST", obj.getString("_type"));
  }

  @Test
  public void retrieveExport() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    // final String exportId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/export/" + exportId)
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals(exportId.toString(), obj.getString("_id"));
    assertEquals("EXPORT", obj.getString("_type"));
  }

  @Test
  public void triggerExport() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    final UUID exportId = UUID.randomUUID();
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final RequestBody body = RequestBody.create("{}", MediaType.parse("application/json"));
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/export/" + exportId)
      .put(body)
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals(exportId.toString(), obj.getString("_id"));
    assertEquals("EXPORT", obj.getString("_type"));
  }

  @Test
  public void listTss() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss")
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals("TSS_LIST", obj.getString("_type"));
  }

  @Test
  public void retrieveTss() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId)
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals(tssId.toString(), obj.getString("_id"));
    assertEquals("TSS", obj.getString("_type"));
  }

  @Test
  public void upsertTss() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final String payload = new JSONObject()
      .put("state", "INITIALIZED")
      .toString();
    final RequestBody body = RequestBody.create(payload, MediaType.parse("application/json"));
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId)
      .put(body)
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals(tssId.toString(), obj.getString("_id"));
    assertEquals("TSS", obj.getString("_type"));
  }

  @Test
  public void listAllTransactions() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tx")
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals("TRANSACTION_LIST", obj.getString("_type"));
  }

  @Test
  public void listTransactions() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/tx")
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals("TRANSACTION_LIST", obj.getString("_type"));
  }

  @Test
  public void retrieveTransaction() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    // final String txId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/tx/" + txId)
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals(txId.toString(), obj.getString("_id"));
    assertEquals("TRANSACTION", obj.getString("_type"));
  }

  @Test
  public void retrieveTransactionLog() throws IOException {
    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    // final String txId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
    final Request request = new Request.Builder()
      .url(BASE_URL + "tss/" + tssId + "/tx/" + txId)
      .get()
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    InputStream inputStream = response.body().byteStream();

    // The code example ends here.

    assert(response.isSuccessful());
    assertNotEquals(null, inputStream);
    inputStream.close();
  }

  @Test
  public void upsertTransaction() throws IOException {
    final String lastRevision = "1";

    // The static BASE_URL is not used intentionally
    // (to keep the code example inherently consistent).

    // The code example starts here.

    // final String tssId = "...";
    // final String txId = "...";
    // final String clientId = "...";
    final String BASE_URL = "https://kassensichv.io/api/v0/";
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
    final HttpUrl url = HttpUrl.parse(BASE_URL + "tss/" + tssId + "/tx/" + txId).newBuilder()
      .addQueryParameter("last_revision", lastRevision)
      .build();
    final RequestBody body = RequestBody.create(payload, MediaType.parse("application/json"));
    final Request request = new Request.Builder()
      .url(url)
      .put(body)
      .build();
    final Response response = client
      .newCall(request)
      .execute();
    JSONObject obj = new JSONObject(response.body().string());

    // The code example ends here.

    assert(response.isSuccessful());
    assertEquals(txId.toString(), obj.getString("_id"));
    assertEquals("TRANSACTION", obj.getString("_type"));
  }
}

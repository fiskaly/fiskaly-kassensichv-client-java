import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.client.interceptors.HeaderInterceptor;
import com.fiskaly.kassensichv.client.interceptors.TransactionInterceptor;
import com.fiskaly.kassensichv.sma.SmaInterface;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuthenticationRetryRequestTest {

    private static final String apiKey = System.getenv("API_KEY");
    private static final String apiSecret = System.getenv("API_SECRET");

    private static TestTokenManager tokenManager;

    private static OkHttpClient getClient(String apiKey, String secret, SmaInterface sma) {
        tokenManager = new TestTokenManager(
                new OkHttpClient
                        .Builder()
                        .build(),
                apiKey,
                secret);

        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new TestAuthenticationInterceptor(tokenManager))
                .addInterceptor(new TransactionInterceptor(sma))
                .authenticator(new TestAuthenticator(tokenManager))
                .build();

        return client;
    }

    @Test(expected = IOException.class)
    public void AR01_GivenNoApiPairWhenSendingRequestExpectException() throws Exception {

        OkHttpClient client = getClient(null, null, SmaProvider.getSma());

        final String BASE_URL = "https://kassensichv.io/api/v0/";
        final Request request = new Request.Builder()
                .url(BASE_URL + "client")
                .get()
                .build();
        final Response response = client
                .newCall(request)
                .execute();

    }

    @Test(expected = IOException.class)
    public void AR02_GivenWrongApiPairWhenSendingRequestExpectException() throws Exception {

        OkHttpClient client = getClient("IAMAKEY", "IAMASECRET", SmaProvider.getSma());

        final String BASE_URL = "https://kassensichv.io/api/v0/";
        final Request request = new Request.Builder()
                .url(BASE_URL + "client")
                .get()
                .build();
        final Response response = client
                .newCall(request)
                .execute();

    }

    @Test
    public void AR03_GivenCorrectApiPairWhenSendingRequestExpectSuccess() throws Exception {

        OkHttpClient client = getClient(apiKey, apiSecret, SmaProvider.getSma());

        final String BASE_URL = "https://kassensichv.io/api/v0/";
        final Request request = new Request.Builder()
                .url(BASE_URL + "client")
                .get()
                .build();
        final Response response = client
                .newCall(request)
                .execute();

    }

    @Test
    public void AR04_GivenCorrectApiPairWhenInvalidAccessTokenAndSendingRequestExpectSuccess() throws Exception {

        OkHttpClient client = getClient(apiKey, apiSecret, SmaProvider.getSma());

        final String BASE_URL = "https://kassensichv.io/api/v0/";
        final Request request = new Request.Builder()
                .url(BASE_URL + "client")
                .get()
                .build();
        final Response response = client
                .newCall(request)
                .execute();

        tokenManager.setAccessToken("invalidToken");

        final Response response2 = client
                .newCall(request)
                .execute();

    }

    @Test
    public void AR05_GivenCorrectApiPairWhenInvalidAccessTokenAndInvalidRefreshTokenAndSendingRequestExpectSuccess() throws Exception {

        OkHttpClient client = getClient(apiKey, apiSecret, SmaProvider.getSma());

        final String BASE_URL = "https://kassensichv.io/api/v0/";
        final Request request = new Request.Builder()
                .url(BASE_URL + "client")
                .get()
                .build();
        final Response response = client
                .newCall(request)
                .execute();

        tokenManager.setAccessToken("invalidToken");
        tokenManager.setRefreshToken("invalidToken");

        final Response response2 = client
                .newCall(request)
                .execute();
    }

}

class TestTokenManager implements Runnable {
    private OkHttpClient client;

    private final String apiKey;
    private final String secret;

    private String accessToken;
    private String refreshToken;

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final ObjectMapper mapper = new ObjectMapper();

    public TestTokenManager(OkHttpClient client, String apiKey, String secret) {
        this.client = client;

        this.apiKey = apiKey;
        this.secret = secret;
    }

    private synchronized void setTokenPair(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public synchronized String getAccessToken() {
        return this.accessToken;
    }

    /**
     * Parses the response of the authentication request
     * and extracts the token pair
     * @param responseBody Response body of the authentication request
     * @throws IOException In case of the body not being parsable
     */
    private synchronized void setTokenPairFromResponseBody(String responseBody) throws IOException {
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
        if (this.accessToken == null) {
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
                            "\"refresh_token\": \"" + this.refreshToken + "\"" +
                            "}";
            body = RequestBody.create(jsonBody, JSON);
        }

        request = new Request
                .Builder()
                .url("https://kassensichv.io/api/v0/auth")
                .post(body)
                .build();
        try(Response response = this.client.newCall(request).execute()) {
            if(response.isSuccessful()) {
                this.setTokenPairFromResponseBody(
                        response
                                .body()
                                .string()
                );
            } else if(this.accessToken != null){
                System.err.println("refresh_token seems to be incorrect or expired. Falling back to authorization with API-Key and API-Secret.");
                this.accessToken = null;
                this.fetchTokenPair();
            } else {
                System.err.println("Bad Credentials.");
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }


    }

    public synchronized void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public synchronized void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Gets called when token needs to be refreshed
    @Override
    public void run() {
        fetchTokenPair();
    }
}

class TestAuthenticationInterceptor implements Interceptor {
    private final ScheduledExecutorService executor;
    private final TestTokenManager tokenManager;

    public TestAuthenticationInterceptor(final TestTokenManager tokenManager) {
        this.tokenManager = tokenManager;
        this.executor = Executors.newScheduledThreadPool(1);

        this.startTokenManagementTask();
    }

    /**
     * Starts an async task that is responsible
     * for keeping the access token-pair active
     */
    private void startTokenManagementTask() {
        final int INITIAL_DELAY = 0;
        final int REFRESH_INTERVAL = 30;

        this.executor
                .scheduleWithFixedDelay(
                        this.tokenManager,
                        INITIAL_DELAY,
                        REFRESH_INTERVAL,
                        TimeUnit.SECONDS
                );
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request interceptedRequest = request
                .newBuilder()
                .header("Authorization", "Bearer " + this.tokenManager.getAccessToken())
                .build();

        return chain.proceed(interceptedRequest);
    }
}

class TestAuthenticator implements okhttp3.Authenticator {
    private TestTokenManager tokenManager;

    public TestAuthenticator(TestTokenManager tokenManager) {
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
        this.tokenManager.fetchTokenPair();

        return response.request().newBuilder()
                .header("Authorization", "Bearer " + this.tokenManager.getAccessToken())
                .header("X-Authorization-Retry", "1")
                .build();
    }
}

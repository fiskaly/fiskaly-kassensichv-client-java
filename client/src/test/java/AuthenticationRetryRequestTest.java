import com.fiskaly.kassensichv.client.authentication.TokenManager;
import com.fiskaly.kassensichv.client.authentication.Authenticator;
import com.fiskaly.kassensichv.client.interceptors.AuthenticationInterceptor;
import com.fiskaly.kassensichv.client.interceptors.HeaderInterceptor;
import com.fiskaly.kassensichv.client.interceptors.TransactionInterceptor;
import com.fiskaly.kassensichv.sma.SmaInterface;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class AuthenticationRetryRequestTest {

    private static final String apiKey = System.getenv("API_KEY");
    private static final String apiSecret = System.getenv("API_SECRET");

    private static TokenManager.TokenHolder tokenHolder;
    private static TokenManager tokenManager;

    private static OkHttpClient getClient(String apiKey, String secret, SmaInterface sma) {
        tokenManager = new TokenManager(
                new OkHttpClient
                        .Builder()
                        .build(),
                apiKey,
                secret,
                tokenHolder);

        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new AuthenticationInterceptor(tokenManager))
                .addInterceptor(new TransactionInterceptor(sma))
                .authenticator(new Authenticator(tokenManager))
                .build();

        return client;
    }

    @Before
    public void resetTokenHolder() {
        tokenHolder = new TokenManager.TokenHolder();
    }

    @Test(expected = IOException.class)
    public void AR01_GivenNoApiPairWhenSendingRequestExpectException() throws Exception {

        OkHttpClient client = getClient(null, null, SmaProvider.getSma());

        final String BASE_URL = "https://kassensichv.io/api/v0/";
        final Request request = new Request.Builder()
                .url(BASE_URL + "client")
                .get()
                .build();
        client.newCall(request).execute();

    }

    @Test(expected = IOException.class)
    public void AR02_GivenWrongApiPairWhenSendingRequestExpectException() throws Exception {

        OkHttpClient client = getClient("IAMAKEY", "IAMASECRET", SmaProvider.getSma());

        final String BASE_URL = "https://kassensichv.io/api/v0/";
        final Request request = new Request.Builder()
                .url(BASE_URL + "client")
                .get()
                .build();
        client.newCall(request).execute();

    }

    @Test
    public void AR03_GivenCorrectApiPairWhenSendingRequestExpectSuccess() throws Exception {

        OkHttpClient client = getClient(apiKey, apiSecret, SmaProvider.getSma());

        final String BASE_URL = "https://kassensichv.io/api/v0/";
        final Request request = new Request.Builder()
                .url(BASE_URL + "client")
                .get()
                .build();
        client.newCall(request).execute();

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

        tokenHolder.accessToken = "invalidToken";

        client.newCall(request).execute();

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

        tokenHolder.accessToken = "invalidToken";
        tokenHolder.refreshToken = "invalidToken";

        client.newCall(request).execute();
    }

}
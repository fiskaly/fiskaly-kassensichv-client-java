package com.fiskaly.kassensichv.client.interceptors;

import com.fiskaly.kassensichv.client.authentication.TokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuthenticationInterceptor implements Interceptor {
    private final ScheduledExecutorService executor;
    private final TokenManager tokenManager;

    public AuthenticationInterceptor(final TokenManager tokenManager) {
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

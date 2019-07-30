package com.fiskaly.kassensichv.client.interceptors;

import com.fiskaly.kassensichv.client.authentication.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.*;

public class AuthenticationInterceptor implements Interceptor {
    private final ObjectMapper mapper;

    private final ScheduledExecutorService executor;
    private final TokenManager tokenManager;

    public AuthenticationInterceptor(final TokenManager tokenManager) {
        this.mapper = new ObjectMapper();

        this.tokenManager = tokenManager;
        this.executor = Executors.newScheduledThreadPool(1);

        this.startTokenManagementTask();
    }

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

package com.fiskaly.kassensichv.client.interceptors;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Overwrites the user agent and sets the appropriate content-type header
 */
public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        request = request
                .newBuilder()
                .header("User-Agent", "fiskaly-kassensichv-client-java/0.0.1 (https://fiskaly.com)")
                .header("Content-Type", "application/json")
                .build();

        return chain.proceed(request);
    }
}

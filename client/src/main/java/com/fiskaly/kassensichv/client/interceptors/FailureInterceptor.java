package com.fiskaly.kassensichv.client.interceptors;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import persistence.PersistenceStrategy;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.List;

public class FailureInterceptor implements Interceptor {
    private PersistenceStrategy strategy;

    public FailureInterceptor(PersistenceStrategy strategy) {
        this.strategy = strategy;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        try {
            Response response = chain.proceed(chain.request());

            return response;
        } catch (ProtocolException e) {
            Request request = chain.request();

            Buffer sink = new Buffer();
            String body = "";

            if (request.body() != null) {
                request.body().writeTo(sink);
                body = sink.readUtf8();
                sink.close();
            }

            this.strategy.persistRequest(
                    new com.fiskaly.kassensichv.client.persistence
                            .Request(request.url().toString(), body, request.method())
            );

            List<com.fiskaly.kassensichv.client.persistence.Request> requests = this.strategy.loadRequests();

            requests.forEach(System.out::println);

            throw e;
        }

    }
}

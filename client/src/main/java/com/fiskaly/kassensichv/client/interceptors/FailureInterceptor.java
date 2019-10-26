package com.fiskaly.kassensichv.client.interceptors;

import com.fiskaly.kassensichv.persistence.PersistenceStrategy;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ProtocolException;

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
                    new com.fiskaly.kassensichv.persistence.Request(request.url().toString(), body, request.method())
            );

            throw e;
        }
    }
}

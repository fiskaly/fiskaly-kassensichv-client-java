package com.fiskaly.kassensichv.client.interceptors;

import com.fiskaly.kassensichv.client.persistence.PersistenceStrategy;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ProtocolException;

public class FailureInterceptor implements Interceptor {
    public FailureInterceptor(PersistenceStrategy strategy) {}

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        try {
            Response response = chain.proceed(chain.request());
        } catch (ProtocolException e) {
            // persist call
            throw e;
        }

        return null;
    }
}

package com.fiskaly.kassensichv.client.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.client.sma.SmaLibrary;
import com.fiskaly.kassensichv.client.sma.SmaLoader;
import jnr.ffi.Pointer;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TransactionInterceptor implements Interceptor {
    final ArrayList<String> AFFECTED_VERBS = new ArrayList<>(Arrays.asList("PUT"));
    final SmaLibrary library;
    final ObjectMapper mapper;

    public TransactionInterceptor() {
        this.library = SmaLoader.load();
        this.mapper = new ObjectMapper();
    }

    private String signTransaction(String requestBody) throws IOException {
        String requestJson = mapper.writeValueAsString(requestBody);
        Pointer pointer = library.Invoke(requestJson);
        String response = pointer.getString(0);

        library.Free(pointer); // this is important, otherwise we'll leak memory

        return response;
    }

    private Request createReroutedRequest(Request request) {
        String originalUrl = request.url().toString();

        List<String> parts = Arrays.asList(originalUrl.split("\\?"));

        String host = parts.remove(0);
        String newUrl = String.join(host + "/log", parts);

        HttpUrl url = request
                .url()
                .newBuilder()
                .host(newUrl)
                .build();

        request = request
                .newBuilder()
                .url(url)
                .build();

        return request;
    }

    private Request replaceBody(Request request, String signedBody) {
        MediaType type = request.body().contentType();

        RequestBody body = RequestBody.create(signedBody, type);

        request = request
                .newBuilder()
                .put(body)
                .build();

        return request;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (!request.url().toString().contains("/tx/")) {
            return chain.proceed(chain.request());
        }

        if (!this.AFFECTED_VERBS.contains(request.method().toUpperCase())) {
            return chain.proceed(chain.request());
        }

        request = this.createReroutedRequest(request);
        String signedBody = this.signTransaction(request.body().toString());

        request = this.replaceBody(request, signedBody);

        return chain.proceed(request);
    }
}

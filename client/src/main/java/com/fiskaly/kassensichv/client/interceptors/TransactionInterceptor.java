package com.fiskaly.kassensichv.client.interceptors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.sma.SMAInterface;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Intercepts requests that target the transaction resource
 * and swaps the request body with a version that's signed
 * by the passed SMAInterface implementation
 */
public class TransactionInterceptor implements Interceptor {
    private final SMAInterface sma;
    private final ObjectMapper mapper;

    public TransactionInterceptor(SMAInterface sma) {
        this.sma = sma;
        this.mapper = new ObjectMapper();
    }

    private String signTransaction(String requestBody) throws IOException {
        Map<String, Object> jsonRpcMap = new HashMap<>();

        Map<String, Object> smaPayload = mapper.readValue(requestBody,
                new TypeReference<Map<String, Object>>(){});

        jsonRpcMap.put("jsonrpc", "2.0");
        jsonRpcMap.put("method", "sign-transaction");
        jsonRpcMap.put("params", new Object[] { smaPayload });

        String smaRequest = mapper.writeValueAsString(jsonRpcMap);

        String jsonRpcResponse = sma.invoke(smaRequest);
        Map<String, Object> resultMap = mapper
                .readValue(jsonRpcResponse, new TypeReference<Map<String, Object>>(){});

        return mapper.writeValueAsString(resultMap.get("result"));
    }

        String rewriteRoute(String sourceUrl) {
        List<String> parts = new LinkedList<>(Arrays.asList(sourceUrl.split("\\?")));

        String host = parts.remove(0);

        String queryList = "";

        if(!parts.isEmpty()){
            queryList += parts.remove(0);

            for (String part : parts) {
                queryList += ("?" + part);
            }
        }

        if (!queryList.isEmpty()) {
            queryList = "?" + queryList;
        }

        String targetUrl = host + "/log" + queryList;

        return targetUrl;
    }


    private Request createReroutedRequest(Request request) {
        String sourceUrl = request
                .url()
                .toString();

        String targetUrl = rewriteRoute(sourceUrl);
        
        HttpUrl url = HttpUrl.parse(targetUrl);

        request = request
                .newBuilder()
                .url(url)
                .build();

        return request;
    }

    private Request swapBody(Request request, String signedBody) {
        MediaType type = request.body().contentType();

        RequestBody body = RequestBody.create(signedBody, type);

        request = request
                .newBuilder()
                .put(body)
                .build();

        return request;
    }

    private String getRequestBodyAsString(RequestBody body) throws IOException {
        Buffer bodyBuffer = new Buffer();
        body.writeTo(bodyBuffer);
        return bodyBuffer.readString(Charset.defaultCharset());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        final String requestUrl = request
                .url()
                .toString();

        final String requestMethod = request
                .method()
                .toUpperCase();

        if (!requestUrl.contains("/tx/")) {
            return chain.proceed(chain.request());
        }

        if (!"PUT".equalsIgnoreCase(requestMethod)) {
            return chain.proceed(chain.request());
        }

        request = this.createReroutedRequest(request);

        String signedBody = this.signTransaction(getRequestBodyAsString(request.body()));

        request = this.swapBody(request, signedBody);

        return chain.proceed(request);
    }
}

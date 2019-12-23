package com.fiskaly.kassensichv.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.sma.SmaInterface;
import okhttp3.*;

import java.util.Map;

public class Client {

    private static String BASE_URL = "https://kassensichv.io/api/v0/";
    private static final ObjectMapper mapper = new ObjectMapper();

    private OkHttpClient client;

    public Client(String apiKey, String apiSecret, SmaInterface sma) {
        this.client = ClientFactory.getClient(apiKey, apiSecret, sma);
    }

    public String request(String method, String path)
        throws Exception {
            this.request(method, path, null, null, null);
    }

    public String request(String method, String path, Map<String, ?> query)
        throws Exception {
            this.request(method, path, null, query, null);
    }

    public String request(String method, String path, Map<String, ?> body)
        throws Exception {
            this.request(method, path, body, null, null);
    }

    public String request(String method, String path, Map<String, ?> body, Map<String, ?> query)
        throws Exception {
            this.request(method, path, body, query, null);
    }

    public String request(String method, String path, Map<String, ?> body, Map<String, ?> query, Map<String, String> headers)
        throws Exception {

        RequestBody parsedBody = null;
        Request.Builder builder = new Request.Builder();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();

        if(query != null && !query.isEmpty()){
            for (Map.Entry<String, ?> entry : query.entrySet()) {
                httpBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        if(body != null) {
            String jsonBody = mapper.writeValueAsString(body);
            parsedBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        }

        if(headers != null) {
            builder.headers(Headers.of(headers));
        }

        builder.url(httpBuilder.build());

        Request request;

        switch (method) {
            case "PUT":
                request = builder.put(parsedBody).build();
            break;
            case "POST":
                request = builder.post(parsedBody).build();
            break;
            case "DELETE":
                request = builder.delete(parsedBody).build();
            break;
            case "GET":
                request = builder.get().build();
            break;
            default:
                throw new Exception("Unsupported Http Method");
        }

        final Response response = client
                .newCall(request)
                .execute();

        return response.body().string();

    }
}

package com.fiskaly.kassensichv.demo.interceptors;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResponseTimeLoggingInterceptor implements Interceptor  {
    private class LoggingRecord {
        private String timestamp;
        private String requestId;
        private String method;
        private String url;
        private long clientDelta;
        private long serverDelta;

        public LoggingRecord(String timestamp, String requestId, String method,
                             String url, long clientDelta, long serverDelta) {
            this.timestamp = timestamp;
            this.requestId = requestId;
            this.method = method;
            this.url = url;
            this.clientDelta = clientDelta;
            this.serverDelta = serverDelta;
        }

        @Override
        public String toString() {
            return timestamp + ";"
                    + requestId + ";"
                    + method + ";"
                    + url + ";"
                    + clientDelta + Long.toString(clientDelta) + ";"
                    + serverDelta + Long.toString(serverDelta);
        }
    }

    private OutputStream logStream;
    private OutputStreamWriter writer;

    public ResponseTimeLoggingInterceptor(OutputStream logStream) {
        this.logStream = logStream;
        this.writer = new OutputStreamWriter(logStream, Charset.forName(StandardCharsets.UTF_8.name()));
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        System.out.println("Intercept");
        Request request = chain.request();
        Headers requestHeaders = request.headers();

        String method = request.method();
        String url = request.url().toString();

        long start = System.currentTimeMillis();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        Response response = chain.proceed(request);

        long clientDelta = System.currentTimeMillis()- start;

        Headers responseHeaders = response.headers();

        String requestId = responseHeaders.get("x-request-id");
        String responseTimeHeader = responseHeaders.get("x-response-time");
        long serverDelta = responseTimeHeader == null ? -1 : Long.valueOf(responseTimeHeader);

        LoggingRecord record = new LoggingRecord(timestamp, requestId, method, url, clientDelta, serverDelta);

        writer.append(record.toString() + "\n");
        writer.flush(); // ensure write in case of a crash

        return response;
    }
}

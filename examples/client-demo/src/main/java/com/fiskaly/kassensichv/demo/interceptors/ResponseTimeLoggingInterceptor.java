package com.fiskaly.kassensichv.demo.interceptors;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

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
                    + Long.toString(clientDelta) + ";"
                    + Long.toString(serverDelta);
        }
    }

    private OutputStreamWriter writer;
    private OutputStreamWriter errorWriter;

    public ResponseTimeLoggingInterceptor(OutputStream logStream, OutputStream errorStream) {
        this.writer = new OutputStreamWriter(logStream, Charset.forName(StandardCharsets.UTF_8.name()));
        this.errorWriter = new OutputStreamWriter(errorStream, Charset.forName(StandardCharsets.UTF_8.name()));
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String method = request.method();
        String url = request.url().toString();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS")
                .format(new Date());

        Response response = chain.proceed(request);

        long clientDelta = response.receivedResponseAtMillis() - response.sentRequestAtMillis();

        Headers responseHeaders = response.headers();

        String requestId = responseHeaders.get("x-request-id");
        String responseTimeHeader = responseHeaders.get("x-response-time");
        long serverDelta = responseTimeHeader == null ? -1 : Long.valueOf(responseTimeHeader);

        if (!response.isSuccessful()) {
            this.errorWriter.append(
                    timestamp + ";" + requestId + ";" + method + ";" +
                    url + ";" + response.code() + "\n"
            );
            this.errorWriter.flush();

            return response;
        }

        LoggingRecord record = new LoggingRecord(timestamp, requestId, method, url, clientDelta, serverDelta);

        writer.append(record.toString() + "\n");
        writer.flush(); // ensure write in case of a crash

        return response;
    }
}

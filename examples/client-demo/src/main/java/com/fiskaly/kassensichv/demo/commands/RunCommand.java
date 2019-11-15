package com.fiskaly.kassensichv.demo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.demo.interceptors.ResponseTimeLoggingInterceptor;
import okhttp3.*;
import picocli.CommandLine;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

@CommandLine.Command()
public class RunCommand implements Callable<Void> {
    private String apiKey;
    private String apiSecret;
    private OkHttpClient client;

    public RunCommand(String apiKey, String apiSecret, OkHttpClient client) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.client = client;
    }

    @Override
    public Void call() throws Exception {
        return runTests();
    }

    @CommandLine.Command(name = "test")
    public Void runTests() throws IOException {
        System.out.println("Creating TSS...");
        final String tssId = createTss(client);
        System.out.println("Created TSS: " + tssId);

        System.out.println("Creating client...");
        final String clientId = createClient(client, tssId);
        System.out.println("Created client: " + clientId);

        System.out.println("Performing transaction...");
        performTransaction(client, tssId, clientId);
        System.out.println("Performed transaction");

        System.out.println("Successfully ran client tests");

        return null;
    }

    @CommandLine.Command(name = "monitor")
    public Void runMonitoring(
        @CommandLine.Option(
                names = {"-o", "--out-file"},
                required = false,
                description = "Path to file which will contain all logging information"
        ) String outPath,

        @CommandLine.Option(
                names = {"-e", "--error-out-file"},
                required = false,
                description = "Path to file which will contain all error logs"
        ) String errPath,

        @CommandLine.Option(
            names = {"-i", "--interval"},
            required = false,
            description = "Interval (ms) in which requests get issued",
            defaultValue = "1000"
        ) long requestInterval,

        @CommandLine.Option(
                names = {"-h", "--help"},
                usageHelp = true
        ) boolean help
    ) throws IOException{
        OutputStream outStream = null;
        OutputStream errStream = null;

        if (outPath == null) {
            outStream = System.out;
        } else {
            try {
                outStream = new FileOutputStream(outPath, true);
            } catch (FileNotFoundException e) {
                System.err.println("Specified log file could not be opened: " + e.getMessage());
                e.printStackTrace();

                System.exit(1);
            }
        }

        if (errPath == null) {
            errStream = System.err;
        } else {
            try {
                errStream = new FileOutputStream(errPath, true);
            } catch (FileNotFoundException e) {
                System.err.println("Specified error log file could not be opened: " + e.getMessage());
                e.printStackTrace();

                System.exit(1);
            }
        }

        OutputStreamWriter errorWriter = new OutputStreamWriter(errStream, Charset.forName(StandardCharsets.UTF_8.name()));

        this.client = client
                .newBuilder()
                .addNetworkInterceptor(new ResponseTimeLoggingInterceptor(outStream, errStream))
                .build();

        try {
            System.out.println("Creating TSS...");
            final String tssId = createTss(client);
            System.out.println("Created TSS: " + tssId);

            System.out.println("Creating client...");
            final String clientId = createClient(client, tssId);
            System.out.println("Created client: " + clientId);

            for (;;) {
                System.out.println("Performing transaction...");
                performTransaction(client, tssId, clientId);
                System.out.println("Performed transaction");

                Thread.sleep(requestInterval);
            }
        } catch (IOException ioe) {
            System.out.println("ioe");
            errorWriter.append("Request failed: " + ioe.getMessage());

            for (StackTraceElement trace : ioe.getStackTrace()) {
                errorWriter.append(trace.toString() + "\n");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();

            for (StackTraceElement trace : e.getStackTrace()) {
                errorWriter.append(trace.toString() + "\n");
            }
        }

        return null;
    }

    public String createTss(OkHttpClient client) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> tssMap = new HashMap<>();

        tssMap.put("state", "INITIALIZED");
        tssMap.put("description", "TSS created by the Fiskaly Java example app");

        final String jsonBody = mapper.writeValueAsString(tssMap);
        final UUID tssId = UUID.randomUUID();

        final Request createTssRequest = new Request.Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId)
                .put(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        client
                .newCall(createTssRequest)
                .execute()
                .close();

        return tssId.toString();
    }

    public String createClient(OkHttpClient client, String tssId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> clientMap = new HashMap<>();

        final UUID clientId = UUID.randomUUID();

        clientMap.put("serial_number", clientId.toString());
        String jsonBody = mapper.writeValueAsString(clientMap);

        final Request createClientRequest = new Request.Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId + "/client/" + clientId)
                .put(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        client
                .newCall(createClientRequest)
                .execute()
                .close();

        return clientId.toString();
    }

    public void performTransaction(OkHttpClient client, String tssId, String clientId) throws IOException {
        Map<String, Object> transactionMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        String txId = UUID.randomUUID().toString();

        transactionMap.put("client_id", clientId);
        transactionMap.put("type", "RECEIPT");
        transactionMap.put("state", "ACTIVE");

        Map<String, Object> amountsPerVatRateMap = new HashMap<>();
        amountsPerVatRateMap.put("vat_rate", "19");
        amountsPerVatRateMap.put("amount", "10.0");

        Map<String, Object> amountPerPaymentTypeMap = new HashMap<>();
        amountPerPaymentTypeMap.put("payment_type", "CASH");
        amountPerPaymentTypeMap.put("amount", "10.0");

        String jsonBody = mapper.writeValueAsString(transactionMap);

        final Request createTransaction = new Request.Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId + "/tx/" + txId)
                .put(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        client
                .newCall(createTransaction)
                .execute()
                .close();
    }
}

package com.fiskaly.kassensichv.demo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.demo.interceptors.ResponseTimeLoggingInterceptor;
import okhttp3.*;
import picocli.CommandLine;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    ) throws IOException {
        OutputStream outStream;
        OutputStream errStream;

        if (outPath == null) {
            outStream = System.out;
        } else {
            outStream = new FileOutputStream(outPath, true);
        }

        if (errPath == null) {
            errStream = System.err;
        } else {
            errStream = new FileOutputStream(errPath, true);
        }

        this.client = client
                .newBuilder()
                .addNetworkInterceptor(new ResponseTimeLoggingInterceptor(outStream))
                .build();

        System.out.println("Creating TSS...");
        final String tssId = createTss(client);
        System.out.println("Created TSS: " + tssId);

        System.out.println("Creating client...");
        final String clientId = createClient(client, tssId);
        System.out.println("Created client: " + clientId);

        try {
            for (;;) {
                System.out.println("Performing transaction...");
                performTransaction(client, tssId, clientId);
                System.out.println("Performed transaction");

                Thread.sleep(requestInterval);
            }
        } catch (IOException ioe) {
            System.err.println("Transaction failed: " + ioe.getMessage());
            ioe.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                .url("https://kassensichv.io/api/v0/tss" + tssId)
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

        clientMap.put("serial_number", "fiskaly-java-serial-number");
        String jsonBody = mapper.writeValueAsString(clientMap);

        final UUID clientId = UUID.randomUUID();

        final Request createClientRequest = new Request.Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId + "/client/" + clientId)
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
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

        Map<String, Object> transactionDataMap = new HashMap<>();

        transactionMap.put("data", transactionDataMap);

        Map<String, Object> aeaoMap = new HashMap<>();
        aeaoMap.put("receipt_type", "RECEIPT");

        Map<String, Object> amountsPerVatRateMap = new HashMap<>();
        amountsPerVatRateMap.put("vat_rate", "19");
        amountsPerVatRateMap.put("amount", "10.0");

        aeaoMap.put("amounts_per_vat_rate", new Object[] { amountsPerVatRateMap });

        Map<String, Object> amountPerPaymentTypeMap = new HashMap<>();
        amountPerPaymentTypeMap.put("payment_type", "CASH");
        amountPerPaymentTypeMap.put("amount", "10.0");

        aeaoMap.put("amount_per_payment_type", amountPerPaymentTypeMap);

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

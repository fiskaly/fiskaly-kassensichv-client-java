package com.fiskaly.kassensichv.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.client.ClientFactory;
import com.fiskaly.kassensichv.sma.GeneralSMA;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws IOException {
        final String API_KEY = System.getenv("API_KEY");
        final String API_SECRET = System.getenv("API_SECRET");

        boolean envCorrect = true;

        if (API_KEY == null) {
            envCorrect = false;

            System.err.println("Please provide a value for the 'API_KEY' environment variable!");
        }

        if (API_SECRET == null) {
            envCorrect = false;

            System.err.println("Please provide a value for the 'API_SECRET' environment variable!");
        }

        if (!envCorrect) {
            System.exit(1);
        }

        System.out.println("Instantiating client...");

        OkHttpClient client = ClientFactory.getClient(
                API_KEY,
                API_SECRET,
                new GeneralSMA()
        );

        System.out.println("Instantiated client");

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

        System.exit(0);
    }

    public static String createTss(OkHttpClient client) throws IOException {
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
            .execute();

        return tssId.toString();
    }

    public static String createClient(OkHttpClient client, String tssId) throws IOException {
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
            .execute();

        return clientId.toString();
    }

    public static void performTransaction(OkHttpClient client, String tssId, String clientId) throws IOException {
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
            .execute();
    }
}

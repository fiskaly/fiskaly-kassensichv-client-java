package com.fiskaly.sdk.androidsdkdemo;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestFactory {
    private ObjectMapper mapper;

    public RequestFactory() {
        this.mapper = new ObjectMapper();
    }

    public Request buildCreateTssRequest(String uuid) throws JsonProcessingException {
        Map<String, String> tssMap = new HashMap<>();

        tssMap.put("state", "INITIALIZED");
        tssMap.put("description", "TSS created by test case createTSS");

        String jsonBody = mapper.writeValueAsString(tssMap);

        return new Request.Builder()
                .url("https://kassensichv.io/api/v0/tss" + uuid)
                .put(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();
    }

    public Request buildCreateClientRequest(String tssId, String clientId) throws JsonProcessingException {
        Map<String, String> clientMap = new HashMap<>();

        clientMap.put("serial_number", clientId);
        String jsonBody = mapper.writeValueAsString(clientMap);

        return new Request.Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId + "/client/" + clientId)
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();
    }

    public Request buildTriggerExportRequest(String tssId, String exportId) {
        return new Request.Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId + "/export/" + exportId)
                .put(RequestBody.create("", MediaType.parse("application/json")))
                .build();
    }

    public Request buildCreateTransactionRequest(String tssId, String txId, String clientId) throws JsonProcessingException {
        Map<String, Object> transactionMap = new HashMap<>();

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

        transactionDataMap.put("aeao", aeaoMap);

        String jsonBody = mapper.writeValueAsString(transactionMap);

        Log.d("JSON", jsonBody);

        return new Request.Builder()
                .url("https://kassensichv.io/api/v0/tss/" + tssId + "/tx/" + txId)
                .put(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();
    }
}

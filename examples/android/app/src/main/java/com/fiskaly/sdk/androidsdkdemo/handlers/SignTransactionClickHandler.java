package com.fiskaly.sdk.androidsdkdemo.handlers;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.client.ClientFactory;
import com.fiskaly.kassensichv.sma.AndroidSMA;
import com.fiskaly.sdk.androidsdkdemo.RequestFactory;
import com.fiskaly.sdk.androidsdkdemo.models.KeyPair;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SignTransactionClickHandler implements View.OnClickListener {
    private final KeyPair keyPair;
    private final String tssId;
    private final String clientId;
    private final TextView textView;
    private final RequestFactory requestFactory = new RequestFactory();

    public SignTransactionClickHandler(KeyPair keyPair, String tssId, String clientId, TextView textView) {
        this.keyPair = keyPair;
        this.tssId = tssId;
        this.clientId = clientId;
        this.textView = textView;
    }

    @Override
    public void onClick(View view) {
        OkHttpClient client = ClientFactory.getClient(
                this.keyPair.getApiKey(),
                this.keyPair.getApiSecret(),
                new AndroidSMA()
        );

        Request createTx;

        try {
            createTx = requestFactory
                    .buildCreateTransactionRequest(tssId,
                            UUID.randomUUID().toString(), clientId);
        } catch (JsonProcessingException e) {
            Toast.makeText(view.getContext(),
                    "JsonProcessingError: " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        try (Response response = client.newCall(createTx).execute()) {
            ResponseBody body = response.body();

            if (body == null) {
                throw new IOException("Response contained an empty body");
            }

            String signedResponse = body.string();

            textView.setText(parseSignedResponse(signedResponse));
        } catch (IOException io) {
            Toast.makeText(view.getContext(),
                    "Error: " + io.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }
    private String parseSignedResponse(String signedResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> transactionMap = mapper
                .readValue(signedResponse,
                        new TypeReference<Map<String,Object>>() {});

        Map signatureMap = (Map) transactionMap.get("signature");

        if (signatureMap == null) {
            throw new IOException("Signature property was not available on signed response");
        }

        int start = (int) transactionMap.get("time_start");
        String end = (String) transactionMap.get("end");
        int signatureCounter = (int) signatureMap.get("counter");
        int transactionCounter = (int) transactionMap.get("number");
        String certificateSerial = (String) transactionMap.get("certificate_serial");

        return "Start: " + start + "\n" +
                "End: " + end + "\n" +
                "Certificate Serial: " + certificateSerial + "\n" +
                "Transaction Counter: " + transactionCounter + "\n" +
                "Signature counter: " + signatureCounter + "\n";
    }
}

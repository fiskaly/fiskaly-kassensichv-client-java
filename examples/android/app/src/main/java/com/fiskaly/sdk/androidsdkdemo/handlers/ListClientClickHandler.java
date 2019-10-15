package com.fiskaly.sdk.androidsdkdemo.handlers;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.kassensichv.client.ClientFactory;
import com.fiskaly.kassensichv.sma.AndroidSMA;
import com.fiskaly.sdk.androidsdkdemo.models.Client;
import com.fiskaly.sdk.androidsdkdemo.models.KeyPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListClientClickHandler implements View.OnClickListener {
    private KeyPair keyPair;
    private ListView clientListView;

    private final Request listClientsRequest = new Request.Builder()
            .url("https://kassensichv.io/api/v0/client")
            .get()
            .build();

    public ListClientClickHandler(KeyPair keyPair, ListView clientListView) {
        this.keyPair = keyPair;
        this.clientListView = clientListView;
    }

    @Override
    public void onClick(View view) {
        // Create a new OkHttpClient using the ClientFactory
        OkHttpClient client = ClientFactory.getClient(
                this.keyPair.getApiKey(),
                this.keyPair.getApiSecret(),
                new AndroidSMA() // create a new AndroidSMA and pass it to the factory
        );

        try (Response response = client
                .newCall(listClientsRequest)
                .execute()) {

            String jsonResponse = (response.body() != null)
                    ? Objects.requireNonNull(response.body()).string() : "";

            ArrayList<Client> clientList = parseResponseToClientList(jsonResponse);
            ArrayAdapter<Client> adapter =
                    new ArrayAdapter<>(view.getContext(),
                            android.R.layout.simple_list_item_1, clientList);

            clientListView.setAdapter(adapter);
        } catch (IOException ioException) {
            Toast.makeText(view.getContext(),
                    "Error: " + ioException.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private ArrayList<Client> parseResponseToClientList(String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, ArrayList<Map>> parsed =
                mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        ArrayList<Client> clientList = new ArrayList<>();

        ArrayList<Map> clients = parsed.get("data");

        if (clients == null) {
            throw new IOException("'data' property is not available on response body");
        }

        for (Map entry : clients) {
            Client client = new Client();

            client._id = (String)entry.get("_id");
            client._type = (String)entry.get("type");
            client.tssId = (String)entry.get("tss_id");

            clientList.add(client);
        }

        return clientList;
    }
}

package com.fiskaly.sdk.androidsdkdemo;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiskaly.sdk.androidsdkdemo.handlers.ListClientClickHandler;
import com.fiskaly.sdk.androidsdkdemo.handlers.SignTransactionClickHandler;
import com.fiskaly.sdk.androidsdkdemo.models.Client;
import com.fiskaly.sdk.androidsdkdemo.models.KeyPair;

public class MainActivity extends AppCompatActivity {
    ObjectMapper mapper;
    RequestFactory requestFactory;
    Client selectedClient;

    public MainActivity() {
        this.mapper = new ObjectMapper();
        this.requestFactory = new RequestFactory();
    }

    static {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public String getApiKey() {
        return ((EditText)findViewById(R.id.apiKeyField))
                .getText()
                .toString();
    }

    public String getApiSecret() {
        return ((EditText)findViewById(R.id.apiSecretField))
                .getText()
                .toString();
    }

    public String getTssId() {
        return selectedClient.tssId;
    }

    public String getClientId() {
        return selectedClient._id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("APP", "onCreate");

        final Button signTransactionButton = findViewById(R.id.signSampleTransaction);
        signTransactionButton.setEnabled(false); // disabled while client hasn't been selected

        Log.d("APP", "after signTransactionButton");

        final ListView clientListView = findViewById(R.id.clientList);
        clientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.this.selectedClient = (Client) adapterView.getItemAtPosition(i);
                signTransactionButton.setEnabled(true);

                signTransactionButton.setOnClickListener(new SignTransactionClickHandler(
                        new KeyPair(getApiKey(), getApiSecret()),
                        getTssId(), getClientId(),
                        (TextView) findViewById(R.id.transactionResponseTextField)
                ));
            }
        });

        final Button listClientButton = findViewById(R.id.listClientButton);
        listClientButton.setEnabled(false); // disabled while credentials haven't been provided

        final EditText apiKeyField = findViewById(R.id.apiKeyField);
        final EditText apiSecretField = findViewById(R.id.apiSecretField);
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String apiKey = apiKeyField.getText().toString();
                String apiSecret = apiSecretField.getText().toString();

                if (!apiKey.isEmpty() && !apiSecret.isEmpty()) {
                    listClientButton.setEnabled(true);
                    listClientButton.setOnClickListener(new ListClientClickHandler(
                            new KeyPair(getApiKey(), getApiSecret()),
                            clientListView
                    ));
                } else {
                    listClientButton.setEnabled(false);
                }
            }
        };

        apiKeyField.addTextChangedListener(watcher);
        apiSecretField.addTextChangedListener(watcher);
    }

}

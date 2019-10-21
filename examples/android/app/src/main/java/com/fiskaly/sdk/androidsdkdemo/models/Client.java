package com.fiskaly.sdk.androidsdkdemo.models;


import android.support.annotation.NonNull;

/**
 * Data class that's used for displaying a list of a client
 */
public class Client {
    public String _id;
    public String _type;
    public String tssId;

    public Client() {}

    @Override
    @NonNull
    public String toString() {
        return _id;
    }
}

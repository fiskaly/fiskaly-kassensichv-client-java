package com.fiskaly.kassensichv.sma;

import jnr.ffi.Pointer;

import java.io.IOException;

public class GeneralSma implements SmaInterface {
    private SmaLibrary library;

    public GeneralSma() throws IOException {
        this.library = SmaLoader.load();
    }

    @Override
    public String invoke(String payload) {
        Pointer pointer = library.Invoke(payload);
        String response = pointer.getString(0);

        // this is important, otherwise we'll leak memory
        library.Free(pointer);

        return response;
    }
}

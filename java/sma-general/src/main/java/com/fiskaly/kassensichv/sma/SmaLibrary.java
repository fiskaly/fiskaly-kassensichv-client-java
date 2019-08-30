package com.fiskaly.kassensichv.sma;

import jnr.ffi.Pointer;

public interface SmaLibrary {
    Pointer Invoke(String request);
    void Free(Pointer response);
}

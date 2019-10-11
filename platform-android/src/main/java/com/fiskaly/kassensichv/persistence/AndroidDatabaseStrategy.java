package com.fiskaly.kassensichv.persistence;

import java.io.IOException;
import java.util.List;

public class AndroidDatabaseStrategy implements PersistenceStrategy {
    public AndroidDatabaseStrategy() {}

    @Override
    public void persistRequest(Request request) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Request> loadRequests() throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }
}

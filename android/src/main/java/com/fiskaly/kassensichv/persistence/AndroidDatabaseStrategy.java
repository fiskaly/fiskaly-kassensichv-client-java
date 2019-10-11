package com.fiskaly.kassensichv.persistence;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.List;

public class AndroidDatabaseStrategy implements PersistenceStrategy {
    public AndroidDatabaseStrategy() {}

    @Override
    public void persistRequest(Request request) throws IOException {
       throw new NotImplementedException();
    }

    @Override
    public List<Request> loadRequests() throws IOException {
        throw new NotImplementedException();
    }
}

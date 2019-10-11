package com.fiskaly.kassensichv.persistence;

import java.io.IOException;
import java.util.List;

public interface PersistenceStrategy {
    void persistRequest(Request request) throws IOException;
    List<Request> loadRequests() throws IOException;
}

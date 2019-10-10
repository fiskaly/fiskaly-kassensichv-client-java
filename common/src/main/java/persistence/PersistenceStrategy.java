package persistence;

import java.io.IOException;

public interface PersistenceStrategy {
    void persistRequest(Request request) throws IOException;
    Request[] loadRequests() throws IOException;
}

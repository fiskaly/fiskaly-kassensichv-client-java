package com.fiskaly.kassensichv.client.persistence;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SqliteStrategy implements PersistenceStrategy {
    private Connection dbConnection;
    private final String REQUEST_TABLE = "request";

    public SqliteStrategy(File databaseDirectory) throws SQLException {
        if (!databaseDirectory.isDirectory()) {
            throw new IllegalArgumentException("databaseDirectory must be a directory");
        }

        this.initDatabase(databaseDirectory);
    }

    private void initDatabase(File databaseDirectory) throws SQLException {
        String connectionUrl =  "";
        this.dbConnection = DriverManager.getConnection(connectionUrl);
        this.createTables();
    }

    private void createTables() throws SQLException {
        String createTable = "create table " + this.REQUEST_TABLE;
        createTable +=
                "(" +
                    "creation_date datetime primary key,\n" +
                    "method varchar(8) not null,\n" +
                    "body varchar(5012) not null,\n" +
                    "url varchar(512) not null,\n" +
                ");"
        ;

        Statement createTableStatement = this.dbConnection.createStatement();
        createTableStatement.execute(createTable);
    }

    @Override
    public void persistRequest(Request request) throws IOException {
        String insertQuery = "insert into " + this.REQUEST_TABLE;
        insertQuery += "(id, method, body, url) values(?, ?, ?, ?)";

        try (PreparedStatement persistStatement = this.dbConnection.prepareStatement(insertQuery)) {
            persistStatement.execute();
        } catch (SQLException e) {
            throw new IOException("Failed to persist data into SQLite: " + e);
        }
    }

    @Override
    public Request[] loadRequests() throws IOException {
        return new Request[0];
    }
}

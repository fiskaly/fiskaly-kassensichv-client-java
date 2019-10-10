package persistence;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteStrategy implements PersistenceStrategy {
    private Connection dbConnection;
    private final String REQUEST_TABLE = "request";

    public SqliteStrategy(File databaseDirectory) throws SQLException {
        if (!databaseDirectory.isDirectory()) {
            throw new IllegalArgumentException("databaseDirectory must be a directory");
        }

        this.initDatabase(databaseDirectory);
    }

    private void initDatabase(File databasePath) throws SQLException {
        String connectionUrl =  "jdbc:sqlite:" + databasePath.getAbsolutePath() + File.separator + "db.sqlite";
        this.dbConnection = DriverManager.getConnection(connectionUrl);
        this.createTables();
    }

    private void createTables() throws SQLException {
        String createTable = "create table if not exists " + this.REQUEST_TABLE;
        createTable +=
                "(" +
                    "creation_date datetime primary key default current_timestamp,\n" +
                    "method varchar(8) not null,\n" +
                    "body varchar(5012) not null,\n" +
                    "url varchar(512) not null\n" +
                ");"
        ;

        Statement createTableStatement = this.dbConnection.createStatement();
        createTableStatement.execute(createTable);
    }

    @Override
    public void persistRequest(Request request) throws IOException {
        String insertQuery = "insert into " + this.REQUEST_TABLE;
        insertQuery += "(method, body, url) values(?, ?, ?)";

        try (PreparedStatement persistStatement = this.dbConnection.prepareStatement(insertQuery)) {
            persistStatement.setString(1, request.getMethod());
            persistStatement.setString(2, request.getBody());
            persistStatement.setString(3, request.getUrl());

            persistStatement.execute();
        } catch (SQLException e) {
            throw new IOException("Failed to persist data into SQLite: " + e);
        }
    }

    @Override
    public List<Request> loadRequests() throws IOException {
        String selectQuery = "select creation_date, method, body, url from request";
        ArrayList<Request> requests = new ArrayList<>();

        try (Statement requestStatement = this.dbConnection.createStatement();
             ResultSet result = requestStatement.executeQuery(selectQuery)) {
            while(result.next()) {
                requests.add(new Request(
                        result.getString(4),
                        result.getString(3),
                        result.getString(2)
                ));
            }
        } catch (SQLException e) {
            throw new IOException("Failed to load data from SQLite: " + e);
        }

        return requests;
    }
}

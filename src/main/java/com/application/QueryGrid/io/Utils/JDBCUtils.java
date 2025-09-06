package com.application.QueryGrid.io.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

public class JDBCUtils {

    public static String testJdbcConnection(String jdbcUrl, String username, String password, int timeoutSeconds) throws SQLException {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            if (!conn.isValid(timeoutSeconds)) {
                throw new SQLException("Connection was established but did not validate within " + timeoutSeconds + " seconds.");
            }
            // Return a connection confirmation ID (URL + hashCode)
            return String.format("ConnectionID-%d [%s]", conn.hashCode(), conn.getMetaData().getURL());
        }
    }


    public static String buildJdbcUrl(String dbType, String host, Integer port, String dbName) {
        if (dbType == null) throw new IllegalArgumentException("dbType must be provided");

        String upper = dbType.strip().toUpperCase(Locale.ROOT);
        boolean hasPort = port != null; // For cloud services where port is not required

        return switch (upper) {
            // PostgreSQL
            case "POSTGRESQL", "POSTGRES" ->
                    hasPort
                            ? String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName)
                            : String.format("jdbc:postgresql://%s/%s", host, dbName);

            // MySQL
            case "MYSQL", "MYSQL8" ->
                    hasPort
                            ? String.format("jdbc:mysql://%s:%d/%s", host, port, dbName)
                            : String.format("jdbc:mysql://%s/%s", host, dbName);

            // SQL Server
            case "MSSQL", "SQLSERVER" ->
                    hasPort
                            ? String.format("jdbc:sqlserver://%s:%d;databaseName=%s", host, port, dbName)
                            : String.format("jdbc:sqlserver://%s;databaseName=%s", host, dbName);

            // Oracle
            case "ORACLE", "ORACLE_THIN" ->
                    hasPort
                            ? String.format("jdbc:oracle:thin:@//%s:%d/%s", host, port, dbName)
                            : String.format("jdbc:oracle:thin:@//%s/%s", host, dbName);

            // MongoDB (Atlas or local)
            case "MONGODB" ->
                    hasPort
                            ? String.format("jdbc:mongodb://%s:%d/%s", host, port, dbName)
                            : String.format("jdbc:mongodb://%s/%s", host, dbName);

            // AWS Aurora (MySQL-compatible, no port needed usually)
            case "AURORA_MYSQL" ->
                    String.format("jdbc:mysql://%s/%s", host, dbName);

            // AWS Aurora (PostgreSQL-compatible, no port needed usually)
            case "AURORA_POSTGRES" ->
                    String.format("jdbc:postgresql://%s/%s", host, dbName);

            // Google Cloud SQL (PostgreSQL)
            case "GCP_POSTGRES" ->
                    String.format("jdbc:postgresql://%s/%s", host, dbName);

            // Google Cloud SQL (MySQL)
            case "GCP_MYSQL" ->
                    String.format("jdbc:mysql://%s/%s", host, dbName);

            default ->
                    throw new IllegalArgumentException("Unsupported dbType: " + dbType + ". Add URL pattern for your DB.");
        };
    }
}

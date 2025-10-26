package com.application.QueryGrid.Utils;

import java.sql.*;
import java.util.*;

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

    public static List<Map<String, Object>> executeQuery(String jdbcUrl, String username, String password, String sqlQuery) throws SQLException{
        List<Map<String, Object>> results = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery)
        ){
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for(int i=1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                results.add(row);
            }
        }
        return results;
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

    public static String preSQLSyntax(String dbType, String requiredType) {
        if (dbType == null || requiredType == null)
            throw new IllegalArgumentException("dbType and requiredType must not be null");

        String upperDb = dbType.strip().toUpperCase(Locale.ROOT);
        String upperReq = requiredType.strip().toUpperCase(Locale.ROOT);

        if (!upperReq.equals("SELECT_TABLE") && !upperReq.equals("SHOW_TABLES")) {
            throw new IllegalArgumentException("preSQLSyntax currently supports only 'SHOW TABLES'");
        }

        return switch (upperDb) {
            case "MYSQL", "MYSQL8", "AURORA_MYSQL", "GCP_MYSQL" ->
                    "SHOW TABLES;";

            case "POSTGRESQL", "POSTGRES", "AURORA_POSTGRES", "GCP_POSTGRES" ->
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';";

            case "MSSQL", "SQLSERVER" ->
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';";

            case "ORACLE", "ORACLE_THIN" ->
                    "SELECT table_name FROM user_tables;";

            case "MONGODB" ->
                    "-- MongoDB is not SQL-based. Use db.getCollectionNames();";

            default ->
                    throw new IllegalArgumentException("Unsupported DB type for 'SHOW TABLES': " + dbType);
        };
    }

}

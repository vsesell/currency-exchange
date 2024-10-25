package com.serge.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
private static final String url = "jdbc:sqlite:Y:\\Code\\Java\\JukovRoadmap\\currency-exchange\\src\\main\\resources\\sample.db";
static {

    try {
        Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(url);
    }
}

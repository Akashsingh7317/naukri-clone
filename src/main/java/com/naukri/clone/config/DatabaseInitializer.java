package com.naukri.clone.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Auto-creates PostgreSQL database if it doesn't exist.
 * Overrides the default DataSource bean to run DB creation first.
 */
@Configuration
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${DB_HOST:localhost}")   private String host;
    @Value("${DB_PORT:5432}")        private String port;
    @Value("${DB_NAME:naukri_hub}")  private String dbName;
    @Value("${DB_USERNAME:postgres}") private String username;
    @Value("${DB_PASSWORD:root}")    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        // 1. Auto-create database
        ensureDbExists();

        // 2. Return proper DataSource
        var props = new DataSourceProperties();
        props.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + dbName);
        props.setUsername(username);
        props.setPassword(password);
        props.setDriverClassName("org.postgresql.Driver");

        return props.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
    }

    private void ensureDbExists() {
        String adminUrl = "jdbc:postgresql://" + host + ":" + port + "/postgres";
        try {
            Class.forName("org.postgresql.Driver");
            try (Connection conn = DriverManager.getConnection(adminUrl, username, password);
                 Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'"
                );
                if (!rs.next()) {
                    stmt.executeUpdate("CREATE DATABASE \"" + dbName + "\"");
                    log.info("✅ Database '{}' created!", dbName);
                } else {
                    log.info("✅ Database '{}' already exists.", dbName);
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ Auto DB creation failed: {}", e.getMessage());
            log.warn("   Please run: psql -U postgres -c \"CREATE DATABASE {};\"", dbName);
        }
    }
}

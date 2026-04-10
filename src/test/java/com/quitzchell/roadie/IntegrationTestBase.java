package com.quitzchell.roadie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public abstract class IntegrationTestBase {
  private static final String DB_NAME = "roadie_" + UUID.randomUUID().toString().replace("-", "");

  @BeforeAll
  static void createDatabase() throws SQLException {
    try (Connection conn =
        DriverManager.getConnection(
            "jdbc:postgresql://localhost:5433/postgres", "postgres", "postgres")) {
      conn.createStatement().execute("CREATE DATABASE %s".formatted(DB_NAME));
    }
  }

  @AfterAll
  static void dropDatabase() throws SQLException {
    try (Connection conn =
        DriverManager.getConnection(
            "jdbc:postgresql://localhost:5433/postgres", "postgres", "postgres")) {
      conn.createStatement().execute("DROP DATABASE IF EXISTS %s WITH (FORCE)".formatted(DB_NAME));
    }
  }

  @DynamicPropertySource
  static void configureDatabase(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5433/" + DB_NAME);
  }
}

package com.quitzchell.roadie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
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
  private static final String ADMIN_URL = "jdbc:postgresql://localhost:5433/postgres";
  private static final String ADMIN_USER = "postgres";
  private static final String ADMIN_PASSWORD = "postgres";

  static {
    createDatabase();
    Runtime.getRuntime().addShutdownHook(new Thread(IntegrationTestBase::dropDatabase));
  }

  private static void createDatabase() {
    try (Connection conn = DriverManager.getConnection(ADMIN_URL, ADMIN_USER, ADMIN_PASSWORD)) {
      conn.createStatement().execute("CREATE DATABASE %s".formatted(DB_NAME));
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create test database " + DB_NAME, e);
    }
  }

  private static void dropDatabase() {
    try (Connection conn = DriverManager.getConnection(ADMIN_URL, ADMIN_USER, ADMIN_PASSWORD)) {
      conn.createStatement().execute("DROP DATABASE IF EXISTS %s WITH (FORCE)".formatted(DB_NAME));
    } catch (SQLException e) {
      throw new RuntimeException("Failed to drop test database " + DB_NAME + ": " + e.getMessage());
    }
  }

  @DynamicPropertySource
  static void configureDatabase(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5433/" + DB_NAME);
  }
}

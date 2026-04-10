package com.quitzchell.roadie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class RoadieApplicationTests extends IntegrationTestBase {

  @Test
  void contextLoads() {}
}

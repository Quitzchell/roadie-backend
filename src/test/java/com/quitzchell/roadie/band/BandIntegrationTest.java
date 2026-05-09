package com.quitzchell.roadie.band;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.quitzchell.roadie.IntegrationTestBase;
import com.quitzchell.roadie.band.dto.BandRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BandIntegrationTest extends IntegrationTestBase {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private BandRepository bandRepository;

  @BeforeEach
  void setUp() {
    bandRepository.deleteAll();
  }

  @AfterAll
  void tearDown() {
    bandRepository.deleteAll();
  }

  @Test
  void createAndGetBand() throws Exception {
    BandRequest request = new BandRequest("Library Card");

    String response =
        mockMvc
            .perform(
                post("/api/v1/bands")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("Library Card"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Integer id = JsonPath.read(response, "$.id");

    mockMvc
        .perform(get("/api/v1/bands/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Library Card"));
  }

  @Test
  void getBand_whenNotFound_returns404() throws Exception {
    mockMvc
        .perform(get("/api/v1/bands/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Band not found"));
  }

  @Test
  void createBand_withBlankName_returns400() throws Exception {
    String json = "{\"name\": \"\"}";

    mockMvc
        .perform(post("/api/v1/bands").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateBand() throws Exception {
    Band band = new Band();
    band.setName("The Lumes");
    Band saved = bandRepository.save(band);

    BandRequest updateRequest = new BandRequest("Library Card");

    mockMvc
        .perform(
            put("/api/v1/bands/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("Library Card"));
  }

  @Test
  void deleteBand() throws Exception {
    Band band = new Band();
    band.setName("To Delete");
    Band saved = bandRepository.save(band);

    mockMvc.perform(delete("/api/v1/bands/" + saved.getId())).andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/bands/" + saved.getId())).andExpect(status().isNotFound());
  }
}

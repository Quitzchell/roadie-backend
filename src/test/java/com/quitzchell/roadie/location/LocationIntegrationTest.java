package com.quitzchell.roadie.location;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.quitzchell.roadie.IntegrationTestBase;
import com.quitzchell.roadie.location.dto.LocationRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LocationIntegrationTest extends IntegrationTestBase {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private LocationRepository locationRepository;

  @BeforeEach
  void setUp() {
    locationRepository.deleteAll();
  }

  @AfterAll
  void tearDown() {
    locationRepository.deleteAll();
  }

  @Test
  void createAndGetLocation() throws Exception {
    LocationRequest request = new LocationRequest("Rotterdam", "South Holland", "Netherlands");

    String response =
        mockMvc
            .perform(
                post("/api/v1/locations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.city").value("Rotterdam"))
            .andExpect(jsonPath("$.region").value("South Holland"))
            .andExpect(jsonPath("$.country").value("Netherlands"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Integer id = JsonPath.read(response, "$.id");

    mockMvc
        .perform(get("/api/v1/locations/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.city").value("Rotterdam"))
        .andExpect(jsonPath("$.region").value("South Holland"))
        .andExpect(jsonPath("$.country").value("Netherlands"));
  }

  @Test
  void getLocation_whenNotFound_returns404() throws Exception {
    mockMvc
        .perform(get("/api/v1/locations/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Location not found"));
  }

  @Test
  void createLocation_withBlankCity_returns400() throws Exception {
    String json = "{\"city\": \"\",\"region\": \"South Holland\",\"country\": \"Netherlands\"}";

    mockMvc
        .perform(post("/api/v1/locations").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createLocation_withBlankRegion_returns400() throws Exception {
    String json = "{\"city\": \"Rotterdam\",\"region\": \"\",\"country\": \"Netherlands\"}";

    mockMvc
        .perform(post("/api/v1/locations").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createLocation_withBlankCountry_returns400() throws Exception {
    String json = "{\"city\": \"Rotterdam\",\"region\": \"South Holland\",\"country\": \"\"}";

    mockMvc
        .perform(post("/api/v1/locations").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateLocation() throws Exception {
    Location location = new Location();
    location.setCity("Rotterdam");
    location.setRegion("South Holland");
    location.setCountry("Netherlands");
    Location saved = locationRepository.save(location);

    LocationRequest updateRequest =
        new LocationRequest("Amsterdam", "North Holland", "Netherlands");

    mockMvc
        .perform(
            put("/api/v1/locations/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId()))
        .andExpect(jsonPath("$.city").value("Amsterdam"))
        .andExpect(jsonPath("$.region").value("North Holland"))
        .andExpect(jsonPath("$.country").value("Netherlands"));
  }

  @Test
  void deleteLocation() throws Exception {
    Location location = new Location();
    location.setCity("Rotterdam");
    location.setRegion("South Holland");
    location.setCountry("Netherlands");
    Location saved = locationRepository.save(location);

    mockMvc.perform(delete("/api/v1/locations/" + saved.getId())).andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/locations/" + saved.getId())).andExpect(status().isNotFound());
  }
}

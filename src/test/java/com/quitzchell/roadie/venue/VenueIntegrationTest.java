package com.quitzchell.roadie.venue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitzchell.roadie.IntegrationTestBase;
import com.quitzchell.roadie.location.Location;
import com.quitzchell.roadie.location.LocationRepository;
import com.quitzchell.roadie.venue.dto.VenueRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class VenueIntegrationTest extends IntegrationTestBase {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private VenueRepository venueRepository;

  @Autowired private LocationRepository locationRepository;

  private Location savedLocation;

  @BeforeEach
  void setUp() {
    venueRepository.deleteAll();
    locationRepository.deleteAll();

    Location location = new Location();
    location.setCity("Rotterdam");
    location.setRegion("South Holland");
    location.setCountry("Netherlands");
    savedLocation = locationRepository.save(location);
  }

  @Test
  void createAndGetVenue() throws Exception {
    VenueRequest request = new VenueRequest("Rotown", savedLocation.getId());

    mockMvc
        .perform(
            post("/api/v1/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Rotown"));
  }

  @Test
  void getVenue_whenNotFound_returns404() throws Exception {
    mockMvc
        .perform(get("/api/v1/venues/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Venue not found"));
  }

  @Test
  void createVenue_withInvalidData_returns400() throws Exception {
    String json = "{\"name\": \"\", \"locationId\": 1}";

    mockMvc
        .perform(post("/api/v1/venues").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateVenue() throws Exception {
    Venue venue = new Venue();
    venue.setName("Old Name");
    venue.setLocation(savedLocation);
    Venue saved = venueRepository.save(venue);

    VenueRequest updateRequest = new VenueRequest("New Name", savedLocation.getId());

    mockMvc
        .perform(
            put("/api/v1/venues/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Name"));
  }

  @Test
  void deleteVenue() throws Exception {
    Venue venue = new Venue();
    venue.setName("To Delete");
    venue.setLocation(savedLocation);
    Venue saved = venueRepository.save(venue);

    mockMvc.perform(delete("/api/v1/venues/" + saved.getId())).andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/venues/" + saved.getId())).andExpect(status().isNotFound());
  }
}

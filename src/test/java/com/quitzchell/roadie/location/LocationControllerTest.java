package com.quitzchell.roadie.location;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitzchell.roadie.location.dto.LocationRequest;
import com.quitzchell.roadie.location.dto.LocationResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LocationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LocationControllerTest {
  private final LocationResponse locationResponse =
      new LocationResponse(1, "Rotterdam", "Zuid Holland", "Netherlands");

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private LocationService locationService;

  @Test
  void getAllLocations_returns200() throws Exception {
    // arrange
    when(locationService.getAllLocations()).thenReturn(List.of(locationResponse));

    // act + assert
    mockMvc
        .perform(get("/api/v1/locations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].city").value("Rotterdam"))
        .andExpect(jsonPath("$[0].region").value("Zuid Holland"))
        .andExpect(jsonPath("$[0].country").value("Netherlands"));
  }

  @Test
  void getVenueById_returns200() throws Exception {
    // arrange
    when(locationService.getLocationById(1)).thenReturn(locationResponse);

    // act + assert
    mockMvc
        .perform(get("/api/v1/locations/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.city").value("Rotterdam"))
        .andExpect(jsonPath("$.region").value("Zuid Holland"))
        .andExpect(jsonPath("$.country").value("Netherlands"));
  }

  @Test
  void createLocation_returns201() throws Exception {
    // arrange
    LocationRequest request = new LocationRequest("Rotterdam", "Zuid Holland", "Netherlands");
    when(locationService.createLocation(any(LocationRequest.class))).thenReturn(locationResponse);

    // act + assert
    mockMvc
        .perform(
            post("/api/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.city").value("Rotterdam"))
        .andExpect(jsonPath("$.region").value("Zuid Holland"))
        .andExpect(jsonPath("$.country").value("Netherlands"));
  }

  @Test
  void createLocation_withBlankCity_returns400() throws Exception {
    // arrange
    LocationRequest request = new LocationRequest("", "Zuid Holland", "Netherlands");

    // act + assert
    mockMvc
        .perform(
            post("/api/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createLocation_withBlankRegion_returns400() throws Exception {
    // arrange
    LocationRequest request = new LocationRequest("Rotterdam", "", "Netherlands");

    // act + assert
    mockMvc
        .perform(
            post("/api/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createLocation_withBlankCountry_returns400() throws Exception {
    // arrange
    LocationRequest request = new LocationRequest("Rotterdam", "Zuid Holland", "");

    // act + assert
    mockMvc
        .perform(
            post("/api/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateLocation_returns200() throws Exception {
    // arrange
    LocationRequest request = new LocationRequest("Amsterdam", "Noord Holland", "Netherlands");
    LocationResponse updated = new LocationResponse(1, "Amsterdam", "Noord Holland", "Netherlands");
    when(locationService.updateLocation(eq(1), any(LocationRequest.class))).thenReturn(updated);

    // act + assert
    mockMvc
        .perform(
            put("/api/v1/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.city").value("Amsterdam"))
        .andExpect(jsonPath("$.region").value("Noord Holland"))
        .andExpect(jsonPath("$.country").value("Netherlands"));
  }

  @Test
  void deleteLocation_returns200() throws Exception {
    // act + assert
    mockMvc.perform(delete("/api/v1/locations/1")).andExpect(status().isNoContent());
  }
}

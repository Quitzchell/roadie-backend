package com.quitzchell.roadie.venue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import com.quitzchell.roadie.location.dto.LocationResponse;
import com.quitzchell.roadie.venue.dto.VenueRequest;
import com.quitzchell.roadie.venue.dto.VenueResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VenueController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VenueControllerTest {
  private final LocationResponse locationResponse =
      new LocationResponse(1, "Rotterdam", "South Holland", "Netherlands");
  private final VenueResponse venueResponse = new VenueResponse(1, "Rotown", locationResponse);

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private VenueService venueService;

  @Test
  void getAllVenues_returns200() throws Exception {
    when(venueService.getAllVenues()).thenReturn(List.of(venueResponse));

    mockMvc
        .perform(get("/api/v1/venues"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Rotown"))
        .andExpect(jsonPath("$[0].location.city").value("Rotterdam"));
  }

  @Test
  void getVenueById_returns200() throws Exception {
    when(venueService.getVenueById(1)).thenReturn(venueResponse);

    mockMvc
        .perform(get("/api/v1/venues/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Rotown"));
  }

  @Test
  void getVenueById_whenNotFound_returns404() throws Exception {
    when(venueService.getVenueById(99)).thenThrow(new ResourceNotFoundException("Venue not found"));

    mockMvc
        .perform(get("/api/v1/venues/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Venue not found"));
  }

  @Test
  void createVenue_returns201() throws Exception {
    VenueRequest request = new VenueRequest("Rotown", 1);
    when(venueService.createVenue(any(VenueRequest.class))).thenReturn(venueResponse);

    mockMvc
        .perform(
            post("/api/v1/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Rotown"));
  }

  @Test
  void createVenue_withBlankName_returns400() throws Exception {
    VenueRequest request = new VenueRequest("", 1);

    mockMvc
        .perform(
            post("/api/v1/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createVenue_withNullLocationId_returns400() throws Exception {
    String json = "{\"name\": \"Rotown\"}";

    mockMvc
        .perform(post("/api/v1/venues").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateVenue_returns200() throws Exception {
    VenueRequest request = new VenueRequest("New Name", 1);
    VenueResponse updated = new VenueResponse(1, "New Name", locationResponse);
    when(venueService.updateVenue(eq(1), any(VenueRequest.class))).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/venues/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Name"));
  }

  @Test
  void deleteVenue_returns204() throws Exception {
    mockMvc.perform(delete("/api/v1/venues/1")).andExpect(status().isNoContent());
  }
}

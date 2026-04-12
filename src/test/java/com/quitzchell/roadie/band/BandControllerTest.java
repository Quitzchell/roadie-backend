package com.quitzchell.roadie.band;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitzchell.roadie.band.dto.BandRequest;
import com.quitzchell.roadie.band.dto.BandResponse;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BandController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BandControllerTest {
  private final BandResponse bandResponse = new BandResponse(1, "Library Card");

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private BandService bandService;

  @Test
  void getAllBands_return200() throws Exception {
    // arrange
    when(bandService.getAllBands()).thenReturn(List.of(bandResponse));

    // act + assert
    mockMvc
        .perform(get("/api/v1/bands"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Library Card"));
  }

  @Test
  void getBandById_returns200() throws Exception {
    // arrange
    when(bandService.getBandById(1)).thenReturn(bandResponse);

    // act + assert
    mockMvc
        .perform(get("/api/v1/bands/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Library Card"));
  }

  @Test
  void getBandById_whenNotFound_returns404() throws Exception {
    // arrange
    when(bandService.getBandById(99)).thenThrow(new ResourceNotFoundException("Band not found"));

    // act + assert
    mockMvc
        .perform(get("/api/v1/bands/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Band not found"));
  }

  @Test
  void createVenue_returns201() throws Exception {
    // arrange
    BandRequest request = new BandRequest("Library Card");
    when(bandService.createBand(any(BandRequest.class))).thenReturn(bandResponse);

    // act + assert
    mockMvc
        .perform(
            post("/api/v1/bands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Library Card"));
  }

  @Test
  void updateBand_returns200() throws Exception {
    // arrange
    BandRequest request = new BandRequest("Library Card");
    BandResponse update = new BandResponse(1, "Library Card");
    when(bandService.updateBand(eq(1), any(BandRequest.class))).thenReturn(update);

    // act + assert
    mockMvc
        .perform(
            put("/api/v1/bands/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Library Card"));
  }

  @Test
  void deleteVenue_returns204() throws Exception {
    // act + assert
    mockMvc.perform(delete("/api/v1/bands/1")).andExpect(status().isNoContent());
  }
}

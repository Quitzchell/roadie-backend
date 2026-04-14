package com.quitzchell.roadie.production;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import com.quitzchell.roadie.location.dto.LocationResponse;
import com.quitzchell.roadie.production.dto.ProductionRequest;
import com.quitzchell.roadie.production.dto.ProductionResponse;
import com.quitzchell.roadie.venue.dto.VenueResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductionControllerTest {
  private final LocationResponse locationResponse =
      new LocationResponse(1, "Rotterdam", "Zuid Holland", "Netherlands");
  private final VenueResponse venueResponse = new VenueResponse(1, "Rotown", locationResponse);
  private final ProductionResponse productionResponse =
      new ProductionResponse(1, LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, null, null);
  @MockitoBean ProductionService productionService;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void getAllProductions_returns200() throws Exception {
    // arrange
    when(productionService.getAllProductions()).thenReturn(List.of(productionResponse));

    // act + assert
    mockMvc
        .perform(get("/api/v1/productions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].date").value(LocalDate.of(2020, 1, 1).toString()))
        .andExpect(jsonPath("$[0].status").value(ProductionStatus.PENDING.name()))
        .andExpect(jsonPath("$[0].venue").isEmpty())
        .andExpect(jsonPath("$[0].contacts").isEmpty());
  }

  @Test
  void getProductionById_returns200() throws Exception {
    // arrange
    when(productionService.getProductionById(1)).thenReturn(productionResponse);

    // act + assert
    mockMvc
        .perform(get("/api/v1/productions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.date").value(LocalDate.of(2020, 1, 1).toString()))
        .andExpect(jsonPath("$.status").value(ProductionStatus.PENDING.name()))
        .andExpect(jsonPath("$.venue").isEmpty())
        .andExpect(jsonPath("$.contacts").isEmpty());
  }

  @Test
  void getProductionById_whenNotFound_returns404() throws Exception {
    // arrange
    when(productionService.getProductionById(99))
        .thenThrow(new ResourceNotFoundException("Production not found"));

    // act + assert
    mockMvc
        .perform(get("/api/v1/productions/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Production not found"));
  }

  @Test
  void createProduction_returns201() throws Exception {
    // arrange
    ProductionRequest request =
        new ProductionRequest(LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, 1, null);
    ProductionResponse created =
        new ProductionResponse(
            1, LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, venueResponse, null);

    when(productionService.createProduction(any(ProductionRequest.class))).thenReturn(created);

    // act + assert
    mockMvc
        .perform(
            post("/api/v1/productions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.date").value(LocalDate.of(2020, 1, 1).toString()))
        .andExpect(jsonPath("$.status").value(ProductionStatus.PENDING.name()))
        .andExpect(jsonPath("$.venue.id").value(1))
        .andExpect(jsonPath("$.venue.name").value("Rotown"))
        .andExpect(jsonPath("$.venue.location.id").value(1))
        .andExpect(jsonPath("$.venue.location.city").value("Rotterdam"))
        .andExpect(jsonPath("$.venue.location.region").value("Zuid Holland"))
        .andExpect(jsonPath("$.venue.location.country").value("Netherlands"))
        .andExpect(jsonPath("$.contacts").isEmpty());
  }

  @Test
  void updateProduction_returns200() throws Exception {
    // arrange
    ProductionRequest request =
        new ProductionRequest(LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, 1, null);
    ProductionResponse update =
        new ProductionResponse(
            1, LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, venueResponse, null);
    when(productionService.updateProduction(eq(1), any(ProductionRequest.class)))
        .thenReturn(update);

    // act + assert
    mockMvc
        .perform(
            put("/api/v1/productions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.date").value(LocalDate.of(2020, 1, 1).toString()))
        .andExpect(jsonPath("$.status").value(ProductionStatus.PENDING.name()))
        .andExpect(jsonPath("$.venue.id").value(1))
        .andExpect(jsonPath("$.venue.name").value("Rotown"))
        .andExpect(jsonPath("$.venue.location.id").value(1))
        .andExpect(jsonPath("$.venue.location.city").value("Rotterdam"))
        .andExpect(jsonPath("$.venue.location.region").value("Zuid Holland"))
        .andExpect(jsonPath("$.venue.location.country").value("Netherlands"))
        .andExpect(jsonPath("$.contacts").isEmpty());
  }

  @Test
  void deleteProduction_returns204() throws Exception {
    // act + assert
    mockMvc.perform(delete("/api/v1/productions/1")).andExpect(status().isNoContent());
  }
}

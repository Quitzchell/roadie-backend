package com.quitzchell.roadie.venue;

import com.quitzchell.roadie.venue.dto.VenueRequest;
import com.quitzchell.roadie.venue.dto.VenueResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/venues")
public class VenueController {
  private final VenueService venueService;

  public VenueController(VenueService venueService) {
    this.venueService = venueService;
  }

  @GetMapping
  public ResponseEntity<List<VenueResponse>> getAllVenues() {
    return ResponseEntity.ok(venueService.getAllVenues());
  }

  @GetMapping("/{id}")
  public ResponseEntity<VenueResponse> getVenueById(@PathVariable Integer id) {
    return ResponseEntity.ok(venueService.getVenueById(id));
  }

  @PostMapping
  public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody VenueRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(venueService.createVenue(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<VenueResponse> updateVenue(
      @PathVariable Integer id, @Valid @RequestBody VenueRequest request) {
    return ResponseEntity.ok(venueService.updateVenue(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteVenue(@PathVariable Integer id) {
    venueService.deleteVenue(id);
    return ResponseEntity.noContent().build();
  }
}

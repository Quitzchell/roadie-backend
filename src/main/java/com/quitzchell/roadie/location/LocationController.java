package com.quitzchell.roadie.location;

import com.quitzchell.roadie.location.dto.LocationRequest;
import com.quitzchell.roadie.location.dto.LocationResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
  private final LocationService locationService;

  public LocationController(LocationService locationService) {
    this.locationService = locationService;
  }

  @GetMapping
  public ResponseEntity<List<LocationResponse>> getAllLocations() {
    return ResponseEntity.ok(locationService.getAllLocations());
  }

  @GetMapping("/{id}")
  public ResponseEntity<LocationResponse> getLocationById(@PathVariable Integer id) {
    return ResponseEntity.ok(locationService.getLocationById(id));
  }

  @PostMapping
  public ResponseEntity<LocationResponse> createLocation(
      @Valid @RequestBody LocationRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(locationService.createLocation(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<LocationResponse> updateLocation(
      @PathVariable Integer id, @Valid @RequestBody LocationRequest request) {
    return ResponseEntity.ok(locationService.updateLocation(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLocation(@PathVariable Integer id) {
    locationService.deleteLocation(id);
    return ResponseEntity.noContent().build();
  }
}

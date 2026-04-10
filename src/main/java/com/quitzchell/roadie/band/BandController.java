package com.quitzchell.roadie.band;

import com.quitzchell.roadie.band.dto.BandRequest;
import com.quitzchell.roadie.band.dto.BandResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bands")
public class BandController {
  private final BandService bandService;

  public BandController(BandService bandService) {
    this.bandService = bandService;
  }

  @GetMapping
  public ResponseEntity<List<BandResponse>> getAllBands() {
    return ResponseEntity.ok(bandService.getAllBands());
  }

  @GetMapping("/{id}")
  public ResponseEntity<BandResponse> getBandById(@PathVariable Integer id) {
    return ResponseEntity.ok(bandService.getBandById(id));
  }

  @PostMapping
  public ResponseEntity<BandResponse> createBand(@Valid @RequestBody BandRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bandService.createBand(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BandResponse> updateBand(
      @PathVariable Integer id, @Valid @RequestBody BandRequest request) {
    return ResponseEntity.ok(bandService.updateBand(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBand(@PathVariable Integer id) {
    bandService.deleteBand(id);
    return ResponseEntity.noContent().build();
  }
}

package com.quitzchell.roadie.production;

import com.quitzchell.roadie.production.dto.ProductionRequest;
import com.quitzchell.roadie.production.dto.ProductionResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/productions")
public class ProductionController {
  private final ProductionService productionService;

  public ProductionController(ProductionService productionService) {
    this.productionService = productionService;
  }

  @GetMapping
  public ResponseEntity<List<ProductionResponse>> getAllProductions() {
    return ResponseEntity.ok(productionService.getAllProductions());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductionResponse> getProductionById(@PathVariable Integer id) {
    return ResponseEntity.ok(productionService.getProductionById(id));
  }

  @PostMapping
  public ResponseEntity<ProductionResponse> createProduction(
      @Valid @RequestBody ProductionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(productionService.createProduction(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductionResponse> updateProduction(
      @PathVariable Integer id, @Valid @RequestBody ProductionRequest request) {
    return ResponseEntity.ok(productionService.updateProduction(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduction(@PathVariable Integer id) {
    productionService.deleteProduction(id);
    return ResponseEntity.noContent().build();
  }
}

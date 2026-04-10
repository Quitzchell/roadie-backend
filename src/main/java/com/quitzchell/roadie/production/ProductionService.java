package com.quitzchell.roadie.production;

import com.quitzchell.roadie.contact.Contact;
import com.quitzchell.roadie.contact.ContactRepository;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import com.quitzchell.roadie.production.dto.ProductionRequest;
import com.quitzchell.roadie.production.dto.ProductionResponse;
import com.quitzchell.roadie.production.mapper.ProductionMapper;
import com.quitzchell.roadie.venue.Venue;
import com.quitzchell.roadie.venue.VenueRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductionService {
  private final ProductionRepository productionRepository;
  private final ProductionMapper productionMapper;

  private final VenueRepository venueRepository;
  private final ContactRepository contactRepository;

  public ProductionService(
      ProductionRepository productionRepository,
      ProductionMapper productionMapper,
      VenueRepository venueRepository,
      ContactRepository contactRepository) {
    this.productionRepository = productionRepository;
    this.productionMapper = productionMapper;
    this.venueRepository = venueRepository;
    this.contactRepository = contactRepository;
  }

  public List<ProductionResponse> getAllProductions() {
    return productionRepository.findAll().stream().map(productionMapper::toResponse).toList();
  }

  public ProductionResponse getProductionById(Integer id) {
    Production production = findProductionById(id);
    return productionMapper.toResponse(production);
  }

  public ProductionResponse createProduction(ProductionRequest request) {
    Venue venue = findVenueById(request.venueId());
    Set<Contact> contacts =
        request.contactIds() == null
            ? Set.of()
            : request.contactIds().stream().map(this::findContactById).collect(Collectors.toSet());

    Production production = productionMapper.toEntity(request, venue, contacts);
    Production saved = productionRepository.save(production);
    return productionMapper.toResponse(saved);
  }

  public ProductionResponse updateProduction(Integer id, ProductionRequest request) {
    Production production = findProductionById(id);
    Venue venue = findVenueById(request.venueId());
    Set<Contact> contacts =
        request.contactIds() == null
            ? Set.of()
            : request.contactIds().stream().map(this::findContactById).collect(Collectors.toSet());
    productionMapper.updateEntity(production, request, venue, contacts);
    Production saved = productionRepository.save(production);
    return productionMapper.toResponse(saved);
  }

  public void deleteProduction(Integer id) {
    productionRepository.deleteById(id);
  }

  private Production findProductionById(Integer id) {
    return productionRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Production not found"));
  }

  private Venue findVenueById(Integer id) {
    return venueRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
  }

  private Contact findContactById(Integer id) {
    return contactRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
  }
}

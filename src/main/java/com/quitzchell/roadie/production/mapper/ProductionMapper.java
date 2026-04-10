package com.quitzchell.roadie.production.mapper;

import com.quitzchell.roadie.contact.Contact;
import com.quitzchell.roadie.contact.mapper.ContactMapper;
import com.quitzchell.roadie.production.Production;
import com.quitzchell.roadie.production.dto.ProductionRequest;
import com.quitzchell.roadie.production.dto.ProductionResponse;
import com.quitzchell.roadie.venue.Venue;
import com.quitzchell.roadie.venue.mapper.VenueMapper;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ProductionMapper {
  private final VenueMapper venueMapper;
  private final ContactMapper contactMapper;

  public ProductionMapper(VenueMapper venueMapper, ContactMapper contactMapper) {
    this.venueMapper = venueMapper;
    this.contactMapper = contactMapper;
  }

  public ProductionResponse toResponse(Production production) {
    return new ProductionResponse(
        production.getId(),
        production.getName(),
        production.getDate(),
        production.getStatus(),
        venueMapper.toResponse(production.getVenue()),
        production.getContacts().stream()
            .map(contactMapper::toResponse)
            .collect(Collectors.toSet()));
  }

  public Production toEntity(ProductionRequest request, Venue venue, Set<Contact> contacts) {
    Production production = new Production();
    production.setName(request.name());
    production.setDate(request.date());
    production.setStatus(request.status());
    production.setVenue(venue);
    production.setContacts(contacts);
    return production;
  }

  public void updateEntity(
      Production production, ProductionRequest request, Venue venue, Set<Contact> contacts) {
    production.setName(request.name());
    production.setDate(request.date());
    production.setStatus(request.status());
    production.setVenue(venue);
    production.setContacts(contacts);
  }
}

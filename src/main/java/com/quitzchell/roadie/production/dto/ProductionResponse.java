package com.quitzchell.roadie.production.dto;

import com.quitzchell.roadie.contact.dto.ContactResponse;
import com.quitzchell.roadie.production.ProductionStatus;
import com.quitzchell.roadie.venue.dto.VenueResponse;
import java.time.LocalDate;
import java.util.Set;

public record ProductionResponse(
    Integer id,
    String name,
    LocalDate date,
    ProductionStatus status,
    VenueResponse venue,
    Set<ContactResponse> contacts) {}

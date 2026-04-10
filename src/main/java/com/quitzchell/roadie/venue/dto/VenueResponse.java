package com.quitzchell.roadie.venue.dto;

import com.quitzchell.roadie.location.dto.LocationResponse;

public record VenueResponse(Integer id, String name, LocationResponse location) {}

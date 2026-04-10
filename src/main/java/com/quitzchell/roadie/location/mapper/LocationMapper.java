package com.quitzchell.roadie.location.mapper;

import com.quitzchell.roadie.location.Location;
import com.quitzchell.roadie.location.dto.LocationRequest;
import com.quitzchell.roadie.location.dto.LocationResponse;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {
  public LocationResponse toResponse(Location location) {
    return new LocationResponse(
        location.getId(), location.getCity(), location.getRegion(), location.getCountry());
  }

  public Location toEntity(LocationRequest request) {
    Location location = new Location();
    location.setCity(request.city());
    location.setRegion(request.region());
    location.setCountry(request.country());
    return location;
  }

  public void updateEntity(Location location, LocationRequest request) {
    location.setCity(request.city());
    location.setRegion(request.region());
    location.setCountry(request.country());
  }
}

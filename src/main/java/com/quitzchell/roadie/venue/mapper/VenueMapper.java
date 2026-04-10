package com.quitzchell.roadie.venue.mapper;

import com.quitzchell.roadie.location.Location;
import com.quitzchell.roadie.location.mapper.LocationMapper;
import com.quitzchell.roadie.venue.Venue;
import com.quitzchell.roadie.venue.dto.VenueRequest;
import com.quitzchell.roadie.venue.dto.VenueResponse;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {
  private final LocationMapper locationMapper;

  public VenueMapper(LocationMapper locationMapper) {
    this.locationMapper = locationMapper;
  }

  public VenueResponse toResponse(Venue venue) {
    return new VenueResponse(
        venue.getId(), venue.getName(), locationMapper.toResponse(venue.getLocation()));
  }

  public Venue toEntity(VenueRequest request, Location location) {
    Venue venue = new Venue();
    venue.setName(request.name());
    venue.setLocation(location);
    return venue;
  }

  public void updateEntity(Venue venue, VenueRequest request, Location location) {
    venue.setName(request.name());
    venue.setLocation(location);
  }
}

package com.quitzchell.roadie.venue;

import com.quitzchell.roadie.exception.ResourceNotFoundException;
import com.quitzchell.roadie.location.Location;
import com.quitzchell.roadie.location.LocationRepository;
import com.quitzchell.roadie.venue.dto.VenueRequest;
import com.quitzchell.roadie.venue.dto.VenueResponse;
import com.quitzchell.roadie.venue.mapper.VenueMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VenueService {
  private final VenueRepository venueRepository;
  private final VenueMapper venueMapper;

  private final LocationRepository locationRepository;

  public VenueService(
      VenueRepository venueRepository,
      VenueMapper venueMapper,
      LocationRepository locationRepository) {
    this.venueRepository = venueRepository;
    this.venueMapper = venueMapper;
    this.locationRepository = locationRepository;
  }

  public List<VenueResponse> getAllVenues() {
    return venueRepository.findAll().stream().map(venueMapper::toResponse).toList();
  }

  public VenueResponse getVenueById(Integer id) {
    Venue venue = findVenueById(id);
    return venueMapper.toResponse(venue);
  }

  public VenueResponse createVenue(VenueRequest request) {
    Location location = findLocationById(request.locationId());
    Venue venue = venueMapper.toEntity(request, location);
    Venue saved = venueRepository.save(venue);
    return venueMapper.toResponse(saved);
  }

  public VenueResponse updateVenue(Integer id, VenueRequest request) {
    Venue existing = findVenueById(id);
    Location location = findLocationById(request.locationId());
    venueMapper.updateEntity(existing, request, location);
    Venue saved = venueRepository.save(existing);
    return venueMapper.toResponse(saved);
  }

  public void deleteVenue(Integer id) {
    venueRepository.deleteById(id);
  }

  private Venue findVenueById(Integer id) {
    return venueRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
  }

  private Location findLocationById(Integer id) {
    return locationRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
  }
}

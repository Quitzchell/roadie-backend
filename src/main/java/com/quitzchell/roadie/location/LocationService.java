package com.quitzchell.roadie.location;

import com.quitzchell.roadie.exception.ResourceNotFoundException;
import com.quitzchell.roadie.location.dto.LocationRequest;
import com.quitzchell.roadie.location.dto.LocationResponse;
import com.quitzchell.roadie.location.mapper.LocationMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
  private final LocationRepository locationRepository;
  private final LocationMapper locationMapper;

  public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
    this.locationRepository = locationRepository;
    this.locationMapper = locationMapper;
  }

  public List<LocationResponse> getAllLocations() {
    return locationRepository.findAll().stream().map(locationMapper::toResponse).toList();
  }

  public LocationResponse getLocationById(Integer id) {
    Location location = findLocationById(id);
    return locationMapper.toResponse(location);
  }

  public LocationResponse createLocation(LocationRequest request) {
    Location location = locationMapper.toEntity(request);
    Location saved = locationRepository.save(location);
    return locationMapper.toResponse(saved);
  }

  public LocationResponse updateLocation(Integer id, LocationRequest request) {
    Location existing = findLocationById(id);
    locationMapper.updateEntity(existing, request);
    Location saved = locationRepository.save(existing);
    return locationMapper.toResponse(saved);
  }

  public void deleteLocation(Integer id) {
    locationRepository.deleteById(id);
  }

  private Location findLocationById(Integer id) {
    return locationRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
  }
}

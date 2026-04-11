package com.quitzchell.roadie.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quitzchell.roadie.exception.ResourceNotFoundException;
import com.quitzchell.roadie.location.dto.LocationRequest;
import com.quitzchell.roadie.location.dto.LocationResponse;
import com.quitzchell.roadie.location.mapper.LocationMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {
  @InjectMocks private LocationService locationService;
  @Mock private LocationRepository locationRepository;
  @Mock private LocationMapper locationMapper;

  @Test
  void getAllLocations_returnsListOfResponse() {
    // arrange
    Location location = new Location();
    LocationResponse response = new LocationResponse(1, "Rotterdam", "Zuid-Holland", "Netherlands");

    when(locationRepository.findAll()).thenReturn(List.of(location));
    when(locationMapper.toResponse(location)).thenReturn(response);

    // act
    List<LocationResponse> result = locationService.getAllLocations();

    // assert
    assertThat(result).containsExactly(response);
  }

  @Test
  void getLocationById_whenExists_returnResponse() {
    // arrange
    Location location = new Location();
    LocationResponse response = new LocationResponse(1, "Rotterdam", "Zuid-Holland", "Netherlands");

    when(locationRepository.findById(1)).thenReturn(Optional.of(location));
    when(locationMapper.toResponse(location)).thenReturn(response);

    // act
    LocationResponse result = locationService.getLocationById(1);

    // assert
    assertThat(result).isEqualTo(response);
  }

  @Test
  void getLocationById_whenNotFound_throwsException() {
    // arrange
    when(locationRepository.findById(99)).thenReturn(Optional.empty());

    // act + assert
    assertThatThrownBy(() -> locationService.getLocationById(99))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Location not found");
  }

  @Test
  void createLocation_saveAndReturnResponse() {
    // arrange
    LocationRequest request = new LocationRequest("Rotterdam", "Zuid-Holland", "Netherlands");
    Location location = new Location();
    Location saved = new Location();
    LocationResponse response = new LocationResponse(1, "Rotterdam", "Zuid-Holland", "Netherlands");

    when(locationMapper.toEntity(request)).thenReturn(location);
    when(locationRepository.save(location)).thenReturn(saved);
    when(locationMapper.toResponse(saved)).thenReturn(response);

    // act
    LocationResponse result = locationService.createLocation(request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(locationRepository).save(location);
  }

  @Test
  void updateLocation_saveAndReturnResponse() {
    // arrange
    Location existing = new Location();
    LocationRequest request = new LocationRequest("Amsterdam", "Noord-Holland", "Netherlands");
    Location saved = new Location();
    LocationResponse response =
        new LocationResponse(1, "Amsterdam", "Noord-Holland", "Netherlands");

    when(locationRepository.findById(1)).thenReturn(Optional.of(existing));
    when(locationRepository.save(existing)).thenReturn(saved);
    when(locationMapper.toResponse(saved)).thenReturn(response);

    // act
    LocationResponse result = locationService.updateLocation(1, request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(locationMapper).updateEntity(existing, request);
    verify(locationRepository).save(existing);
  }

  @Test
  void deleteLocation_callsRepository() {
    // arrange
    locationService.deleteLocation(1);

    // act + assert
    verify(locationRepository).deleteById(1);
  }
}

package com.quitzchell.roadie.venue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quitzchell.roadie.exception.ResourceNotFoundException;
import com.quitzchell.roadie.location.Location;
import com.quitzchell.roadie.location.LocationRepository;
import com.quitzchell.roadie.venue.dto.VenueRequest;
import com.quitzchell.roadie.venue.dto.VenueResponse;
import com.quitzchell.roadie.venue.mapper.VenueMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VenueServiceTest {
  @InjectMocks private VenueService venueService;
  @Mock private VenueRepository venueRepository;
  @Mock private LocationRepository locationRepository;
  @Mock private VenueMapper venueMapper;

  @Test
  void getAllVenues_returnsListOfResponse() {
    // arrange
    Venue venue = new Venue();
    VenueResponse response = new VenueResponse(1, "Rotown", null);

    when(venueRepository.findAll()).thenReturn(List.of(venue));
    when(venueMapper.toResponse(venue)).thenReturn(response);

    // act
    List<VenueResponse> result = venueService.getAllVenues();

    // assert
    assertThat(result).containsExactly(response);
  }

  @Test
  void getVenueById_whenExists_returnsResponse() {
    // arrange
    Venue venue = new Venue();
    VenueResponse response = new VenueResponse(1, "Rotown", null);

    when(venueRepository.findById(1)).thenReturn(Optional.of(venue));
    when(venueMapper.toResponse(venue)).thenReturn(response);

    // act
    VenueResponse result = venueService.getVenueById(1);

    // assert
    assertThat(result).isEqualTo(response);
  }

  @Test
  void getVenueById_whenNotFound_throwsException() {
    // arrange
    when(venueRepository.findById(99)).thenReturn(Optional.empty());

    // act + assert
    assertThatThrownBy(() -> venueService.getVenueById(99))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Venue not found");
  }

  @Test
  void createVenue_saveAndReturnsResponse() {
    // arrange
    Location location = new Location();
    VenueRequest request = new VenueRequest("Rotown", 1);
    Venue venue = new Venue();
    Venue saved = new Venue();
    VenueResponse response = new VenueResponse(1, "Rotown", null);

    when(locationRepository.findById(1)).thenReturn(Optional.of(location));
    when(venueMapper.toEntity(request, location)).thenReturn(venue);
    when(venueRepository.save(venue)).thenReturn(saved);
    when(venueMapper.toResponse(saved)).thenReturn(response);

    // act
    VenueResponse result = venueService.createVenue(request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(venueRepository).save(venue);
  }

  @Test
  void updateVenue_saveAndReturnsResponse() {
    // arrange
    Venue existing = new Venue();
    Location location = new Location();
    VenueRequest request = new VenueRequest("Rotown", 1);
    Venue saved = new Venue();
    VenueResponse response = new VenueResponse(1, "Rotown", null);

    when(venueRepository.findById(1)).thenReturn(Optional.of(existing));
    when(locationRepository.findById(1)).thenReturn(Optional.of(location));
    when(venueRepository.save(existing)).thenReturn(saved);
    when(venueMapper.toResponse(saved)).thenReturn(response);

    // act
    VenueResponse result = venueService.updateVenue(1, request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(venueMapper).updateEntity(existing, request, location);
    verify(venueRepository).save(existing);
  }

  @Test
  void updateVenue_whenLocationNotFound_throwsException() {
    // arrange
    Venue existing = new Venue();
    VenueRequest request = new VenueRequest("Name", 99);

    when(venueRepository.findById(1)).thenReturn(Optional.of(existing));
    when(locationRepository.findById(99)).thenReturn(Optional.empty());

    // act + assert
    assertThatThrownBy(() -> venueService.updateVenue(1, request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Location not found");
  }

  @Test
  void deleteVenue_callsRepository() {
    // act
    venueService.deleteVenue(1);

    // assert
    verify(venueRepository).deleteById(1);
  }
}

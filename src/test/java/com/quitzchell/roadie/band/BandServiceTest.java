package com.quitzchell.roadie.band;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quitzchell.roadie.band.dto.BandRequest;
import com.quitzchell.roadie.band.dto.BandResponse;
import com.quitzchell.roadie.band.mapper.BandMapper;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BandServiceTest {
  @InjectMocks private BandService bandService;
  @Mock private BandRepository bandRepository;
  @Mock private BandMapper bandMapper;

  @Test
  void getAllBands_returnsListOfResponse() {
    // arrange
    Band band = new Band();
    BandResponse response = new BandResponse(1, "Library Card");

    when(bandRepository.findAll()).thenReturn(List.of(band));
    when(bandMapper.toResponse(band)).thenReturn(response);

    // act
    List<BandResponse> result = bandService.getAllBands();

    // assert
    assertThat(result).containsExactly(response);
  }

  @Test
  void getBandById_whenExists_returnResponse() {
    // arrange
    Band band = new Band();
    band.setId(1);
    band.setName("Library Card");
    BandResponse response = new BandResponse(1, "Library Card");

    when(bandRepository.findById(1)).thenReturn(Optional.of(band));
    when(bandMapper.toResponse(band)).thenReturn(response);

    // act
    BandResponse result = bandService.getBandById(1);

    // assert
    assertThat(result).isEqualTo(response);
  }

  @Test
  void getBandById_whenNotFound_throwsException() {
    // arrange
    when(bandRepository.findById(99)).thenReturn(Optional.empty());

    // act + assert
    assertThatThrownBy(() -> bandService.getBandById(99))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Band not found");
  }

  @Test
  void createBand_saveAndReturnResponse() {
    // arrange
    BandRequest request = new BandRequest("Library Card");
    Band band = new Band();
    Band saved = new Band();
    saved.setId(1);
    BandResponse response = new BandResponse(1, "Library Card");

    when(bandMapper.toEntity(request)).thenReturn(band);
    when(bandRepository.save(band)).thenReturn(saved);
    when(bandMapper.toResponse(saved)).thenReturn(response);

    // act
    BandResponse result = bandService.createBand(request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(bandRepository).save(band);
  }

  @Test
  void updateBand_saveAndReturnResponse() {
    // arrange
    Band existing = new Band();
    existing.setId(1);
    existing.setName("The Lumes");
    BandRequest request = new BandRequest("Library Card");
    Band saved = new Band();
    saved.setId(1);
    BandResponse response = new BandResponse(1, "Library Card");

    when(bandRepository.findById(1)).thenReturn(Optional.of(existing));
    when(bandRepository.save(existing)).thenReturn(saved);
    when(bandMapper.toResponse(saved)).thenReturn(response);

    // act
    BandResponse result = bandService.updateBand(1, request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(bandMapper).updateEntity(existing, request);
    verify(bandRepository).save(existing);
  }

  @Test
  void deleteBand_callsRepository() {
    // arrange
    bandService.deleteBand(1);

    // act + assert
    verify(bandRepository).deleteById(1);
  }
}

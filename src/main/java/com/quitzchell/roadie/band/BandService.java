package com.quitzchell.roadie.band;

import com.quitzchell.roadie.band.dto.BandRequest;
import com.quitzchell.roadie.band.dto.BandResponse;
import com.quitzchell.roadie.band.mapper.BandMapper;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BandService {
  private final BandRepository bandRepository;
  private final BandMapper bandMapper;

  public BandService(BandRepository bandRepository, BandMapper bandMapper) {
    this.bandRepository = bandRepository;
    this.bandMapper = bandMapper;
  }

  public List<BandResponse> getAllBands() {
    return bandRepository.findAll().stream().map(bandMapper::toResponse).toList();
  }

  public BandResponse getBandById(Integer id) {
    Band band = findBandById(id);
    return bandMapper.toResponse(band);
  }

  public BandResponse createBand(BandRequest request) {
    Band band = bandMapper.toEntity(request);
    Band saved = bandRepository.save(band);
    return bandMapper.toResponse(saved);
  }

  public BandResponse updateBand(Integer id, BandRequest request) {
    Band existing = findBandById(id);
    bandMapper.updateEntity(existing, request);
    Band saved = bandRepository.save(existing);
    return bandMapper.toResponse(saved);
  }

  public void deleteBand(Integer id) {
    bandRepository.deleteById(id);
  }

  private Band findBandById(Integer id) {
    return bandRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Band not found"));
  }
}

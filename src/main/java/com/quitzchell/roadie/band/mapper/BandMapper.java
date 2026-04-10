package com.quitzchell.roadie.band.mapper;

import com.quitzchell.roadie.band.Band;
import com.quitzchell.roadie.band.dto.BandRequest;
import com.quitzchell.roadie.band.dto.BandResponse;
import org.springframework.stereotype.Component;

@Component
public class BandMapper {
  public BandResponse toResponse(Band band) {
    return new BandResponse(band.getId(), band.getName());
  }

  public Band toEntity(BandRequest request) {
    Band band = new Band();
    band.setName(request.name());
    return band;
  }

  public void updateEntity(Band band, BandRequest request) {
    band.setName(request.name());
  }
}

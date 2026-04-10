package com.quitzchell.roadie.venue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VenueRequest(@NotBlank String name, @NotNull Integer locationId) {}

package com.quitzchell.roadie.band.dto;

import jakarta.validation.constraints.NotBlank;

public record BandRequest(@NotBlank String name) {}

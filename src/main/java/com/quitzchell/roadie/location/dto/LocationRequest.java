package com.quitzchell.roadie.location.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(
    @NotBlank String city, @NotBlank String region, @NotBlank String country) {}

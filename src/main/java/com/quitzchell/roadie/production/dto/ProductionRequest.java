package com.quitzchell.roadie.production.dto;

import com.quitzchell.roadie.production.ProductionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public record ProductionRequest(
    @NotBlank String name,
    @NotNull LocalDate date,
    @NotNull ProductionStatus status,
    @NotNull Integer venueId,
    Set<Integer> contactIds) {}

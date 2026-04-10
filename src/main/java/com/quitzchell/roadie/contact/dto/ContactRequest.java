package com.quitzchell.roadie.contact.dto;

import com.quitzchell.roadie.contact.ContactRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record ContactRequest(
    @NotBlank String name, @NotBlank String email, @NotEmpty Set<ContactRole> roles) {}

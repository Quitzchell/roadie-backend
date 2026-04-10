package com.quitzchell.roadie.contact.dto;

import com.quitzchell.roadie.contact.ContactRole;
import java.util.Set;

public record ContactResponse(Integer id, String name, String email, Set<ContactRole> roles) {}

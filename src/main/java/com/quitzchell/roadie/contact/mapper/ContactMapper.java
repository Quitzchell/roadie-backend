package com.quitzchell.roadie.contact.mapper;

import com.quitzchell.roadie.contact.Contact;
import com.quitzchell.roadie.contact.dto.ContactRequest;
import com.quitzchell.roadie.contact.dto.ContactResponse;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {
  public ContactResponse toResponse(Contact contact) {
    return new ContactResponse(
        contact.getId(), contact.getName(), contact.getEmail(), contact.getRoles());
  }

  public Contact toEntity(ContactRequest request) {
    Contact contact = new Contact();
    contact.setName(request.name());
    contact.setEmail(request.email());
    contact.setRoles(request.roles());
    return contact;
  }

  public void updateEntity(Contact contact, ContactRequest request) {
    contact.setName(request.name());
    contact.setEmail(request.email());
    contact.setRoles(request.roles());
  }
}

package com.quitzchell.roadie.contact;

import com.quitzchell.roadie.contact.dto.ContactRequest;
import com.quitzchell.roadie.contact.dto.ContactResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {
  private final ContactService contactService;

  public ContactController(ContactService contactService) {
    this.contactService = contactService;
  }

  @GetMapping
  public ResponseEntity<List<ContactResponse>> getAllContacts() {
    return ResponseEntity.ok(contactService.getAllContacts());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ContactResponse> getContactById(@PathVariable Integer id) {
    return ResponseEntity.ok(contactService.getContactById(id));
  }

  @PostMapping
  public ResponseEntity<ContactResponse> createContact(@Valid @RequestBody ContactRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(contactService.createContact(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ContactResponse> updateContact(
      @PathVariable Integer id, @Valid @RequestBody ContactRequest request) {
    return ResponseEntity.ok(contactService.updateContact(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteContact(@PathVariable Integer id) {
    contactService.deleteContact(id);
    return ResponseEntity.noContent().build();
  }
}

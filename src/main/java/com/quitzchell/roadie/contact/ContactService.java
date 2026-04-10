package com.quitzchell.roadie.contact;

import com.quitzchell.roadie.contact.dto.ContactRequest;
import com.quitzchell.roadie.contact.dto.ContactResponse;
import com.quitzchell.roadie.contact.mapper.ContactMapper;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
  private final ContactRepository contactRepository;
  private final ContactMapper contactMapper;

  public ContactService(ContactRepository contactRepository, ContactMapper contactMapper) {
    this.contactRepository = contactRepository;
    this.contactMapper = contactMapper;
  }

  public List<ContactResponse> getAllContacts() {
    return contactRepository.findAll().stream().map(contactMapper::toResponse).toList();
  }

  public ContactResponse getContactById(Integer id) {
    Contact contact = findContactById(id);
    return contactMapper.toResponse(contact);
  }

  public ContactResponse createContact(ContactRequest request) {
    Contact contact = contactMapper.toEntity(request);
    Contact saved = contactRepository.save(contact);
    return contactMapper.toResponse(saved);
  }

  public ContactResponse updateContact(Integer id, ContactRequest request) {
    Contact existing = findContactById(id);
    contactMapper.updateEntity(existing, request);
    Contact saved = contactRepository.save(existing);
    return contactMapper.toResponse(saved);
  }

  public void deleteContact(Integer id) {
    contactRepository.deleteById(id);
  }

  private Contact findContactById(Integer id) {
    return contactRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
  }
}

package com.quitzchell.roadie.contact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quitzchell.roadie.contact.dto.ContactRequest;
import com.quitzchell.roadie.contact.dto.ContactResponse;
import com.quitzchell.roadie.contact.mapper.ContactMapper;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {
  @InjectMocks private ContactService contactService;
  @Mock private ContactRepository contactRepository;
  @Mock private ContactMapper contactMapper;

  @Test
  void getAllContacts_returnsListOfResponse() {
    // arrange
    Contact contact = new Contact();
    ContactResponse response =
        new ContactResponse(1, "John Doe", "john.doe@example.com", Set.of(ContactRole.PROMOTER));

    when(contactRepository.findAll()).thenReturn(List.of(contact));
    when(contactMapper.toResponse(contact)).thenReturn(response);

    // act
    List<ContactResponse> result = contactService.getAllContacts();

    // assert
    assertThat(result).containsExactly(response);
  }

  @Test
  void getContactById_whenExists_returnResponse() {
    // arrange
    Contact contact = new Contact();
    ContactResponse response =
        new ContactResponse(1, "John Doe", "john.doe@example.com", Set.of(ContactRole.PROMOTER));

    when(contactRepository.findById(1)).thenReturn(Optional.of(contact));
    when(contactMapper.toResponse(contact)).thenReturn(response);

    // act
    ContactResponse result = contactService.getContactById(1);

    // assert
    assertThat(result).isEqualTo(response);
  }

  @Test
  void getContactById_whenNotFound_throwsException() {
    // arrange
    when(contactRepository.findById(99)).thenReturn(Optional.empty());

    // act + assert
    assertThatThrownBy(() -> contactService.getContactById(99))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Contact not found");
  }

  @Test
  void createContact_saveAndReturnsResponse() {
    // arrange
    ContactRequest request =
        new ContactRequest("John Doe", "john.doe@example.com", Set.of(ContactRole.PROMOTER));
    Contact contact = new Contact();
    Contact saved = new Contact();
    ContactResponse response =
        new ContactResponse(1, "John Doe", "john.doe@example.com", Set.of(ContactRole.PROMOTER));

    when(contactMapper.toEntity(request)).thenReturn(contact);
    when(contactRepository.save(contact)).thenReturn(saved);
    when(contactMapper.toResponse(saved)).thenReturn(response);

    // act
    ContactResponse result = contactService.createContact(request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(contactRepository).save(contact);
  }

  @Test
  void updateContact_saveAndReturnsResponse() {
    // arrange
    Contact existing = new Contact();
    ContactRequest request =
        new ContactRequest("John Doe", "john.doe@example.com", Set.of(ContactRole.PROMOTER));
    Contact saved = new Contact();
    ContactResponse response =
        new ContactResponse(1, "John Doe", "john.doe@example.com", Set.of(ContactRole.PROMOTER));

    when(contactRepository.findById(1)).thenReturn(Optional.of(existing));
    when(contactRepository.save(existing)).thenReturn(saved);
    when(contactMapper.toResponse(saved)).thenReturn(response);

    // act
    ContactResponse result = contactService.updateContact(1, request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(contactMapper).updateEntity(existing, request);
    verify(contactRepository).save(existing);
  }

  @Test
  void deleteContact_callsRepository() {
    // act
    contactService.deleteContact(1);

    // assert
    verify(contactRepository).deleteById(1);
  }
}

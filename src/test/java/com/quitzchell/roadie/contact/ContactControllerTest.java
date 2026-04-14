package com.quitzchell.roadie.contact;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitzchell.roadie.contact.dto.ContactRequest;
import com.quitzchell.roadie.contact.dto.ContactResponse;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ContactControllerTest {
  private final ContactResponse contactResponse =
      new ContactResponse(
          1, "John Doe", "johndoe@example.com", Set.of(ContactRole.PRODUCTION_MANAGER));

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ContactService contactService;

  @Test
  void getAllContacts_returns200() throws Exception {
    // arrange
    when(contactService.getAllContacts()).thenReturn(List.of(contactResponse));

    // act + assert
    mockMvc
        .perform(get("/api/v1/contacts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("John Doe"))
        .andExpect(jsonPath("$[0].email").value("johndoe@example.com"))
        .andExpect(jsonPath("$[0].roles[0]").value(ContactRole.PRODUCTION_MANAGER.name()));
  }

  @Test
  void getContactById_returns200() throws Exception {
    // arrange
    when(contactService.getContactById(1)).thenReturn(contactResponse);

    // act + assert
    mockMvc
        .perform(get("/api/v1/contacts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("johndoe@example.com"))
        .andExpect(jsonPath("$.roles[0]").value(ContactRole.PRODUCTION_MANAGER.name()));
  }

  @Test
  void getContactById_whenNotFound_returns404() throws Exception {
    // arrange
    when(contactService.getContactById(99))
        .thenThrow(new ResourceNotFoundException("Contact not found"));

    // act + assert
    mockMvc
        .perform(get("/api/v1/contacts/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Contact not found"));
  }

  @Test
  void createContact_returns201() throws Exception {
    // arrange
    ContactRequest request =
        new ContactRequest(
            "John Doe", "johndoe@example.com", Set.of(ContactRole.PRODUCTION_MANAGER));
    when(contactService.createContact(any(ContactRequest.class))).thenReturn(contactResponse);

    // act + assert
    mockMvc
        .perform(
            post("/api/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("johndoe@example.com"))
        .andExpect(jsonPath("$.roles[0]").value(ContactRole.PRODUCTION_MANAGER.name()));
  }

  @Test
  void updateContact_returns200() throws Exception {
    // arrange
    ContactRequest request =
        new ContactRequest(
            "John Doe", "johndoe@example.com", Set.of(ContactRole.PRODUCTION_MANAGER));
    ContactResponse update =
        new ContactResponse(
            1, "John Doe", "johndoe@example.com", Set.of(ContactRole.PRODUCTION_MANAGER));
    when(contactService.updateContact(eq(1), any(ContactRequest.class))).thenReturn(update);

    // act + assert
    mockMvc
        .perform(
            put("/api/v1/contacts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("johndoe@example.com"))
        .andExpect(jsonPath("$.roles[0]").value(ContactRole.PRODUCTION_MANAGER.name()));
  }

  @Test
  void deleteContact_returns204() throws Exception {
    // act + assert
    mockMvc.perform(delete("/api/v1/contacts/1")).andExpect(status().isNoContent());
  }
}

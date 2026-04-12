package com.quitzchell.roadie.contact;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitzchell.roadie.contact.dto.ContactResponse;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
    ;
  }
}

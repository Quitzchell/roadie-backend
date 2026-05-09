package com.quitzchell.roadie.contact;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.quitzchell.roadie.IntegrationTestBase;
import com.quitzchell.roadie.contact.dto.ContactRequest;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContactIntegrationTest extends IntegrationTestBase {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ContactRepository contactRepository;

  @BeforeEach
  void setUp() {
    contactRepository.deleteAll();
  }

  @AfterAll
  void tearDown() {
    contactRepository.deleteAll();
  }

  @Test
  void createAndGetContact() throws Exception {
    ContactRequest request =
        new ContactRequest("John Doe", "johndoe@example.com", Set.of(ContactRole.PROMOTER));

    String response =
        mockMvc
            .perform(
                post("/api/v1/contacts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("johndoe@example.com"))
            .andExpect(jsonPath("$.roles[0]").value(ContactRole.PROMOTER.name()))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Integer id = JsonPath.read(response, "$.id");

    mockMvc
        .perform(get("/api/v1/contacts/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("johndoe@example.com"))
        .andExpect(jsonPath("$.roles[0]").value(ContactRole.PROMOTER.name()));
  }

  @Test
  void getContact_whenNotFound_returns404() throws Exception {
    mockMvc
        .perform(get("/api/v1/contacts/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Contact not found"));
  }

  @Test
  void createContact_withBlankName_returns400() throws Exception {
    String json = "{\"name\": \"\", \"email\": \"test@example.com\", \"roles\": [\"PROMOTER\"]}";

    mockMvc
        .perform(post("/api/v1/contacts").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createContact_withBlankEmail_returns400() throws Exception {
    String json = "{\"name\": \"John Doe\", \"email\": \"\", \"roles\": [\"PROMOTER\"]}";

    mockMvc
        .perform(post("/api/v1/contacts").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createContact_withEmptyRoles_returns400() throws Exception {
    String json = "{\"name\": \"John Doe\", \"email\": \"test@example.com\", \"roles\": []}";

    mockMvc
        .perform(post("/api/v1/contacts").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateContact() throws Exception {
    Contact contact = new Contact();
    contact.setName("John Doe");
    contact.setEmail("john@example.com");
    contact.setRoles(Set.of(ContactRole.PROMOTER));
    Contact saved = contactRepository.save(contact);

    ContactRequest updateRequest =
        new ContactRequest("Jane Doe", "jane@example.com", Set.of(ContactRole.BOOKING_AGENT));

    mockMvc
        .perform(
            put("/api/v1/contacts/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("Jane Doe"))
        .andExpect(jsonPath("$.email").value("jane@example.com"))
        .andExpect(jsonPath("$.roles[0]").value(ContactRole.BOOKING_AGENT.name()));
  }

  @Test
  void deleteContact() throws Exception {
    Contact contact = new Contact();
    contact.setName("Delete me");
    contact.setEmail("delete@example.com");
    contact.setRoles(Set.of(ContactRole.SOUND_ENGINEER));
    Contact saved = contactRepository.save(contact);

    mockMvc.perform(delete("/api/v1/contacts/" + saved.getId())).andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/contacts/" + saved.getId())).andExpect(status().isNotFound());
  }
}

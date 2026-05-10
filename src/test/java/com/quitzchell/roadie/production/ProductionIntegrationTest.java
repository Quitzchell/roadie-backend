package com.quitzchell.roadie.production;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.quitzchell.roadie.IntegrationTestBase;
import com.quitzchell.roadie.contact.Contact;
import com.quitzchell.roadie.contact.ContactRepository;
import com.quitzchell.roadie.contact.ContactRole;
import com.quitzchell.roadie.location.Location;
import com.quitzchell.roadie.location.LocationRepository;
import com.quitzchell.roadie.production.dto.ProductionRequest;
import com.quitzchell.roadie.venue.Venue;
import com.quitzchell.roadie.venue.VenueRepository;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductionIntegrationTest extends IntegrationTestBase {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ProductionRepository productionRepository;

  @Autowired private LocationRepository locationRepository;
  private Location savedLocation;

  @Autowired private VenueRepository venueRepository;
  private Venue savedVenue;

  @Autowired private ContactRepository contactRepository;
  private Contact savedContact;

  @BeforeEach
  void setUp() {
    productionRepository.deleteAll();
    contactRepository.deleteAll();
    venueRepository.deleteAll();
    locationRepository.deleteAll();

    Location location = new Location();
    location.setCity("Rotterdam");
    location.setRegion("South Holland");
    location.setCountry("Netherlands");
    savedLocation = locationRepository.save(location);

    Venue venue = new Venue();
    venue.setName("Rotown");
    venue.setLocation(savedLocation);
    savedVenue = venueRepository.save(venue);

    Contact contact = new Contact();
    contact.setName("John Doe");
    contact.setEmail("john@example.com");
    contact.setRoles(Set.of(ContactRole.STAGE_MANAGER));
    savedContact = contactRepository.save(contact);
  }

  @AfterAll
  void tearDown() {
    productionRepository.deleteAll();
    contactRepository.deleteAll();
    venueRepository.deleteAll();
    locationRepository.deleteAll();
  }

  @Test
  void createAndGetProduction() throws Exception {
    ProductionRequest request =
        new ProductionRequest(
            LocalDate.of(2020, 1, 1),
            ProductionStatus.PENDING,
            savedVenue.getId(),
            Set.of(savedContact.getId()));

    String response =
        mockMvc
            .perform(
                post("/api/v1/productions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.date").value(request.date().toString()))
            .andExpect(jsonPath("$.status").value(ProductionStatus.PENDING.name()))
            .andExpect(jsonPath("$.venue.id").value(savedVenue.getId()))
            .andExpect(jsonPath("$.venue.name").value("Rotown"))
            .andExpect(jsonPath("$.venue.location.city").value("Rotterdam"))
            .andExpect(jsonPath("$.contacts", hasSize(1)))
            .andExpect(jsonPath("$.contacts[*].id", contains(savedContact.getId())))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Integer id = JsonPath.read(response, "$.id");

    mockMvc
        .perform(get("/api/v1/productions/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.date").value(request.date().toString()))
        .andExpect(jsonPath("$.status").value(ProductionStatus.PENDING.name()))
        .andExpect(jsonPath("$.venue.id").value(savedVenue.getId()))
        .andExpect(jsonPath("$.venue.name").value("Rotown"))
        .andExpect(jsonPath("$.venue.location.city").value("Rotterdam"))
        .andExpect(jsonPath("$.contacts", hasSize(1)))
        .andExpect(jsonPath("$.contacts[*].id", contains(savedContact.getId())));
  }

  @Test
  void getProduction_whenNotFound_returns404() throws Exception {
    mockMvc
        .perform(get("/api/v1/productions/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Production not found"));
  }

  @Test
  void createProduction_withMissingDate_returns400() throws Exception {
    String json =
        "{\"date\": null, \"status\": \"PENDING\", \"venueId\": "
            + savedVenue.getId()
            + ", \"contactIds\": []}";

    mockMvc
        .perform(post("/api/v1/productions").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createProduction_withMissingStatus_returns400() throws Exception {
    String json =
        "{\"date\": \"2020-01-01\", \"status\": null, \"venueId\": "
            + savedVenue.getId()
            + ", \"contactIds\": []}";

    mockMvc
        .perform(post("/api/v1/productions").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createProduction_withMissingVenueId_returns400() throws Exception {
    String json =
        "{\"date\": \"2020-01-01\", \"status\": \"PENDING\", \"venueId\": null, \"contactIds\": []}";

    mockMvc
        .perform(post("/api/v1/productions").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createProduction_withUnknownVenue_returns404() throws Exception {
    ProductionRequest request =
        new ProductionRequest(
            LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, 999, Set.of(savedContact.getId()));

    mockMvc
        .perform(
            post("/api/v1/productions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Venue not found"));
  }

  @Test
  void createProduction_withUnknownContact_returns404() throws Exception {
    ProductionRequest request =
        new ProductionRequest(
            LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, savedVenue.getId(), Set.of(999));

    mockMvc
        .perform(
            post("/api/v1/productions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("Contact not found"));
  }

  @Test
  void updateProduction() throws Exception {
    Production production = new Production();
    production.setDate(LocalDate.of(2020, 1, 1));
    production.setStatus(ProductionStatus.PENDING);
    production.setVenue(savedVenue);
    production.setContacts(Set.of(savedContact));
    Production saved = productionRepository.save(production);

    Contact otherContact = new Contact();
    otherContact.setName("Jane Doe");
    otherContact.setEmail("jane@example.com");
    otherContact.setRoles(Set.of(ContactRole.PROMOTER));
    Contact savedOtherContact = contactRepository.save(otherContact);

    ProductionRequest updateRequest =
        new ProductionRequest(
            LocalDate.of(2021, 6, 15),
            ProductionStatus.COMPLETE,
            savedVenue.getId(),
            Set.of(savedOtherContact.getId()));

    mockMvc
        .perform(
            put("/api/v1/productions/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId()))
        .andExpect(jsonPath("$.date").value("2021-06-15"))
        .andExpect(jsonPath("$.status").value(ProductionStatus.COMPLETE.name()))
        .andExpect(jsonPath("$.contacts", hasSize(1)))
        .andExpect(jsonPath("$.contacts[*].id", contains(savedOtherContact.getId())));
  }

  @Test
  void deleteProduction() throws Exception {
    Production production = new Production();
    production.setDate(LocalDate.of(2020, 1, 1));
    production.setStatus(ProductionStatus.PENDING);
    production.setVenue(savedVenue);
    production.setContacts(Set.of(savedContact));
    Production saved = productionRepository.save(production);

    mockMvc
        .perform(delete("/api/v1/productions/" + saved.getId()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/productions/" + saved.getId())).andExpect(status().isNotFound());
  }
}
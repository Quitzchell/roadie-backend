package com.quitzchell.roadie.production;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quitzchell.roadie.contact.Contact;
import com.quitzchell.roadie.contact.ContactRepository;
import com.quitzchell.roadie.exception.ResourceNotFoundException;
import com.quitzchell.roadie.production.dto.ProductionRequest;
import com.quitzchell.roadie.production.dto.ProductionResponse;
import com.quitzchell.roadie.production.mapper.ProductionMapper;
import com.quitzchell.roadie.venue.Venue;
import com.quitzchell.roadie.venue.VenueRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductionServiceTest {
  @InjectMocks private ProductionService productionService;
  @Mock private ProductionRepository productionRepository;
  @Mock private ProductionMapper productionMapper;
  @Mock private VenueRepository venueRepository;
  @Mock private ContactRepository contactRepository;

  @Test
  void getAllProductions_returnsListOfResponse() {
    // arrange
    Production production = new Production();
    ProductionResponse response =
        new ProductionResponse(1, LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, null, null);

    when(productionRepository.findAll()).thenReturn(List.of(production));
    when(productionMapper.toResponse(production)).thenReturn(response);

    // act
    List<ProductionResponse> result = productionService.getAllProductions();

    // assert
    assertThat(result).containsExactly(response);
  }

  @Test
  void getProductionById_whenExists_returnResponse() {
    // arrange
    Production production = new Production();
    ProductionResponse response =
        new ProductionResponse(1, LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, null, null);

    when(productionRepository.findById(1)).thenReturn(Optional.of(production));
    when(productionMapper.toResponse(production)).thenReturn(response);

    // act
    ProductionResponse result = productionService.getProductionById(1);

    // assert
    assertThat(result).isEqualTo(response);
  }

  @Test
  void getProductionById_whenNotFound_throwsException() {
    // arrange
    when(productionRepository.findById(99)).thenReturn(Optional.empty());

    // act + assert
    assertThatThrownBy(() -> productionService.getProductionById(99))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Production not found");
  }

  @Test
  void createProduction_saveAndReturnsResponse() {
    // arrange
    Venue venue = new Venue();
    Contact contact = new Contact();

    ProductionRequest request =
        new ProductionRequest(LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, 1, Set.of(1));
    Production production = new Production();
    Production saved = new Production();
    ProductionResponse response =
        new ProductionResponse(1, LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, null, null);

    when(venueRepository.findById(1)).thenReturn(Optional.of(venue));
    when(contactRepository.findById(1)).thenReturn(Optional.of(contact));
    when(productionMapper.toEntity(request, venue, Set.of(contact))).thenReturn(production);
    when(productionRepository.save(production)).thenReturn(saved);
    when(productionMapper.toResponse(saved)).thenReturn(response);

    // act
    ProductionResponse result = productionService.createProduction(request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(productionRepository).save(production);
  }

  @Test
  void updateProduction_saveAndReturnsResponse() {
    // arrange
    Venue venue = new Venue();
    Contact contact = new Contact();

    Production existing = new Production();
    ProductionRequest request =
        new ProductionRequest(LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, 1, Set.of(1));
    Production saved = new Production();
    ProductionResponse response =
        new ProductionResponse(1, LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, null, null);

    when(venueRepository.findById(1)).thenReturn(Optional.of(venue));
    when(contactRepository.findById(1)).thenReturn(Optional.of(contact));
    when(productionRepository.findById(1)).thenReturn(Optional.of(existing));
    when(productionRepository.save(existing)).thenReturn(saved);
    when(productionMapper.toResponse(saved)).thenReturn(response);

    // act
    ProductionResponse result = productionService.updateProduction(1, request);

    // assert
    assertThat(result).isEqualTo(response);
    verify(productionMapper).updateEntity(existing, request, venue, Set.of(contact));
    verify(productionRepository).save(existing);
  }

  @Test
  void updateProduction_whenVenueNotFound_throwsException() {
    // arrange
    Production existing = new Production();
    ProductionRequest request =
        new ProductionRequest(LocalDate.of(2020, 1, 1), ProductionStatus.PENDING, 99, Set.of(1));

    when(productionRepository.findById(1)).thenReturn(Optional.of(existing));
    when(venueRepository.findById(99)).thenReturn(Optional.empty());

    // act + assert
    assertThatThrownBy(() -> productionService.updateProduction(1, request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Venue not found");
  }

  @Test
  void deleteProduction_callsRepository() {
    // act
    productionService.deleteProduction(1);

    // assert
    verify(productionRepository).deleteById(1);
  }
}

package com.quitzchell.roadie.production;

import com.quitzchell.roadie.contact.Contact;
import com.quitzchell.roadie.venue.Venue;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Production {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private LocalDate date;

  @Enumerated(EnumType.STRING)
  private ProductionStatus status;

  @ManyToOne
  @JoinColumn(name = "venue_id")
  private Venue venue;

  @ManyToMany
  @JoinTable(
      name = "production_contacts",
      joinColumns = @JoinColumn(name = "production_id"),
      inverseJoinColumns = @JoinColumn(name = "contact_id"))
  private Set<Contact> contacts;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Production production)) return false;
    return id != null && Objects.equals(id, production.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

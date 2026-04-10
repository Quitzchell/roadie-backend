package com.quitzchell.roadie.contact;

import jakarta.persistence.*;
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
public class Contact {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private String email;

  @ElementCollection
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "contact_roles", joinColumns = @JoinColumn(name = "contact_id"))
  @Column(name = "role")
  private Set<ContactRole> roles;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Contact contact)) return false;
    return id != null && Objects.equals(id, contact.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

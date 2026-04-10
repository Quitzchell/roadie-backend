package com.quitzchell.roadie.venue;

import com.quitzchell.roadie.location.Location;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "location_id"}))
@Entity
public class Venue {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Venue venue)) return false;
    return id != null && Objects.equals(id, venue.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

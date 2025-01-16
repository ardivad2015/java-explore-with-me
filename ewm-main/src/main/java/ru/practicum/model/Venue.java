package ru.practicum.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "venues")
@DynamicUpdate
@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Long id;
    @Embedded
    private Location location;
    @Column(name = "name")
    private String name;
}
package ru.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Embeddable
public class Location {

    private Float lat;
    private Float lon;
}

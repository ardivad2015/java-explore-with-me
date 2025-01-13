package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;
    @NotBlank
    @Email
    @Size(max = 254, min = 6)
    private String email;
    @NotBlank
    @Size(max = 250, min = 2)
    private String name;
}


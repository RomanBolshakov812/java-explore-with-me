package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
    @NotBlank
    @Email
    @Length(min = 6, max = 254)
    private String email;
}
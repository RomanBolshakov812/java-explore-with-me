package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class NewUserRequest {
    @NotNull
    @Length(min = 2, max = 250)
    private String name;
    @NotNull
    @Length(min = 6, max = 254)
    private String email;
}
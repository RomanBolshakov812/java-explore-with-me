package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
//@AllArgsConstructor///////////////////////////////////////////////////////////////
public class UserDto extends NewUserRequest {

    private Long id;
    public UserDto(Long id, String name, String email) {
        super(name, email);
        this.id = id;
    }
}

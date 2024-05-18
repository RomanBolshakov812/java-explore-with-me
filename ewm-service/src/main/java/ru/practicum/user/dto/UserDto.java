package ru.practicum.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto extends NewUserRequest {

    private Long id;

    public UserDto(Long id, String name, String email) {
        super(name, email);
        this.id = id;
    }
}

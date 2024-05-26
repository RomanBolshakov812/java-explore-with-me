package ru.practicum.user;

import java.util.List;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}

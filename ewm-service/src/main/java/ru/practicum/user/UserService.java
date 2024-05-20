package ru.practicum.user;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

@Transactional
public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    @Transactional(readOnly = true)
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}

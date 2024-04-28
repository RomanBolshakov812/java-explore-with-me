package ru.practicum.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.util.PageMaker;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id=" + userId + " was not found"));
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        List<User> users;
        if(ids == null || ids.isEmpty()) {
            users = userRepository.findAll(page).toList();
        } else {
            users = userRepository.findByIdIn(ids, page).toList();
        }
        return UserMapper.toUserDtoList(users);
    }
}

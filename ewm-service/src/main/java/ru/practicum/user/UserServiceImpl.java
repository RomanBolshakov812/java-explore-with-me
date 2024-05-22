package ru.practicum.user;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exception.IncorrectRequestParametersException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.util.PageMaker;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        User currentUser = UserMapper.toUser(newUserRequest);
        User user;
        try {
            user = userRepository.save(currentUser);
        } catch (RuntimeException e) {
            throw new IncorrectRequestParametersException("This email is taken!");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id=" + userId + " was not found"));
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageMaker.toPage(from, size);
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(page).toList();
        } else {
            users = userRepository.findByIdIn(ids, page).toList();
        }
        return UserMapper.toUserDtoList(users);
    }
}

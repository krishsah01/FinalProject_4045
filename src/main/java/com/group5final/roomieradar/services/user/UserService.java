package com.group5final.roomieradar.services.user;

import com.group5final.roomieradar.dtos.response.UserDto;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.exceptions.InvalidCredentialsException;
import com.group5final.roomieradar.exceptions.UserEmailExistsException;
import com.group5final.roomieradar.exceptions.UserNotFoundException;
import com.group5final.roomieradar.exceptions.WeakPasswordException;
import com.group5final.roomieradar.mappers.UserMapper;
import com.group5final.roomieradar.repositories.UserRepository;
import com.group5final.roomieradar.services.user.implement.UserServiceInterface;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     *Registers a new user after validating inputs.
     *
     * @param username User name
     * @param email User new email
     * @param password User new password
     * @return userDto
     * @exception IllegalArgumentException bad data format
     * @exception UserNotFoundException User id not found
     * @exception UserEmailExistsException User email exists
     * @exception WeakPasswordException User's password invalid
     */
    @Transactional
    @Override
    public UserDto register(String username, String email, String password) {
        if (username.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (email.isBlank() || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("A valid email is required");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserEmailExistsException("This email already exists");
        }
        validatePassword(password);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        return userMapper.toDto(userRepository.save(user));
    }

    /**
     *Validates password strength against local policy.
     *
     * @param email User existing email
     * @param password User existing password
     * @return userDto
     * @exception UserNotFoundException if user email is not found
     * @exception InvalidCredentialsException if user email is not correct
     */
    @Override
    public UserDto Login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password"));

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return userMapper.toDto(user);
    }

    /**
     * Changes a user's password.
     *
     * @param userId User Id
     * @param oldPassword User old password
     * @param newPassword User new password
     * @return UserDto
     * @exception UserNotFoundException if User id not found
     * @exception InvalidCredentialsException if old password is the same as new and failed validation
     */
    @Transactional
    @Override
    public UserDto ChangePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found by id: " + userId));

        if (!user.getPassword().equals(oldPassword)) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        if (user.getPassword().equals(newPassword)) {
            throw new InvalidCredentialsException("New password must be different from current password");
        }
        validatePassword(newPassword);

        user.setPassword(newPassword);

        return userMapper.toDto(userRepository.save(user));
    }

    /**
     *Validates password strength against local policy.
     *
     * @param password raw password to validate
     * @throws WeakPasswordException if any rule is violated
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new WeakPasswordException("Password must be at least 8 characters long");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new WeakPasswordException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new WeakPasswordException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new WeakPasswordException("Password must contain at least one digit");
        }
    }
}


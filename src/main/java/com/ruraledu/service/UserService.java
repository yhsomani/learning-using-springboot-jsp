package com.ruraledu.service;

import com.ruraledu.entity.User;
import com.ruraledu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new com.ruraledu.exception.UserAlreadyExistsException(String.format("The username '%s' is already in use. Please select a different username.", user.getUsername()));
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new com.ruraledu.exception.UserAlreadyExistsException(String.format("The email address '%s' is already associated with an account.", user.getEmail()));
        }
        
        // Prevent elevation of privilege
        if (user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Security Restriction: Administrative accounts cannot be created via public registration. Please contact the system administrator for assistance.");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        
        notificationService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName());
        
        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getChildren(Long parentId) {
        return userRepository.findByParentId(parentId);
    }
    public User updateUser(@org.springframework.lang.NonNull User user) {
        return userRepository.save(user);
    }
    public List<User> findAll() {
        return userRepository.findAllActive();
    }
}

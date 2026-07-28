package com.monexus.finance.user.service;

import com.monexus.finance.user.dto.request.RegisterRequest;
import com.monexus.finance.user.dto.response.UserResponse;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.event.UserRegisteredEvent;
import com.monexus.finance.user.exception.EmailAlreadyExistsException;
import com.monexus.finance.user.mapper.UserMapper;
import com.monexus.finance.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(savedUser));

        return userMapper.toResponse(savedUser);
    }
}

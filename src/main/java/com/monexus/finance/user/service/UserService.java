package com.monexus.finance.user.service;

import com.monexus.finance.shared.storage.ImageStorageService;
import com.monexus.finance.user.dto.request.RegisterRequest;
import com.monexus.finance.user.dto.request.UpdateProfileRequest;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageStorageService imageStorageService;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher, ImageStorageService imageStorageService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.imageStorageService = imageStorageService;
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

    @Transactional
    public UserResponse updateProfile(User authenticatedUser, UpdateProfileRequest request) {
        authenticatedUser.setFirstName(request.firstName());
        authenticatedUser.setLastName(request.lastName());

        User updatedUser = userRepository.save(authenticatedUser);

        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public UserResponse updateProfileImage(User authenticatedUser, MultipartFile file) {
        String imageUrl = imageStorageService.uploadImage(file);

        authenticatedUser.setProfileImage(imageUrl);
        User updatedUser = userRepository.save(authenticatedUser);

        return userMapper.toResponse(updatedUser);
    }

    public UserResponse getUserResponse(User user) {
        return userMapper.toResponse(user);
    }
}

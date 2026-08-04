package com.monexus.finance.user.service;

import com.monexus.finance.shared.storage.ImageStorageService;
import com.monexus.finance.user.dto.request.RegisterRequest;
import com.monexus.finance.user.dto.request.UpdateProfileRequest;
import com.monexus.finance.user.dto.response.UserResponse;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.event.UserDeletedEvent;
import com.monexus.finance.user.event.UserRegisteredEvent;
import com.monexus.finance.user.exception.EmailAlreadyExistsException;
import com.monexus.finance.user.mapper.UserMapper;
import com.monexus.finance.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ImageStorageService imageStorageService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper, passwordEncoder, eventPublisher, imageStorageService);
    }

    @Test
    void shouldRegisterUserWithEncodedPasswordAndPublishEvent() {
        RegisterRequest request = new RegisterRequest("Breno", "Teste", "breno@example.com", "12345678");
        User userToSave = User.builder().email("breno@example.com").build();
        User savedUser = User.builder().id(1L).email("breno@example.com").password("encoded-hash").build();
        UserResponse expectedResponse = new UserResponse(1L, "Breno", "Teste", "breno@example.com", null, false, null);

        when(userRepository.existsByEmail("breno@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userToSave);
        when(passwordEncoder.encode("12345678")).thenReturn("encoded-hash");
        when(userRepository.save(userToSave)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse response = userService.register(request);

        assertThat(response).isEqualTo(expectedResponse);
        assertThat(userToSave.getPassword()).isEqualTo("encoded-hash");
        verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void shouldThrowWhenRegisteringDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Breno", "Teste", "breno@example.com", "12345678");

        when(userRepository.existsByEmail("breno@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldUpdateProfileFields() {
        User user = User.builder().id(1L).firstName("Breno").lastName("Antigo").build();
        UpdateProfileRequest request = new UpdateProfileRequest("Breno", "Novo");

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(
                new UserResponse(1L, "Breno", "Novo", "breno@example.com", null, true, null));

        userService.updateProfile(user, request);

        assertThat(user.getFirstName()).isEqualTo("Breno");
        assertThat(user.getLastName()).isEqualTo("Novo");
    }

    @Test
    void shouldUpdateProfileImageUsingStorageService() {
        User user = User.builder().id(1L).build();
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "content".getBytes());

        when(imageStorageService.uploadImage(file)).thenReturn("https://cdn.example.com/photo.jpg");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(
                new UserResponse(1L, "Breno", "Teste", "breno@example.com", "https://cdn.example.com/photo.jpg", true, null));

        userService.updateProfileImage(user, file);

        assertThat(user.getProfileImage()).isEqualTo("https://cdn.example.com/photo.jpg");
    }

    @Test
    void shouldDeleteAccountAndPublishEventBeforeDeletingWhenPasswordMatches() {
        User user = User.builder().id(1L).password("hashed-password").build();

        when(passwordEncoder.matches("12345678", "hashed-password")).thenReturn(true);

        userService.deleteAccount(user, "12345678");

        InOrder inOrder = inOrder(eventPublisher, userRepository);
        inOrder.verify(eventPublisher).publishEvent(any(UserDeletedEvent.class));
        inOrder.verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowWhenDeleteAccountPasswordDoesNotMatch() {
        User user = User.builder().id(1L).password("hashed-password").build();

        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteAccount(user, "wrong-password"))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).delete(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}

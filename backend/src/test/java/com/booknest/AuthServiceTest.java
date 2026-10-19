package com.booknest;

import com.booknest.dto.AuthResponse;
import com.booknest.dto.RegisterRequest;
import com.booknest.entities.Role;
import com.booknest.exceptions.BadRequestException;
import com.booknest.repositories.UserRepository;
import com.booknest.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRegisterNewCustomerSuccessfully() {
        RegisterRequest req = new RegisterRequest(
                "Jane Doe",
                "jane.doe@example.com",
                "Secret@123",
                "Secret@123",
                "+1 (555) 123-4567"
        );

        AuthResponse response = authService.register(req);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUser());
        assertEquals("jane.doe@example.com", response.getUser().getEmail());
        assertEquals("Jane Doe", response.getUser().getFullName());
        // Enforce default customer role
        assertEquals(Role.CUSTOMER, response.getUser().getRole());
    }

    @Test
    void testRegisterRejectsDuplicateEmail() {
        RegisterRequest req1 = new RegisterRequest(
                "First User",
                "duplicate@example.com",
                "Password@123",
                "Password@123",
                null
        );
        authService.register(req1);

        RegisterRequest req2 = new RegisterRequest(
                "Second User",
                "duplicate@example.com",
                "Password@456",
                "Password@456",
                null
        );

        assertThrows(BadRequestException.class, () -> authService.register(req2));
    }

    @Test
    void testRegisterRejectsMismatchedPasswords() {
        RegisterRequest req = new RegisterRequest(
                "User Name",
                "mismatch@example.com",
                "Password123",
                "PasswordDifferent",
                null
        );

        assertThrows(BadRequestException.class, () -> authService.register(req));
    }
}

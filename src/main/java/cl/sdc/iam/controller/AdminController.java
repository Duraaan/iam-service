package cl.sdc.iam.controller;

import cl.sdc.iam.dto.AuthResponse;

import cl.sdc.iam.dto.*;
import cl.sdc.iam.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AuthService authService;

    @PostMapping("/register-admin")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public AuthResponse createAdmin(@Valid @RequestBody RegistrationAdminRequest request) {
        return authService.registerAdmin(request);
    }

    @PostMapping("/register-staff")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AuthResponse createStaff(@Valid @RequestBody RegistrationStaffRequest request) {
        return authService.registerStaff(request);
    }
}

package cl.sdc.iam.service;

import cl.sdc.iam.dto.*;
import cl.sdc.iam.exception.EmailAlreadyExistsException;
import cl.sdc.iam.exception.PasswordsDoNotMatchException;
import cl.sdc.iam.exception.RoleNotFoundException;
import cl.sdc.iam.model.entity.*;
import cl.sdc.iam.model.enums.RoleName;
import cl.sdc.iam.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Servicio para la autenticación y registro de usuarios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse registerUser(RegistrationUserRequest request) {
        log.info("Intentando registrar usuario con email: {}", request.email());

        // Validaciones comunes
        validateRegistrationCommon(request.email(), request.password(), request.passwordConfirm());

        // Validaciones ESPECÍFICAS de User

        // Crear usuario con rol USER
        User user = createUserWithRole(request.email(), request.password(), RoleName.ROLE_USER);
        userRepository.save(user);

        // Crear perfil de User
        UserProfile staffProfile = UserProfile.builder()
                .user(user)
                .datoEspecificoUser(request.datoEspecificoUser())
                .build();
        userProfileRepository.save(staffProfile);

        log.info("Usuario registrado exitosamente con email: {}", user.getEmail());

        return generateAuthResponse(user);
    }

    public AuthResponse registerStaff(RegistrationStaffRequest request) {
        log.info("Intentando registrar staff con email: {}", request.email());

        // Validaciones comunes
        validateRegistrationCommon(request.email(), request.password(), request.passwordConfirm());

        // Validaciones ESPECÍFICAS de Staff

        // Crear usuario con rol STAFF
        User user = createUserWithRole(request.email(), request.password(), RoleName.ROLE_STAFF);
        userRepository.save(user);

        // Crear perfil de Staff
        StaffProfile staffProfile = StaffProfile.builder()
                .user(user)
                .datoEspecificoStaff(request.datoEspecificoStaff())
                .build();
        staffProfileRepository.save(staffProfile);

        log.info("Staff registrado exitosamente con email: {}", user.getEmail());

        return generateAuthResponse(user);
    }

    public AuthResponse registerAdmin(RegistrationAdminRequest request) {
        log.info("Intentando registrar administrador con email: {}", request.email());

        // Validaciones comunes
        validateRegistrationCommon(request.email(), request.password(), request.passwordConfirm());

        // Validaciones ESPECÍFICAS de Admin

        // Crear usuario con rol ADMIN
        User user = createUserWithRole(request.email(), request.password(), RoleName.ROLE_ADMIN);
        userRepository.save(user);

        // Crear perfil de Admin
        AdminProfile adminProfile = AdminProfile.builder()
                .user(user)
                .datoEspecificoAdmin(request.datoEspecificoAdmin())
                .build();
        adminProfileRepository.save(adminProfile);

        log.info("Administrador registrado exitosamente con email: {}", user.getEmail());

        return generateAuthResponse(user);
    }

    private void validateRegistrationCommon(String email, String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new PasswordsDoNotMatchException("Las contraseñas no coinciden");
        }

        if (userRepository.existsByEmailIncludingInactive(email)) {
            log.warn("Fallo el registro: Email {} ya existe", email);
            throw new EmailAlreadyExistsException("El correo electrónico " + email + " ya está en uso");
        }
    }

    private User createUserWithRole(String email, String password, RoleName roleName) {
        Role role = roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new RoleNotFoundException("Error interno: El rol " + roleName.name() + " no se encontró. Contacte al administrador."));

        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(role))
                .active(true)
                .build();
    }

    private AuthResponse generateAuthResponse(User user) {
        String jwToken = jwtService.generateToken(user);
        return new AuthResponse(jwToken, "Bearer", user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Intentando login para usuario: {}", request.email());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    ));

            User user = (User) authentication.getPrincipal();

            String jwToken = jwtService.generateToken(user);

            log.info("Login exitoso para usuario: {}", user.getEmail());

            return new AuthResponse(jwToken, "Bearer", user.getEmail());
        } catch (AuthenticationException e) {
            log.warn("Falló el login para usuario {}: {}", request.email(), e.getMessage());

            throw e;
        }

    }
}

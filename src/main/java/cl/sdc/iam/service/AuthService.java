package cl.sdc.iam.service;

import cl.sdc.iam.dto.AuthResponse;
import cl.sdc.iam.dto.LoginRequest;
import cl.sdc.iam.dto.RegistrationRequest;
import cl.sdc.iam.exception.EmailAlreadyExistsException;
import cl.sdc.iam.exception.PasswordsDoNotMatchException;
import cl.sdc.iam.exception.RoleNotFoundException;
import cl.sdc.iam.model.entity.Role;
import cl.sdc.iam.model.entity.User;
import cl.sdc.iam.model.enums.RoleName;
import cl.sdc.iam.repository.RoleRepository;
import cl.sdc.iam.repository.UserRepository;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegistrationRequest request) {
        log.info("Intentando registrar usuario con email: {}", request.email());

        if (!request.password().equals(request.passwordConfirm())) {
            throw new PasswordsDoNotMatchException("Las contraseñas no coinciden");
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Fallo el registro: Email {} ya existe", request.email());
            throw new EmailAlreadyExistsException("El correo electrónico " + request.email() + " ya está en uso");
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER.name())
                .orElseThrow(() -> new RoleNotFoundException("Error interno: El rol "  + RoleName.ROLE_USER.name() +  " no se encontró. Contacte al administrador."));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        log.info("Usuario registrado exitosamente con email: {}", user.getEmail());

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

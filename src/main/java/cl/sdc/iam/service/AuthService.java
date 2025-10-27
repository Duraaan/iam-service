package cl.sdc.iam.service;

import cl.sdc.iam.dto.AuthResponse;
import cl.sdc.iam.dto.LoginRequest;
import cl.sdc.iam.dto.RegistrationRequest;
import cl.sdc.iam.exception.EmailAlreadyExistsException;
import cl.sdc.iam.exception.PasswordsDoNotMatchException;
import cl.sdc.iam.exception.RoleNotFoundException;
import cl.sdc.iam.model.entity.Role;
import cl.sdc.iam.model.entity.User;
import cl.sdc.iam.repository.RoleRepository;
import cl.sdc.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Servicio para la autenticación y registro de usuarios.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegistrationRequest request) {

        if (!request.password().equals(request.passwordConfirm())) {
            throw new PasswordsDoNotMatchException("Las contraseñas no coinciden");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("El correo electrónico " + request.email() + " ya esta en uso");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("Error interno: El rol 'ROLE_USER' no se encontró. Contacte al administrador."));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        String jwToken = jwtService.generateToken(user);

        return new AuthResponse(jwToken, "Bearer", user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                ));

        User user = (User) authentication.getPrincipal();

        String jwToken = jwtService.generateToken(user);

        return new AuthResponse(jwToken, "Bearer", user.getEmail());
    }
}

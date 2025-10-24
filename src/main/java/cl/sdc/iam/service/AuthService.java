package cl.sdc.iam.service;

import cl.sdc.iam.dto.AuthResponse;
import cl.sdc.iam.dto.LoginRequest;
import cl.sdc.iam.dto.RegistrationRequest;
import cl.sdc.iam.model.entity.Role;
import cl.sdc.iam.model.entity.User;
import cl.sdc.iam.repository.RoleRepository;
import cl.sdc.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Rol de usuario no encontrado"));

        User user = User.builder()
                .rut(request.rut())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .age(request.age())
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse("Usuario registrado exitosamente!", jwtToken, user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {

          authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(
                          request.email(),
                          request.password()
                  ));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String jwtToken = jwtService.generateToken(user);


        return new AuthResponse("Inicio de sesión exitoso!", jwtToken, user.getEmail());
    }
}

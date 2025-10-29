package cl.sdc.iam.service;

import cl.sdc.iam.dto.UserResponse;
import cl.sdc.iam.model.entity.*;
import cl.sdc.iam.repository.AdminProfileRepository;
import cl.sdc.iam.repository.StaffProfileRepository;
import cl.sdc.iam.repository.UserProfileRepository;
import cl.sdc.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final AdminProfileRepository adminProfileRepository;

    @Override
    @Transactional(readOnly = true) // Buena práctica para métodos de solo lectura
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));
    }

    /**
     * Obtiene una lista de todos los usuarios ACTIVOS.
     * @return Lista de DTOs UserResponse.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToUserResponse) // Llama al método helper
                .collect(Collectors.toList());
    }

    /**
     * Metodo helper privado para convertir una entidad User (y sus perfiles)
     * en un DTO UserResponse aplanado.
     */
    private UserResponse mapToUserResponse(User user) {

        String datoUser = null;
        String datoStaff = null;
        String datoAdmin = null;

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_USER")) {
            datoUser = userProfileRepository.findByUser(user)
                    .map(UserProfile::getDatoEspecificoUser)
                    .orElse(null);
        }

        if (roles.contains("ROLE_STAFF")) {
            datoStaff = staffProfileRepository.findByUser(user)
                    .map(StaffProfile::getDatoEspecificoStaff)
                    .orElse(null);
        }

        if (roles.contains("ROLE_ADMIN")) {
            datoAdmin = adminProfileRepository.findByUser(user)
                    .map(AdminProfile::getDatoEspecificoAdmin)
                    .orElse(null);
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                roles,
                user.getCreatedAt(),
                user.isActive(),
                datoUser,
                datoStaff,
                datoAdmin
        );
    }
}
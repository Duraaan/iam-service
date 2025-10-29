package cl.sdc.iam.service;

import cl.sdc.iam.dto.UpdateUserRequest;
import cl.sdc.iam.dto.UserResponse;
import cl.sdc.iam.exception.EmailAlreadyExistsException;
import cl.sdc.iam.exception.ResourceNotFoundException;
import cl.sdc.iam.model.entity.*;
import cl.sdc.iam.model.enums.RoleName;
import cl.sdc.iam.repository.*;
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
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true) // Buena práctica para métodos de solo lectura
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));
    }

    /**
     * Obtiene una lista de todos los usuarios ACTIVOS.
     *
     * @return Lista de DTOs UserResponse.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToUserResponse) // Llama al metodo helper
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

    /**
     * Obtiene un usuario activo por su ID.
     *
     * @param id El ID del usuario a buscar.
     * @return El DTO UserResponse con los datos del usuario.
     * @throws ResourceNotFoundException si el usuario no se encuentra o no está activo.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        return mapToUserResponse(user);
    }

    /**
     * Actualiza un usuario existente y sus perfiles asociados.
     * Esta operación es transaccional.
     *
     * @param id      El ID del usuario a actualizar.
     * @param request El DTO con los datos a actualizar.
     * @return El UserResponse actualizado.
     */
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        // 1. Buscar al usuario (incluyendo inactivos)
        User user = userRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // 2. Actualizar campos de la entidad User (Base)
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmailIncludingInactive(request.email())) {
                throw new EmailAlreadyExistsException("El email " + request.email() + " ya está en uso");
            }
            user.setEmail(request.email());
        }

        if (request.active() != null) {
            user.setActive(request.active());
        }

        if (request.roles() != null && !request.roles().isEmpty()) {
            Set<Role> newRoles = roleRepository.findByNameIn(request.roles());
            if (newRoles.size() != request.roles().size()) {
                throw new ResourceNotFoundException("Uno o más roles no fueron encontrados");
            }
            user.setRoles(newRoles);
        }

        User updatedUser = userRepository.save(user);

        Set<String> newRoleNames = updatedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        if (newRoleNames.contains(RoleName.ROLE_USER.name())) {
            UserProfile profile = userProfileRepository.findByUser(updatedUser)
                    .orElse(new UserProfile());
            profile.setUser(updatedUser);
            profile.setDatoEspecificoUser(request.datoEspecificoUser());
            userProfileRepository.save(profile);
        }

        if (newRoleNames.contains(RoleName.ROLE_STAFF.name())) {
            StaffProfile profile = staffProfileRepository.findByUser(updatedUser)
                    .orElse(new StaffProfile());
            profile.setUser(updatedUser);
            profile.setDatoEspecificoStaff(request.datoEspecificoStaff());
            staffProfileRepository.save(profile);
        }

        if (newRoleNames.contains(RoleName.ROLE_ADMIN.name())) {
            AdminProfile profile = adminProfileRepository.findByUser(updatedUser)
                    .orElse(new AdminProfile());
            profile.setUser(updatedUser);
            profile.setDatoEspecificoAdmin(request.datoEspecificoAdmin());
            adminProfileRepository.save(profile);
        }

        return mapToUserResponse(updatedUser);
    }

    /**
     * Elimina lógicamente (soft delete) un usuario por su ID.
     * Gracias a @SQLDelete en la entidad User, esto ejecutará un UPDATE
     * en lugar de un DELETE.
     *
     * @param id El ID del usuario a "borrar".
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsByIdIncludingInactive(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }

        userRepository.deleteById(id);
    }
}
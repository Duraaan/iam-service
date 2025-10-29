package cl.sdc.iam.config;

import cl.sdc.iam.model.entity.Role;
import cl.sdc.iam.model.entity.User;
import cl.sdc.iam.model.enums.RoleName;
import cl.sdc.iam.repository.RoleRepository;
import cl.sdc.iam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Clase encargada de inicializar los roles en la base de datos al iniciar la aplicación.
 * Implementa CommandLineRunner para ejecutar el código después de que el contexto de Spring se haya cargado.
 */
@Component
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.super_admin.email}")
    private String superAdminEmail;

    @Value("${app.super_admin.password}")
    private String superAdminPassword;

    public RoleSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        RoleName[] roles = RoleName.values();

        for (RoleName roleName : roles) {
            if (roleRepository.findByName(roleName.name()).isEmpty()) {
                roleRepository.save(new Role(roleName.name()));
            }
        }

        if (userRepository.findByEmail(superAdminEmail).isEmpty()) {
            Role superAdminRole = roleRepository.findByName(RoleName.ROLE_SUPER_ADMIN.name())
                    .orElseThrow(() -> new RuntimeException("El rol 'ROLE_SUPERADMIN' no se encontró en la base de datos."));

            userRepository.save(
                    User.builder()
                            .email(superAdminEmail)
                            .password(passwordEncoder.encode(superAdminPassword))
                            .roles(Set.of(superAdminRole))
                            .active(true)
                            .build()
            );
        }
    }
}
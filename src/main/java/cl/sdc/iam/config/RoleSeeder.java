package cl.sdc.iam.config;

import cl.sdc.iam.model.entity.Role;
import cl.sdc.iam.model.enums.RoleName;
import cl.sdc.iam.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;

/**
 * Clase encargada de inicializar los roles en la base de datos al iniciar la aplicación.
 * Implementa CommandLineRunner para ejecutar el código después de que el contexto de Spring se haya cargado.
 */
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        RoleName[] roles = RoleName.values();

        for (RoleName roleName : roles) {
            if (roleRepository.findByName(roleName.name()).isEmpty()) {
                roleRepository.save(new Role(roleName.name()));
            }
        }
    }
}
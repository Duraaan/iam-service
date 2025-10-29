package cl.sdc.iam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateUserRequest(

        // Campos de la entidad User (base)
        @Email(message = "El formato del email no es valido")
        String email,

        @NotNull(message = "El estado 'active' no puede ser nulo")
        Boolean active,

        @NotEmpty(message = "El usuario debe tener al menos un rol")
        Set<String> roles,

        // Campos del UserProfile
        String datoEspecificoUser,

        // Campos del StaffProfile
        String datoEspecificoStaff,

        // Campos del AdminProfile
        String datoEspecificoAdmin
) {}

package cl.sdc.iam.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(

        long id,
        String email,
        Set<String> roles,
        LocalDateTime createdAt,
        boolean active,

        // Campos del UserProfile
        String datoEspecificoUser,

        // Campos del StaffProfile
        String datoEspecificoStaff,

        // Campos del AdminProfile
        String datoEspecificoAdmin
) {
}

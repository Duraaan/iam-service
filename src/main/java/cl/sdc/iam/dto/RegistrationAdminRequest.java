package cl.sdc.iam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Registro de administrador DTO
 *
 * @param email           Email del administrador
 * @param password        Contraseña del administrador
 * @param passwordConfirm Confirmación de la contraseña
 * @param datoEspecificoAdmin Dato específico del usuario
 */
public record RegistrationAdminRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "La confirmación de contraseña es obligatoria")
        String passwordConfirm,

        @NotBlank(message = "El dato específico es obligatorio")
        String datoEspecificoAdmin
) {
}

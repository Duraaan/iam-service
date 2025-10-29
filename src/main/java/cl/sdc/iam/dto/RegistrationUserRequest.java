package cl.sdc.iam.dto;

import jakarta.validation.constraints.*;

/**
 * Registro de usuario DTO
 *
 * @param email              Email del usuario
 * @param password           Contraseña del usuario
 * @param passwordConfirm    Confirmación de la contraseña
 * @param datoEspecificoUser Dato específico del usuario
 */
public record RegistrationUserRequest(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "La confirmación de contraseña es obligatoria")
        String passwordConfirm,

        @NotBlank(message = "El dato específico es obligatorio")
        String datoEspecificoUser
) {
}


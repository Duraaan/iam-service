package cl.sdc.iam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Login de usuario DTO
 *
 * @param email    Email del usuario
 * @param password Contraseña del usuario
 */
public record LoginRequest(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}

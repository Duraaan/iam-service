package cl.sdc.iam.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Login de usuario DTO
 *
 * @param email    Email del usuario
 * @param password Contraseña del usuario
 */
public record LoginRequest(

        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}

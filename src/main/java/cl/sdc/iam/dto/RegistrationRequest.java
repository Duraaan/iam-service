package cl.sdc.iam.dto;

import jakarta.validation.constraints.*;

/**
 * Registro de usuario DTO
 *
 * @param rut             RUT del usuario
 * @param email           Email del usuario
 * @param password        Contraseña del usuario
 * @param passwordConfirm Confirmación de la contraseña
 * @param firstName       Nombre del usuario
 * @param lastName        Apellido del usuario
 * @param age             Edad del usuario
 */
public record RegistrationRequest(

        @NotBlank(message = "El RUT es obligatorio")
        @Size(min = 8, max = 12, message = "El RUT no puede exceder los 12 caracteres")
        String rut,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank
        String passwordConfirm,

        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @NotNull(message = "La edad es obligatoria")
        @Min(value = 18, message = "Debe ser mayor de 18 años")
        int age
) {
}


package cl.sdc.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(

        @NotBlank(message = "El RUT es obligatorio")
        @Size(min = 8, max = 12, message = "El RUT no puede exceder los 12 caracteres")
        String rut,

        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank
        String passwordConfirm,

        @NotBlank(message = "El nombre es obligatorio")
        String firstName,
        String lastName,

        @NotNull(message = "La edad es obligatoria")
        int age
) {}


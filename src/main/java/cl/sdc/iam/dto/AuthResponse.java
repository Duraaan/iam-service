package cl.sdc.iam.dto;

/**
 * Respuesta de autenticación DTO
 *
 * @param token Token JWT generado
 * @param type  Tipo de token (por ejemplo, "Bearer")
 * @param email Email del usuario autenticado
 */
public record AuthResponse(

        String token,
        String type,
        String email
) {
}

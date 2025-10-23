package cl.sdc.iam.dto;

public record AuthResponse(

        String token,
        String type,
        String email
) {}

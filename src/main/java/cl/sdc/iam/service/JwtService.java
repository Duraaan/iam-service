package cl.sdc.iam.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Servicio para la generación de tokens JWT.
 */
@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${jwt.expiration.ms}")
    private long jwtExpiration;

    /**
     * Genera un token JWT para el usuario proporcionado.
     *
     * @param userDetails Detalles del usuario para el cual se genera el token.
     * @return Token JWT generado.
     */
    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Obtiene la clave de firma para el token JWT.
     *
     * @return Clave de firma.
     */
    private Key getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae todos los valores del token JWT.
     *
     * @param token Token JWT.
     * @return valores extraídos.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

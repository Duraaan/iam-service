package cl.sdc.iam.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representa la entidad principal para la gestión de usuarios en la base de datos.
 * Define la estructura de la tabla 'users', sus propiedades y relaciones.
 * Incluye campos de auditoría y soporte para borrado lógico (soft delete).
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE users SET active = false WHERE id = ?")
@FilterDef(name = "activeUserFilter", defaultCondition = "active = true")
@Filter(name = "activeUserFilter")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean active;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Indica si la cuenta del usuario ha expirado. Una cuenta expirada no puede ser autenticada.
     *
     * @return true si la cuenta es válida (no ha expirado), false si ya no es válida (ha expirado).
     * Por defecto true (no expira).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está bloqueado o desbloqueado. Un usuario bloqueado no puede ser autenticado.
     *
     * @return true si el usuario no está bloqueado, false si lo está.
     * Por defecto true (no bloqueado).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales del usuario (contraseña) han expirado. Credenciales expiradas
     * previenen la autenticación.
     *
     * @return true si las credenciales son válidas (no han expirado), false si ya no son válidas (han expirado).
     * Por defecto true (no expiran).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está habilitado o deshabilitado. Un usuario deshabilitado no puede ser autenticado.
     * Usamos nuestro campo 'active' para esto.
     *
     * @return true si el usuario está habilitado, false si no lo está.
     */
    @Override
    public boolean isEnabled() {
        return this.active;
    }
}
package cl.sdc.iam.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que almacena los datos de perfil específicos para usuarios con ROLE_ADMIN.
 */
@Entity
@Table(name = "admin_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String datoEspecificoAdmin; // Dato específico para administradores (para propósitos de ejemplo)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            unique = true)
    private User user;
}

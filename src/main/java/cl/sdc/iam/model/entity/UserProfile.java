package cl.sdc.iam.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que almacena los datos de perfil específicos para usuarios con ROLE_USER.
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String datoEspecificoUser; // Dato específico para usuarios regulares (para propósitos de ejemplo)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            unique = true)
    private User user;

}
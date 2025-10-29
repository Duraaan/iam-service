package cl.sdc.iam.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que almacena los datos de perfil específicos para usuarios con ROLE_STAFF.
 */
@Entity
@Table(name = "staff_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String datoEspecificoStaff; // Dato específico para personal de staff (para propósitos de ejemplo)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            unique = true)
    private User user;
}

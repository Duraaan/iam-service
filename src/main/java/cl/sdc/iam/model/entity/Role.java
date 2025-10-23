package cl.sdc.iam.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa la entidad principal para la gestión de Roles en la base de datos.
 * Define la estructura de la tabla 'roles' y sus propiedades.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true, nullable = false)
    private String name;

    public Role(String name) {
        this.name = name;
    }
}

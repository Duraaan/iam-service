package cl.sdc.iam.repository;

import cl.sdc.iam.model.entity.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {
}

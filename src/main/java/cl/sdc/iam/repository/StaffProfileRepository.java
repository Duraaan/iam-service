package cl.sdc.iam.repository;

import cl.sdc.iam.model.entity.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {
}

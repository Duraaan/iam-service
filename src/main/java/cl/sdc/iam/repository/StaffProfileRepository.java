package cl.sdc.iam.repository;

import cl.sdc.iam.model.entity.StaffProfile;
import cl.sdc.iam.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {

    Optional<StaffProfile> findByUser(User user);
}

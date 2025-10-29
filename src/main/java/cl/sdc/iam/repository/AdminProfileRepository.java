package cl.sdc.iam.repository;

import cl.sdc.iam.model.entity.AdminProfile;
import cl.sdc.iam.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {

    Optional<AdminProfile> findByUser(User user);
}

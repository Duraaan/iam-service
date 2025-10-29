package cl.sdc.iam.repository;

import cl.sdc.iam.model.entity.User;
import cl.sdc.iam.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser(User user);
}

package cl.sdc.iam.repository;

import cl.sdc.iam.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmailIncludingInactive(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdIncludingInactive(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id")
    boolean existsByIdIncludingInactive(@Param("id") Long id);
}

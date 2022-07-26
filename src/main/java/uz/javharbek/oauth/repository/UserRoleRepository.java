package uz.javharbek.oauth.repository;

import uz.javharbek.oauth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
    Optional<UserRole> findUserRoleByUserIdAndAndRoleId(Long userId,Long roleId);
}

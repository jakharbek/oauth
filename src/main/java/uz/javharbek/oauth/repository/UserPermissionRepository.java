package uz.javharbek.oauth.repository;

import uz.javharbek.oauth.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPermissionRepository extends JpaRepository<UserPermission,Long> {
    Optional<UserPermission> findUserPermissionByUserIdAndAndPermissionId(Long userId,Long permissionId);
}

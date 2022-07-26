package uz.javharbek.oauth.repository;

import uz.javharbek.oauth.entity.AuthPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthPermissionRepository extends JpaRepository<AuthPermission, Long>, CrudRepository<AuthPermission, Long> {
    Optional<AuthPermission> findAuthPermissionByPermissionName(String permissionName);
}

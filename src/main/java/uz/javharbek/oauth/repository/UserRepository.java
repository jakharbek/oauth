package uz.javharbek.oauth.repository;

import uz.javharbek.oauth.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AuthUser, Long>, CrudRepository<AuthUser, Long> {
    Optional<AuthUser> findByUserName(String username);

    Optional<AuthUser> findByUserNameOrEmail(String username, String email);

    Optional<AuthUser> findByUserNameOrEmailOrPhone(String username, String email, String phone);

    Optional<AuthUser> findByPhone(String phone);

    Optional<AuthUser> findByEmail(String userName);

}

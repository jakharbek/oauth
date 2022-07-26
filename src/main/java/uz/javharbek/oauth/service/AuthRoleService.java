package uz.javharbek.oauth.service;

import uz.javharbek.oauth.entity.AuthRole;
import uz.javharbek.oauth.entity.AuthUser;
import uz.javharbek.oauth.entity.UserRole;
import uz.javharbek.oauth.exceptions.AppException;
import uz.javharbek.oauth.repository.AuthRoleRepository;
import uz.javharbek.oauth.repository.UserRepository;
import uz.javharbek.oauth.repository.UserRoleRepository;
import uz.javharbek.oauth.specification.authRole.AuthRoleCriteriaRepository;
import uz.javharbek.oauth.specification.authRole.AuthRolePage;
import uz.javharbek.oauth.specification.authRole.AuthRoleSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthRoleService {
    @Autowired
    AuthRoleRepository authRoleRepository;

    @Autowired
    AuthRoleCriteriaRepository authRoleCriteriaRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;


    public Page<AuthRole> getList(AuthRolePage authRolePage,
                                  AuthRoleSearchCriteria authRoleSearchCriteria) {
        return authRoleCriteriaRepository.findAllWithFilters(authRolePage, authRoleSearchCriteria);
    }

    public UserRole grant(Long user_id, String roleName) throws AppException {

        Optional<AuthRole> authRoleOptional = authRoleRepository.findAuthRoleByRoleName(roleName);
        if (authRoleOptional.isEmpty()) {
            throw new AppException("Auth Role not found");
        }

        AuthRole authRole = authRoleOptional.get();

        Optional<AuthUser> authUserOptional = userRepository.findById(user_id);
        if (authUserOptional.isEmpty()) {
            throw new AppException("User not found");
        }

        AuthUser user = authUserOptional.get();

        Optional<UserRole> userRoleOptional = userRoleRepository.findUserRoleByUserIdAndAndRoleId(user.getId(), authRole.getId());

        if (userRoleOptional.isPresent()) {
            throw new AppException("User is granted early");
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(authRole.getId());

        return userRoleRepository.save(userRole);
    }

    public UserRole revoke(Long user_id, String roleName) throws AppException {

        Optional<AuthRole> authRoleOptional = authRoleRepository.findAuthRoleByRoleName(roleName);
        if (authRoleOptional.isEmpty()) {
            throw new AppException("Auth Permission not found");
        }

        AuthRole authRole = authRoleOptional.get();

        Optional<AuthUser> authUserOptional = userRepository.findById(user_id);
        if (authUserOptional.isEmpty()) {
            throw new AppException("User not found");
        }

        AuthUser user = authUserOptional.get();

        Optional<UserRole> userRoleOptional = userRoleRepository.findUserRoleByUserIdAndAndRoleId(user.getId(), authRole.getId());

        if (userRoleOptional.isEmpty()) {
            throw new AppException("User is revoke early");
        }

        UserRole userRole = userRoleOptional.get();
        userRoleRepository.delete(userRole);
        return userRole;
    }

}

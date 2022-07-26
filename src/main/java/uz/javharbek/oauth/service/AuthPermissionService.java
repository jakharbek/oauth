package uz.javharbek.oauth.service;

import uz.javharbek.oauth.entity.AuthPermission;
import uz.javharbek.oauth.entity.AuthUser;
import uz.javharbek.oauth.entity.UserPermission;
import uz.javharbek.oauth.exceptions.AppException;
import uz.javharbek.oauth.repository.AuthPermissionRepository;
import uz.javharbek.oauth.repository.UserPermissionRepository;
import uz.javharbek.oauth.repository.UserRepository;
import uz.javharbek.oauth.specification.authPermission.AuthPermissionCriteriaRepository;
import uz.javharbek.oauth.specification.authPermission.AuthPermissionPage;
import uz.javharbek.oauth.specification.authPermission.AuthPermissionSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthPermissionService {
    @Autowired
    AuthPermissionRepository authPermissionRepository;

    @Autowired
    AuthPermissionCriteriaRepository authPermissionCriteriaRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;

    public Page<AuthPermission> getList(AuthPermissionPage authPermissionPage,
                                        AuthPermissionSearchCriteria authPermissionSearchCriteria) {
        return authPermissionCriteriaRepository.findAllWithFilters(authPermissionPage, authPermissionSearchCriteria);
    }

    public UserPermission grant(Long user_id, String permission_name) throws AppException {

        Optional<AuthPermission> authPermissionOptional = authPermissionRepository.findAuthPermissionByPermissionName(permission_name);
        if (authPermissionOptional.isEmpty()) {
            throw new AppException("Auth Permission not found");
        }

        AuthPermission authPermission = authPermissionOptional.get();

        Optional<AuthUser> authUserOptional = userRepository.findById(user_id);
        if (authUserOptional.isEmpty()) {
            throw new AppException("User not found");
        }

        AuthUser user = authUserOptional.get();

        Optional<UserPermission> userPermissionOptional = userPermissionRepository.findUserPermissionByUserIdAndAndPermissionId(user.getId(), authPermission.getId());

        if (userPermissionOptional.isPresent()) {
            throw new AppException("User is granted early");
        }

        UserPermission userPermission = new UserPermission();
        userPermission.setUserId(user.getId());
        userPermission.setPermissionId(authPermission.getId());

        return userPermissionRepository.save(userPermission);
    }

    public UserPermission revoke(Long user_id, String permission_name) throws AppException {

        Optional<AuthPermission> authPermissionOptional = authPermissionRepository.findAuthPermissionByPermissionName(permission_name);
        if (authPermissionOptional.isEmpty()) {
            throw new AppException("Auth Permission not found");
        }

        AuthPermission authPermission = authPermissionOptional.get();

        Optional<AuthUser> authUserOptional = userRepository.findById(user_id);
        if (authUserOptional.isEmpty()) {
            throw new AppException("User not found");
        }

        AuthUser user = authUserOptional.get();

        Optional<UserPermission> userPermissionOptional = userPermissionRepository.findUserPermissionByUserIdAndAndPermissionId(user.getId(), authPermission.getId());

        if (userPermissionOptional.isEmpty()) {
            throw new AppException("User is revoke early");
        }

        UserPermission userPermission = userPermissionOptional.get();
        userPermissionRepository.delete(userPermission);
        return userPermission;
    }
}

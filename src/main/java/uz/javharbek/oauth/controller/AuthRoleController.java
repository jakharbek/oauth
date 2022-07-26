package uz.javharbek.oauth.controller;

import uz.javharbek.oauth.dto.AuthRoleCreateRequestDTO;
import uz.javharbek.oauth.dto.AuthRoleUpdateRequestDTO;
import uz.javharbek.oauth.entity.AuthRole;
import uz.javharbek.oauth.entity.UserRole;
import uz.javharbek.oauth.exceptions.AppException;
import uz.javharbek.oauth.repository.AuthRoleRepository;
import uz.javharbek.oauth.service.AuthRoleService;
import uz.javharbek.oauth.specification.authRole.AuthRolePage;
import uz.javharbek.oauth.specification.authRole.AuthRoleSearchCriteria;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth-role")
public class AuthRoleController {

    @Autowired
    AuthRoleRepository authRoleRepository;

    @Autowired
    AuthRoleService authRoleService;

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthRole create(@Valid @RequestBody AuthRoleCreateRequestDTO requestDTO) throws AppException {
        Optional<AuthRole> authRoleOptional = authRoleRepository.findAuthRoleByRoleName(requestDTO.getRoleName());
        if (authRoleOptional.isPresent()) {
            throw new AppException("Role is exist");
        }
        AuthRole authRole = new AuthRole();
        authRole.setRoleName(requestDTO.getRoleName());
        authRole.setDescription(requestDTO.getDescription());
        return authRoleRepository.save(authRole);
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PutMapping("/{id}")
    public AuthRole update(@PathVariable Long id, @Valid @RequestBody AuthRoleUpdateRequestDTO requestDTO) throws AppException {
        Optional<AuthRole> authRoleOptional = authRoleRepository.findById(id);
        if(authRoleOptional.isEmpty()){
            throw new AppException("Not Found");
        }
        AuthRole authRole = authRoleOptional.get();
        authRole.setDescription(requestDTO.getDescription());
        return authRoleRepository.save(authRole);
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @DeleteMapping("/{id}")
    public AuthRole delete(@PathVariable Long id) throws AppException {
        Optional<AuthRole> authRoleOptional = authRoleRepository.findById(id);
        if(authRoleOptional.isEmpty()){
            throw new AppException("Not Found");
        }
        AuthRole authRole = authRoleOptional.get();
        authRoleRepository.save(authRole);
        return authRole;
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Page<AuthRole> list(AuthRolePage authRolePage,
                               AuthRoleSearchCriteria authRoleSearchCriteria){
        return authRoleService.getList(authRolePage, authRoleSearchCriteria);
    }


    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/{user}/{name}")
    @ResponseStatus(HttpStatus.OK)
    public UserRole grant(@PathVariable Long user, @PathVariable String name) throws AppException {
        return authRoleService.grant(user,name);
    }


    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @DeleteMapping("/{user}/{name}")
    @ResponseStatus(HttpStatus.OK)
    public UserRole revoke(@PathVariable Long user, @PathVariable String name) throws AppException {
        return authRoleService.revoke(user,name);
    }
}

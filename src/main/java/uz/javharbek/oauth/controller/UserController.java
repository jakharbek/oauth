package uz.javharbek.oauth.controller;

import uz.javharbek.oauth.dto.*;
import uz.javharbek.oauth.entity.AuthPermission;
import uz.javharbek.oauth.entity.AuthRole;
import uz.javharbek.oauth.entity.AuthUser;
import uz.javharbek.oauth.entity.File;
import uz.javharbek.oauth.exceptions.AppException;
import uz.javharbek.oauth.exceptions.CdnServerNotFoundException;
import uz.javharbek.oauth.exceptions.PasswordIncorrectException;
import uz.javharbek.oauth.exceptions.UserNotFoundException;
import uz.javharbek.oauth.repository.UserRepository;
import uz.javharbek.oauth.service.*;
import uz.javharbek.oauth.service.AuthUserService;
import uz.javharbek.oauth.service.FileService;
import uz.javharbek.oauth.specification.authUser.AuthUserPage;
import uz.javharbek.oauth.specification.authUser.AuthUserSearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.javharbek.oauth.dto.*;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/oauth/users")
public class UserController {

    @Autowired
    AuthUserService authUserService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    FileService fileService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegisteredDTO register(@RequestBody UserDto userDto) throws AppException, IOException {
        RegisterDTO registerDTO = authUserService.register(userDto);
        RegisteredDTO registeredDTO = new RegisteredDTO();
        UserShortDTO userShortDTO = new ObjectMapper().convertValue(registerDTO.getAuthUser(), UserShortDTO.class);
        TokenShortDTO tokenShortDTO = new ObjectMapper().convertValue(registerDTO.getAuthToken(), TokenShortDTO.class);
        tokenShortDTO.setIdentityPhone(userDto.getPhone());
        registeredDTO.setUser(userShortDTO);
        registeredDTO.setToken(tokenShortDTO);
        return registeredDTO;
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public GetMeDTO me() throws CdnServerNotFoundException {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<AuthUser> userOptional = userRepository.findByUserName(principal.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        AuthUser user = userOptional.get();
        GetMeDTO me = new GetMeDTO();
        me.setUsername(user.getUserName());
        me.setPhone(user.getPhone());
        me.setEmail(user.getEmail());
        me.setId(user.getId());
        me.setAuthorities(authUserService.getUserRolesPermissions(user));
        me.setStatus(user.getStatus());
        me.setAvatarIconType(user.getAvatarIconType());
        if(user.getAvatar() != null){
            me.setAvatar(fileService.getAbsoluteUrl(user.getAvatar()));
        }
        return me;
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public OAuth2AccessToken logout(@RequestHeader("Authorization") String Authorization) {
        String tokenValue = Authorization.replace("Bearer", "").trim();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        tokenStore.removeAccessToken(accessToken);
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        tokenStore.removeRefreshToken(refreshToken);
        return accessToken;
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@Valid @RequestBody ChangePasswordProfileRequestDTO dto) throws PasswordIncorrectException {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authUserService.ChangePasswordProfile(dto.getCurrent(),dto.getPassword(),principal.getUsername());
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/change-avatar")
    @Consumes("multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public FileResponseDTO changeAvatar(@RequestPart("file") MultipartFile file) throws IOException, CdnServerNotFoundException {
        UploadedDTO dto = fileService.transferToStorage(file);
        File uploadFile = fileService.save(dto);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<AuthUser> userOptional = userRepository.findByUserName(principal.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        AuthUser user = userOptional.get();
        authUserService.SetAvatar(uploadFile,user);
        FileResponseDTO fileResponseDTO = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule())
                .convertValue(uploadFile, FileResponseDTO.class);
        fileResponseDTO.setAbsoluteUrl(fileService.getAbsoluteUrl(uploadFile));
        return fileResponseDTO;
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/avatar-icon-type")
    @ResponseStatus(HttpStatus.OK)
    public String setAvatarIconType(@RequestBody ChangeAvatarIconTypeRequestDTO changeAvatarIconTypeRequestDTO, @RequestHeader("Authorization") String Authorization) throws UserNotFoundException {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<AuthUser> userOptional = userRepository.findByUserName(principal.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        AuthUser user = userOptional.get();
        authUserService.setAvatarIconType(user.getId(),changeAvatarIconTypeRequestDTO.getAvatarIconType());
        return user.getAvatarIconType();
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserItemDTO> list(AuthUserPage authUserPage,
                               AuthUserSearchCriteria authUserSearchCriteria) throws CdnServerNotFoundException {
        return authUserService.getList(authUserPage, authUserSearchCriteria);
    }

    @PostMapping("/login")
    public GetTokenDTO login(@Valid @RequestBody LoginDTO loginDTO) throws IOException, AppException {
        return authUserService.login(loginDTO);
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("/permissions/list/user/{user}")
    @ResponseStatus(HttpStatus.OK)
    public Set<AuthPermission> permissionListUser(@PathVariable Long user) throws AppException {
        Optional<AuthUser> userOptional = userRepository.findById(user);
        if(userOptional.isEmpty()){
            throw new AppException("User not found");
        }
        AuthUser userEntity = userOptional.get();

        return userEntity.getPermissions();
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("/roles/list/user/{user}")
    @ResponseStatus(HttpStatus.OK)
    public Set<AuthRole> roleListUser(@PathVariable Long user) throws AppException {
        Optional<AuthUser> userOptional = userRepository.findById(user);
        if(userOptional.isEmpty()){
            throw new AppException("User not found");
        }
        AuthUser userEntity = userOptional.get();

        return userEntity.getRoles();
    }

}

package uz.javharbek.oauth.service;

import uz.javharbek.oauth.dto.*;
import uz.javharbek.oauth.entity.*;
import uz.javharbek.oauth.enums.AuthTokenDataTypeEnum;
import uz.javharbek.oauth.enums.AuthTokenTypeEnum;
import uz.javharbek.oauth.enums.AuthUserStatusEnum;
import uz.javharbek.oauth.exceptions.*;
import uz.javharbek.oauth.exceptions.*;
import uz.javharbek.oauth.repository.UserRepository;
import uz.javharbek.oauth.repository.UserRoleRepo;
import uz.javharbek.oauth.security.jwt.JwtUtils;
import uz.javharbek.oauth.specification.authUser.AuthUserCriteriaRepository;
import uz.javharbek.oauth.specification.authUser.AuthUserPage;
import uz.javharbek.oauth.specification.authUser.AuthUserSearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.javharbek.oauth.dto.*;
import uz.javharbek.oauth.entity.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepo userRoleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthTokenService tokenService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private DefaultTokenServices tokenServices;

    @Autowired
    private AuthUserCriteriaRepository authUserCriteriaRepository;

    @Value("${security.auth-server}")
    private String auth_server;
    @Value("${security.auth-server-client-id}")
    private String client_id;
    @Value("${security.auth-server-client-secret}")
    private String client_secret;

    @Transactional
    public RegisterDTO register(UserDto userDto) throws AppException, IOException {
        AuthUser authUser = new ObjectMapper().convertValue(userDto, AuthUser.class);
        HashSet<AuthRole> firstRoles = new HashSet<>();
        firstRoles.add(userRoleRepo.findByRoleNameContaining("user"));
        authUser.setRoles(firstRoles);
        authUser.setStatus(AuthUserStatusEnum.INACTIVE);
        authUser.setUserName(userDto.getPhone());

        Optional<AuthUser> optUserPhone = userRepository.findByPhone(userDto.getPhone());
        if (optUserPhone.isPresent()) {
            AuthUser user = optUserPhone.get();
            if (user.getStatus().equals(AuthUserStatusEnum.ACTIVE)) {
                throw new UserExistException("User already exist");
            }
            ResendConfirmRequestDTO resendConfirmRequestDTO = new ResendConfirmRequestDTO();
            resendConfirmRequestDTO.setPhone(userDto.getPhone());
            AuthToken token = tokenService.resendConfirm(resendConfirmRequestDTO);

            RegisterDTO registerDTO = new RegisterDTO();
            registerDTO.setAuthUser(user);
            registerDTO.setAuthToken(token);

            return registerDTO;
        }

        AuthUser createdAuthUser = userRepository.save(authUser);
        CreateTokenDTO createTokenDTO = new CreateTokenDTO();
        createTokenDTO.setIdentityId(createdAuthUser.getId().toString());
        createTokenDTO.setData(userDto.getPhone());
        createTokenDTO.setData_type(AuthTokenDataTypeEnum.PHONE);
        createTokenDTO.setType(AuthTokenTypeEnum.PHONE_CONFIRM);
        AuthToken createdToken = tokenService.create(createTokenDTO);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setAuthUser(createdAuthUser);
        registerDTO.setAuthToken(createdToken);

        return registerDTO;
    }

    public AuthUser changePhone(Long id, String phone) throws UserNotFoundException {
        Optional<AuthUser> authUserOptional = userRepository.findById(id);
        if (!authUserOptional.isPresent()) {
            throw new UserNotFoundException("User not found");
        }

        AuthUser user = authUserOptional.get();
        user.setPhone(phone);
        userRepository.save(user);
        return user;
    }

    public AuthUser active(Long id) throws UserNotFoundException {
        Optional<AuthUser> authUserOptional = userRepository.findById(id);
        if (!authUserOptional.isPresent()) {
            throw new UserNotFoundException("User not found");
        }

        AuthUser user = authUserOptional.get();
        user.setStatus(AuthUserStatusEnum.ACTIVE);
        userRepository.save(user);
        return user;
    }

    public AuthUser setPassword(Long id, String password) throws UserNotFoundException {
        Optional<AuthUser> authUserOptional = userRepository.findById(id);
        if (!authUserOptional.isPresent()) {
            throw new UserNotFoundException("User not found");
        }

        AuthUser user = authUserOptional.get();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public List<String> getUserRolesPermissions(AuthUser user) {
        List<String> authorities_roles = user.getRoles()
                .stream().map(AuthRole::getRoleName)
                .collect(Collectors.toList());
        List<String> authorities_permissions = user.getPermissions()
                .stream().map(AuthPermission::getPermissionName)
                .collect(Collectors.toList());
        List<String> authorities = new ArrayList<>();
        authorities.addAll(authorities_roles);
        authorities.addAll(authorities_permissions);
        return authorities;
    }

    public void ChangePasswordProfile(String currentPassword, String newPassword, String username) throws PasswordIncorrectException {
        Optional<AuthUser> userOptional = userRepository.findByUserName(username);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        AuthUser user = userOptional.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordIncorrectException("Password wrong");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }

    public void SetAvatar(File file, AuthUser authUser) {
        authUser.setAvatar(file);
        userRepository.save(authUser);
    }

    public GetTokenDTO login(LoginDTO loginDTO) throws IOException, AppException {
        Gson gson = new Gson();
        String decodeBasic = (client_id + ":" + client_secret);
        String basic = Base64.getEncoder().encodeToString(decodeBasic.getBytes());

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(auth_server + "/oauth/token?username=" + URLEncoder.encode(loginDTO.getLogin(), StandardCharsets.UTF_8) + "&password=" + URLEncoder.encode(loginDTO.getPassword(), StandardCharsets.UTF_8) + "&grant_type=password")
                .method("POST", body)
                .addHeader("Authorization", "Basic " + basic)
                .build();
        Response response = client.newCall(request).execute();

        ResponseBody responseBody = response.body();
        String data = responseBody.string();
        if (response.code() != HttpStatus.SC_OK) {
            ServerResponseDTO serverResponseDTO = gson.fromJson(data, ServerResponseDTO.class);
            throw new AppException(serverResponseDTO.getMessage());
        }
        return gson.fromJson(data, GetTokenDTO.class);
    }

    public OAuth2AccessToken loginWithoutPassword(String username) throws UserNotFoundException {

        if (!userRepository.findByUserName(username).isPresent()) {
            throw new UserNotFoundException("User not found");
        }

        HashMap<String, String> authorizationParameters = new HashMap<String, String>();
        authorizationParameters.put("scope", "read write");
        authorizationParameters.put("username", username);
        authorizationParameters.put("client_id", client_id);
        authorizationParameters.put("client_secret", client_secret);
        authorizationParameters.put("grant", "password");

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("user"));

        Set<String> responseType = new HashSet<String>();
        responseType.add("password");

        Set<String> scopes = new HashSet<String>();
        scopes.add("read");
        scopes.add("write");

        OAuth2Request authorizationRequest = new OAuth2Request(
                authorizationParameters, client_id,
                authorities, true, scopes, null, "",
                responseType, null);

        User userPrincipal = new User(username, "", true, true, true, true, authorities);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, authorities);

        OAuth2Authentication authenticationRequest = new OAuth2Authentication(
                authorizationRequest, authenticationToken);

        return tokenServices.createAccessToken(authenticationRequest);
    }

    public AuthUser setAvatarIconType(Long id, String avatarIconType) throws UserNotFoundException {
        Optional<AuthUser> authUserOptional = userRepository.findById(id);
        if (!authUserOptional.isPresent()) {
            throw new UserNotFoundException("User not found");
        }

        AuthUser user = authUserOptional.get();
        user.setAvatarIconType(avatarIconType);
        userRepository.save(user);
        return user;
    }


    public Page<UserItemDTO> getList(AuthUserPage authUserPage,
                                     AuthUserSearchCriteria authUserSearchCriteria) throws CdnServerNotFoundException {
        return authUserCriteriaRepository.findAllWithFilters(authUserPage, authUserSearchCriteria);
    }

}

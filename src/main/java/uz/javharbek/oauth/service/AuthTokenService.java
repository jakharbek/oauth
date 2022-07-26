package uz.javharbek.oauth.service;

import uz.javharbek.oauth.dto.*;
import uz.javharbek.oauth.entity.AuthToken;
import uz.javharbek.oauth.entity.AuthUser;
import uz.javharbek.oauth.enums.*;
import uz.javharbek.oauth.exceptions.*;
import uz.javharbek.oauth.exceptions.*;
import uz.javharbek.oauth.repository.AuthTokenRepository;
import uz.javharbek.oauth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.javharbek.oauth.dto.*;
import uz.javharbek.oauth.enums.AuthTokenDataTypeEnum;
import uz.javharbek.oauth.enums.AuthTokenStatusEnum;
import uz.javharbek.oauth.enums.AuthTokenTypeEnum;
import uz.javharbek.oauth.enums.AuthUserStatusEnum;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthTokenService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthUserService userService;
    @Autowired
    private AuthTokenRepository authTokenRepository;

    public static int rnd(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }

    public AuthToken create(CreateTokenDTO createTokenDTO) {
        AuthToken authToken = new ObjectMapper().convertValue(createTokenDTO, AuthToken.class);
        authToken.setToken(UUID.randomUUID().toString());
        authToken.setSecret((String.valueOf(rnd(10000, 99999))));
//        authToken.setSecret("11111");
        authToken.setStatus(AuthTokenStatusEnum.ACTIVE);
        authToken.setExpiredDuration(18000);
        return authTokenRepository.save(authToken);
    }

    public ConfirmedDTO confirm(TokenConfirmDTO tokenConfirmDTO) throws AuthTokenExpiredException, AuthTokenInactiveException, AuthTokenNotFoundException, TokenConfirmDataException, UserNotFoundException {
        Optional<AuthToken> authTokenQuery = authTokenRepository.findByTokenAndSecret(tokenConfirmDTO.getToken(), tokenConfirmDTO.getSecret());
        if (!authTokenQuery.isPresent()) {
            throw new AuthTokenNotFoundException("Auth token not found");
        }

        AuthToken authToken = authTokenQuery.get();
        if (authToken.getStatus().equals(AuthTokenStatusEnum.INACTIVE)) {
            throw new AuthTokenInactiveException("Auth token is inactive");
        }

        LocalDateTime createdDatetime = authToken.getCreatedDatetime();
        if (createdDatetime.plusSeconds(authToken.getExpiredDuration()).isBefore(LocalDateTime.now())) {
            throw new AuthTokenExpiredException("Auth token is expired");
        }

        if (authToken.getType().equals(AuthTokenTypeEnum.PHONE_CONFIRM)) {
            userService.changePhone(Long.parseLong(authToken.getIdentityId()), authToken.getData());
            userService.active(Long.parseLong(authToken.getIdentityId()));

            if (tokenConfirmDTO.getData() == null || tokenConfirmDTO.getData().length() == 0) {
                throw new TokenConfirmDataException("Data: Password required");
            }
            if (tokenConfirmDTO.getData().length() < 8) {
                throw new TokenConfirmDataException("Data: Password must be more than 8 characters");
            }

            userService.setPassword(Long.parseLong(authToken.getIdentityId()), tokenConfirmDTO.getData());
        }

        if (authToken.getType().equals(AuthTokenTypeEnum.PASSWORD_RESET)) {
            userService.changePhone(Long.parseLong(authToken.getIdentityId()), authToken.getData());
            userService.active(Long.parseLong(authToken.getIdentityId()));

            if (tokenConfirmDTO.getData() == null || tokenConfirmDTO.getData().length() == 0) {
                throw new TokenConfirmDataException("Data: Password required");
            }

            if (tokenConfirmDTO.getData().length() < 8) {
                throw new TokenConfirmDataException("Data: Password must be more than 8 characters");
            }

            userService.setPassword(Long.parseLong(authToken.getIdentityId()), tokenConfirmDTO.getData());
        }

        Optional<AuthUser> authUserOptional = userRepository.findById(Long.parseLong(authToken.getIdentityId()));
        if (!authUserOptional.isPresent()) {
            throw new UserNotFoundException("Token user not found");
        }
        AuthUser authUser = authUserOptional.get();
        authToken.setStatus(AuthTokenStatusEnum.INACTIVE);
        authTokenRepository.save(authToken);
        ConfirmedDTO confirmedDTO = new ConfirmedDTO();
        confirmedDTO.setToken(new ObjectMapper().convertValue(authToken, TokenShortDTO.class));
        confirmedDTO.setUser(new ObjectMapper().convertValue(authUser, UserShortDTO.class));
        return confirmedDTO;
    }

    public AuthToken resendConfirm(ResendConfirmRequestDTO dto) throws UserIsActiveException, UserNotFoundException {
        CreateTokenDTO createTokenDTO = new CreateTokenDTO();
        Optional<AuthUser> authUserOptional = userRepository.findByPhone(dto.getPhone());
        if (!authUserOptional.isPresent()) {
            throw new UserNotFoundException("Token user not found");
        }
        AuthUser authUser = authUserOptional.get();
        if (authUser.getStatus().equals(AuthUserStatusEnum.ACTIVE)) {
            throw new UserIsActiveException("User is active");
        }

        createTokenDTO.setData(dto.getPhone());
        createTokenDTO.setType(AuthTokenTypeEnum.PHONE_CONFIRM);
        createTokenDTO.setData_type(AuthTokenDataTypeEnum.PHONE);
        createTokenDTO.setIdentityId(authUser.getId().toString());
        return create(createTokenDTO);

    }

    public AuthToken resetPassword(ResetPasswordRequestDTO dto) throws UserIsNotActiveException, UserNotFoundException {
        CreateTokenDTO createTokenDTO = new CreateTokenDTO();

        Optional<AuthUser> authUserOptional = userRepository.findByPhone(dto.getPhone());
        if (!authUserOptional.isPresent()) {
            throw new UserNotFoundException("Token user not found");
        }

        AuthUser authUser = authUserOptional.get();
        if (!authUser.getStatus().equals(AuthUserStatusEnum.ACTIVE)) {
            throw new UserIsNotActiveException("User is not active");
        }

        createTokenDTO.setData(dto.getPhone());
        createTokenDTO.setType(AuthTokenTypeEnum.PASSWORD_RESET);
        createTokenDTO.setData_type(AuthTokenDataTypeEnum.PHONE);
        createTokenDTO.setIdentityId(authUser.getId().toString());
        return create(createTokenDTO);

    }
}

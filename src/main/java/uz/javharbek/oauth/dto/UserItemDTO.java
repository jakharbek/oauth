package uz.javharbek.oauth.dto;

import uz.javharbek.oauth.entity.AuthPermission;
import uz.javharbek.oauth.entity.AuthRole;
import uz.javharbek.oauth.entity.File;
import uz.javharbek.oauth.enums.AuthUserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserItemDTO {
    Long id;
    String userName;
    String password;
    String email;
    String phone;
    String passport;
    String inn;
    String personalIdentificationNumber;
    AuthUserStatusEnum status;
    LocalDateTime createdDatetime;
    Set<AuthRole> roles;
    Set<AuthPermission> permissions;
    FidoIdentityCustomerResponseRequestBodyDTO fidoGspIdentity;
    File avatar;
    String avatarUrl;
    String avatarIconType;
}

package uz.javharbek.oauth.dto;

import uz.javharbek.oauth.enums.AuthUserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetMeDTO {
    String username;
    String email;
    String phone;
    Long id;
    List<String> authorities;
    AuthUserStatusEnum status;
    String avatar;
    String avatarIconType;
}

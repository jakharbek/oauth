package uz.javharbek.oauth.dto;

import uz.javharbek.oauth.entity.AuthToken;
import uz.javharbek.oauth.entity.AuthUser;
import lombok.Data;

@Data
public class RegisterDTO {
    private AuthUser authUser;
    private AuthToken authToken;
}

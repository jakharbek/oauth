package uz.javharbek.oauth.dto;

import uz.javharbek.oauth.enums.AuthTokenDataTypeEnum;
import uz.javharbek.oauth.enums.AuthTokenTypeEnum;
import lombok.Data;

@Data
public class CreateTokenDTO {
    private String data;
    private AuthTokenDataTypeEnum data_type;
    private AuthTokenTypeEnum type;
    private String identityId;
}

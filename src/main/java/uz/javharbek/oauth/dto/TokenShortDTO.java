package uz.javharbek.oauth.dto;

import uz.javharbek.oauth.enums.AuthTokenStatusEnum;
import uz.javharbek.oauth.enums.AuthTokenTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenShortDTO {
    private Long id;

    private String token;

    private String data;

    private String data_type;

    private AuthTokenTypeEnum type;

    private AuthTokenStatusEnum status;

    private Integer expiredDuration;

    private String identityId;

    private String identityPhone;
}

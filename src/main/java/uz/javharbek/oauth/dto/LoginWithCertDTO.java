package uz.javharbek.oauth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginWithCertDTO {
    @NotBlank
    CertInfoDTO certInfo;
    @NotBlank
    String signedMsg;
}

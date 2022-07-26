package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class SignDTO {
    String issuer;
    String notafter;
    String notbefore;
    String serialnumber;
    String thumbprint;
    String subject;

    CertInfoDTO getCertInfoDTO(){
        return new CertInfoDTO(this);
    }
}

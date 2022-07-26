package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class FidoIdentityCustomerResponseDTO {
    Long code;
    String msg;
    FidoIdentityCustomerResponseRequestBodyDTO responseBody;
}

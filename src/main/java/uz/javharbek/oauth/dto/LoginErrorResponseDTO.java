package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class LoginErrorResponseDTO {
    private String error;
    private String error_description;

}

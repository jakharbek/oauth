package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class FidoGetTokenRequestDTO {
    String password;
    String username;
}

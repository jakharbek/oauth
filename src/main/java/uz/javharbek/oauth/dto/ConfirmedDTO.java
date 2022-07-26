package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class ConfirmedDTO {
    TokenShortDTO token;
    UserShortDTO user;
}

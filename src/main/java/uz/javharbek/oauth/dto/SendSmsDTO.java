package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class SendSmsDTO {
    private String text;
    private String phone;
}

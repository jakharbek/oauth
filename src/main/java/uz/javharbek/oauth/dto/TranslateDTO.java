package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class TranslateDTO {
    String locale;
    String key;
    String translation;
}

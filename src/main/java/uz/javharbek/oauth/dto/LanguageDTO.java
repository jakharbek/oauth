package uz.javharbek.oauth.dto;

import uz.javharbek.oauth.entity.Language;
import lombok.Data;

import java.util.List;

@Data
public class LanguageDTO {
    Long id;
    String locale;
    String key;
    String content;
    List<Language> translations;
}

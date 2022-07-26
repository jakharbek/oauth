package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class ErrorDTO {
    private String field;
    private String message;

    public ErrorDTO(String field, String message) {
        this.field = field;
        this.message = message;
    }
}

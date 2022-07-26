package uz.javharbek.oauth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordProfileRequestDTO {
    @NotBlank
    @Size(min = 2)
    String current;

    @NotBlank
    @Size(min = 8)
    String password;

}

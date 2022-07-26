package uz.javharbek.oauth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TestDTO {
    @NotBlank
    @Size(min = 2, max = 5)
    private String phone;
    @NotBlank
    @Size(min = 2, message = "user name should have at least 2 characters")
    private String username;
    @NotBlank
    @Size(min = 2, message = "user name should have at least 2 characters")
    private String pin;
}

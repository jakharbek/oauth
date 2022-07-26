package uz.javharbek.oauth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthPermissionUpdateRequestDTO {
    @NotBlank
    String description;
}

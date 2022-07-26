package uz.javharbek.oauth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthPermissionCreateRequestDTO {
    @NotBlank
    String permissionName;
    @NotBlank
    String description;
}

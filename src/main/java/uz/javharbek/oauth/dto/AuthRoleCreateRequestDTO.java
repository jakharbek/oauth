package uz.javharbek.oauth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthRoleCreateRequestDTO {
    @NotBlank
    String roleName;
    String description;
}

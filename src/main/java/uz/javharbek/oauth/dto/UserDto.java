package uz.javharbek.oauth.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDto {

    @NotEmpty
    @Size(min = 9,max = 9, message = "Phone number is not valid")
    private String phone;

}
package uz.javharbek.oauth.specification.authUser;

import lombok.Data;

@Data
public class AuthUserSearchCriteria {
    private String userName;
    private String email;
    private String phone;
}

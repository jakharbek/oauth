package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class ServerCdnDTO {
    String host;
    String username;
    String password;
    String uploadPath;
    String publicPath;
    String alias;
}

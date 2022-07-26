package uz.javharbek.oauth.dto;

import java.util.List;

public class CheckTokenResponseDTO {
    public String user_name;
    public List<String> scope;
    public boolean active;
    public int exp;
    public List<String> authorities;
    public String jti;
    public String client_id;
}

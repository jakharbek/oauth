package uz.javharbek.oauth.exceptions;

public class AuthTokenExpiredException extends AppException{
    public AuthTokenExpiredException(String message) {
        super(message);
    }
}

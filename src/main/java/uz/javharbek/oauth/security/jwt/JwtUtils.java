package uz.javharbek.oauth.security.jwt;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;


@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Autowired
	TokenStore tokenStore;

	public String getUserNameFromJwtToken(String token) throws IOException {
		return tokenStore.readAuthentication(token).getName();
	}

	public boolean validateJwtToken(String authToken) throws IOException {
		if(tokenStore.readAuthentication(authToken) == null){
			return false;
		}
		return tokenStore.readAuthentication(authToken).isAuthenticated() && !tokenStore.readAccessToken(authToken).isExpired();
	}
}

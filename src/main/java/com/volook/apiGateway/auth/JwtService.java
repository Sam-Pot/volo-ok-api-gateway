package com.volook.apiGateway.auth;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.volook.apiGateway.userManagement.services.UserService;
import userManager.UserOuterClass.User;

@Service
public class JwtService {
	
	@Value("${security.jwt.token.secret-key}")
	private String JWT_SECRET;
	@Value("${security.jwt.token.lifetime}")
	private Integer JWT_LIFETIME;
	private final String ROLE_CLAIM_KEY = "role";
	@Autowired
	private UserService userService;
	
	public String generateJwtToken(String emailAddress, String password) {
		try {
			//VERIFICA SE LE CREDENZIALI SONO VALIDE
			boolean isAuthenticated = this.userService.checkCredentials(emailAddress, password);
			if(!isAuthenticated) {
				throw new JWTVerificationException("INVALID CREDENTIALS");
			}
			User user = this.userService.findOneByEmail(emailAddress);
			String role = user.getRole().toString();
			//GENERA IL TOKEN
			Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
			return JWT.create()
					.withSubject(user.getId())
					.withClaim(ROLE_CLAIM_KEY, role)
					.withExpiresAt(LocalDateTime.now().plusHours(JWT_LIFETIME).toInstant(OffsetDateTime.now().getOffset()))
					.sign(algorithm);
		} catch (JWTCreationException exception) {
			throw new JWTCreationException("Error while generating token", exception);
	    }
	}
	
	public String validateToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
			return JWT.require(algorithm)
					.build()
					.verify(token)
					.getSubject();
		} catch (JWTVerificationException exception) {
			throw new JWTVerificationException("Error while validating token", exception);
	    }
	}
	
	public String validateClaim(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
			return JWT.require(algorithm)
					.build()
					.verify(token)
					.getClaim(ROLE_CLAIM_KEY).toString();
		} catch (JWTVerificationException exception) {
			throw new JWTVerificationException("Error while validating token", exception);
	    }
	}
}

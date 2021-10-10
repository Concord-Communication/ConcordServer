package io.github.concord_communication.web_server.service;

import io.github.concord_communication.web_server.model.User;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

@Service
public class TokenService {
	private PrivateKey privateKey = null;

	private final JwtParser parser;

	public TokenService() {
		this.parser = Jwts.parserBuilder()
				.requireIssuer("Concord")
				.setSigningKey(getPrivateKey())
				.build();
	}

	public String createToken(Authentication auth) {
		User user = (User) auth.getPrincipal();
		return Jwts.builder()
				.setSubject(user.getUsername())
				.claim("id", user.getId())
				.setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
				.setIssuer("Concord")
				.signWith(getPrivateKey())
				.compact();
	}

	public Authentication getAuthentication(String token) {
		Jws<Claims> jws;
		try {
			jws = this.parser.parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			return null;
		}
		var claims = jws.getBody();
		var user = new User(claims.get("id", Long.class), claims.getSubject(), null);
		return new UsernamePasswordAuthenticationToken(user, token, new ArrayList<>());
	}

	private PrivateKey getPrivateKey() {
		if (this.privateKey == null) {
			try {
				byte[] keyBytes = Files.readAllBytes(Path.of("private_key.der"));
				PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
				KeyFactory kf = KeyFactory.getInstance("RSA");
				this.privateKey = kf.generatePrivate(spec);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return privateKey;
	}
}

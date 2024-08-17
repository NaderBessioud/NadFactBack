package tn.famytech.esprit.Services;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

@Service
public class JWTService {
	
	public String generateToken(UserDetails userdetails) {
			
			return Jwts.builder().setSubject(userdetails.getUsername())
					.setIssuedAt(new Date(System.currentTimeMillis()))
					
					.setExpiration(new Date(System.currentTimeMillis()+ 1000 *60 *24))
					.signWith(getSignKey(), SignatureAlgorithm.HS256)
					.compact();
		}
	
	public String generateRefreshToken(Map<String, Object> extractClaims, UserDetails userdetails) {
		return Jwts.builder().setClaims(extractClaims).setSubject(userdetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+ 604800000))
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	private Key getSignKey() {
		byte[] key = Decoders.BASE64.decode("413F4428472B4B6250655368566D5970337336763979244226452948404D6351");
		return Keys.hmacShaKeyFor(key);
	}
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	private <T> T extractClaim(String token, Function<Claims, T> claimResolvers) {
		final Claims claims = extractAllClaims(token);
		return claimResolvers.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	public boolean TokenIsValid(String token, UserDetails details) {
		final String useremail = extractUsername(token);
		return (useremail.equals(details.getUsername()) && !isTokenExpired(token));
	}
	
	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date() );
	}

}

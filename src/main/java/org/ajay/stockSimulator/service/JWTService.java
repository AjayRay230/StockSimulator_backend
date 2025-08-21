package org.ajay.stockSimulator.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.ajay.stockSimulator.model.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;




import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

  private final KeyProvider keyProvider;

  public JWTService(KeyProvider keyProvider) {
    this.keyProvider = keyProvider;
  }
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles",user.getRole());
        claims.put("userId",user.getUserId());

        return Jwts.builder().claims()
                .add(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration( new Date(System.currentTimeMillis()+1000*60*60))
                .and()
                .signWith(keyProvider.getSecretKey())
                .compact();

    }

//    private SecretKey getKey() {
//        byte[] bytes = Decoders.BASE64.decode(secrectKey);
//        return Keys.hmacShaKeyFor(bytes);
//    }

    public String extractUsername(String token) {
      return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimResolver) {
      final Claims claims = extractAllClaim(token);
      return claimResolver.apply(claims);
    }

    private Claims extractAllClaim(String token) {
      return Jwts.parser()
              .verifyWith(keyProvider.getSecretKey())
              .build()
              .parseClaimsJws(token)
              .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
      final String username = extractUsername(token);
      return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
      return extractClaim(token, Claims::getExpiration);
    }
}

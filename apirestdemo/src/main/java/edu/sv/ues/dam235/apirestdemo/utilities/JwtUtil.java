package edu.sv.ues.dam235.apirestdemo.utilities;

import edu.sv.ues.dam235.apirestdemo.dtos.TokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private final Set<String> invalidatedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extactAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extactAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public TokenDTO generateToken(String userName, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap();
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("authorities", authorities);
        return createToken(claims, userName);
    }

    private TokenDTO createToken(Map<String, Object> claims, String userName) {

        TokenDTO newToken = new TokenDTO();
        newToken.setExpireIn(String.valueOf(expirationMs));

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();

        newToken.setToken(token);

        return newToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validatedTokenPermission(String token) {
        try {
            if (token == null || invalidatedTokens.contains(token)) {
                return false;
            }
            String email = extractUsername(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email, null, new ArrayList<>());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;

        } catch (Exception e) {
            return false;
        }

    }

    public void invalidateToken(String token) {
        if (token != null) {
            invalidatedTokens.add(token);
        }
        SecurityContextHolder.clearContext();
    }

}

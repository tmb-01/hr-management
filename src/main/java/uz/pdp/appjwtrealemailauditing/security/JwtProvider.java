package uz.pdp.appjwtrealemailauditing.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import uz.pdp.appjwtrealemailauditing.entity.Role;

import java.util.Date;
import java.util.Set;

@Component
public class JwtProvider {

    private static final long expireTime = 1000 * 60 * 60 * 24;
    private static final String secretKey = "maraim01";

    public String generateToken(String username, Set<Role> roles) {

        Date expireDate = new Date(System.currentTimeMillis() + expireTime);

        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String getEmailFromToken(String token) {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}

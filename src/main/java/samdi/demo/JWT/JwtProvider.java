package samdi.demo.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey key; // 토큰에 서명할 비밀키
    private final long validity = 3600000; // 토큰이 유효한 시간, 밀리초 기준, 3600000 = 1시간

    public JwtProvider(@Value("${jwt.secret}") String secret) { // properties 혹은 yml파일에서 jwt.secret값을 읽어오고 문자열 secret변수에 값을 넣음
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)); // 문자열 secret변수를 기반으로 HMAC_SHA 방식으로 키를 생성하고 이 키를 이용해 JWT에 서명할 수 있음
    }

    public String createToken(String username, String role) { // 토큰 생성, username과 role(권한)을 받아 JWT토큰으로 만들고 이를 리턴함
        Date now = new Date(); // 현재시간
        Date exp = new Date(now.getTime() + validity); // 만료시간 = 현재시간 + 아까 정한 validity(1시간)

        return Jwts
                .builder()
                .subject(username) // Subject에는 username
                .claim("role", role) // claim에는 권한(role)
                .issuedAt(now) // 현재 시간
                .expiration(exp) // 만료 시간
                .signWith(key) // HS256알고리즘 사용 및 키를 이용해 서명
                .compact(); // 결과적으로 문자열 형태의 토큰값 생성
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser() // 토큰 안에 있는 정보를 꺼내기 위한 parser생성
                .verifyWith(key) // 토큰의 성명을 확인하기 위한 비밀 키
                .build()
                .parseSignedClaims(token) // 토큰의 내용을 검사하고 해석함 (토큰을 파싱함)
                .getPayload(); // 토큰에 있는 payload(내용)값을 꺼냄
        return claims.get("username", String.class); // JWT 토큰 안에 있는 값을 문자열타입으로 꺼냄
    } // claims는 실제 토큰에 담겨있는 정보(payload)임

    public String getRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) { // 토큰 검증
        try {
            Jws <Claims> jwsClaims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = jwsClaims. getPayload();
            Date exp = claims.getExpiration();

            Date now = new Date();
            return exp != null && exp.after(now); // 만약 토큰의 만료시간이 null이 아니고 현재시간 보다 이후면 true반환

        } catch (Exception e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        if (jwt != null && jwt.startsWith("Bearer ")) {
            return jwt.substring(7);
        }
        return null;
    }
}

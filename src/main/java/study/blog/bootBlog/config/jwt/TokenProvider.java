package study.blog.bootBlog.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import study.blog.bootBlog.domain.User;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
// 토큰을 생성하고 올바른 토큰인지 유효성 검사 및 토큰에서 필요한 정보를 가져오는 클래스
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // 1. JWT 토큰 생성 메소드 : 인자는 만료 시간, 유저 정보를 받음
    // set 계열의 메소드를 통해 여러 값을 지정 (헤더 : 타입, 내용 : iss(발급자), iat(발급일시), exp(만료일시), sub(토큰 제목)
    // 클레임에는 유저 ID 지정 
    // 토큰을 만들 때 프로퍼티즈 파일에 선언해둔 비밀값과 함께 HS256 방식으로 암호화
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
                // 내용 iss : vkslvkd092@naver.com(propertise 파일에서 설정한 값)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now) // 내용 iat : 현재 시간
                .setExpiration(expiry) // 내용 exp : expiry 멤버 변숫값
                .setSubject(user.getEmail()) // 내용 sub : 유저의 이메일
                .claim("id", user.getId()) // 클레임 id : 유저 ID
                // 서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    // 2. JWT 토큰 유효성 검증 메소드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    // 3. 토큰 기반으로 인증 정보를 가져오는 메소드
    // 토큰을 받아 인증 정보를 담은 객체 Authentication 반환 메소드
    // 프로퍼티즈 파일에 저장한 비밀값으로 토큰을 복호화, 클레임을 가져오는 private 메소드인 getClaims() 호출 -> 클레임 정보 반환
    // 사용자 이메일이 들어 있는 제목 sub와 토큰 기반 인증 정보 생성
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(),
                "", authorities), token, authorities);
        // UsernamePasswordAuthenticationToken의 첫 인자로 들어가는 User는 프로젝트에서 만든 User (X)
        // 스프링 시큐리티에서 제공하는 객체인 User 클래스 임포트 !!!
    }

    // 4. 토큰 기반으로 유저 ID를 가져오는 메소드
    // 프로퍼티즈(application.yml)에 저장한 비밀값으로 토큰 복호화 -> 클레임을 가져오는 private 메소드 getClaims() 호출
    // 클레임 정보 반환, 클레임에서 id 키로 저장된 값을 가져와 반환.
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser() // 클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}

package study.blog.bootBlog.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import study.blog.bootBlog.BootBlogApplication;
import study.blog.bootBlog.domain.User;
import study.blog.bootBlog.repository.UserRepository;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = BootBlogApplication.class)
// SpringBoot 에서 생성한
public class TokenProviderTest extends DefaultJwtBuilder {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    // 1. generateToken() 검증 테스트 : 토큰 생성하는 메소드를 테스트하는 메소드
    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있음")
    @Test
    void generateToken() {
        //given : 토큰에 유저 정보를 추가하기 위한 테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        //when : 토큰 제공자의 generateToken() 메소드를 호추해 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        //then : jjwt 라이브러리를 사용해 토큰 복호화 
        //       토큰을 만들 때 클레임으로 넣어둔 id 값이 given절에서 만든 유저 ID와 동일한지 확인
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
        
        assertThat(userId).isEqualTo(testUser.getId());
    }
    
    // 2. validToken() 검증 테스트 : 토큰이 유효한 토큰인지 검증
    @DisplayName("validToken() : 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given : jjwt 라이브러리를 사용해 토큰 생성
        //         만료 시간은 1970년 1월 1일부터 현재 시간을 밀리초 단위로 치환한 값(new Date~)에 1000을 빼 이미 만료된 토큰으로 생성
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() -
                        Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when : 토큰 제공자의 validToken() 메소드를 호출해 유효한 토큰인지 검증한 뒤 결과값을 반환
        boolean result = tokenProvider.validToken(token);

        // then : 반환값이 false(유효한 토큰 X)인 것을 확인
        assertThat(result).isFalse();
    }

    @DisplayName("validToken() : 유효한 토큰일 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given : jjwt 라이브러리를 사용해 토큰 생성
        //         만료 시간은 현재 시간으로부터 14일 뒤로, 만료되지 않은 토큰으로 생성
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        // when : 토큰 제공자인 validToken() 메소드를 호출해 유효한 토큰인지 검증한 뒤 결괏값 반환
        boolean result = tokenProvider.validToken(token);

        // then : 반환값이 true(유효한 토큰 O)인 것을 확인
        assertThat(result).isTrue();
    }

    // 3. getAuthentication() 검증 테스트 : 토큰을 전달받아 인증 정보를 담은 객체 Authentication을 반환하는
    //     메소드 getAuthentication() 테스트 
    @DisplayName("getAuthentication() : 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // given : jjwt 라이브러리를 사용해 토큰 생성
        //         토큰 제목인 subject는 "user@email.com" 이라는 값 사용
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when : 토큰 제공자의 getAuthentication() 메소드를 호출해 인증 객체를 반환
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then : 반환받은 인증 객체의 유저 이름을 가져와 given절에서 설정한 subject 값인 "user@email.com"
        //        과 같은지 확인
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    // 4. getUserId() 검증 테스트 : 토큰 기반 유저 ID를 가져오는 메소드를 테스트하는 메소드
    //                            토큰을 properties 파일에 저장한 비밀값으로 복호화한 뒤 클레임을 가져오는
    //                            private 메소드인 getClaims() 호출 -> 클레임 정보를 반환받아 클레임에서 id 키로 
    //                            저장된 값을 가져와 반환
    @DisplayName("getUserId() : 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        //given : jjwt 라이브러리를 사용해 토큰 생성
        //        클레임을 추가 ( 키는 id, 값은 1 이라는 유저 ID)
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when : 토큰 제공자의 getUserId() 메소드를 호출해 유저 ID를 반환
        Long userIdByToken = tokenProvider.getUserId(token);

        // then : 반환받은 유저 ID가 given절에서 설정한 유저 ID값인 1과 같은지 확인
        assertThat(userIdByToken).isEqualTo(userId);
    }
}

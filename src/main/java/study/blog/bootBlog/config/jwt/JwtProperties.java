package study.blog.bootBlog.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt") // 자바 클래스에 프로피티 값을 가져와서 사용하는 어노테이션
public class JwtProperties {
    // jwt 토큰의 값들을 변수로 접근하는 데 사용하는 JwtProperties

    private String issuer; // jwt 토큰 발급자 

    private String secretKey; // jwt 토큰 비밀키

}

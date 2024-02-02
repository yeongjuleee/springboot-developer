package study.blog.bootBlog.config.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import study.blog.bootBlog.domain.User;
import study.blog.bootBlog.repository.UserRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    // 리소스 서버에서 보내주는 사용자 정보를 불러오는 메소드 loadUser() 를 통해 사용자 조회,
    // user 테이블에 사용자 정보가 있다면 이름을 업데이트, 없다면 saveOrUpdate() 메소드를 실행해 users 테이블에 회원 데이터 추가
    private final UserRepository userRepository;

    // 부모 클래스인 DefaultOAuth2UserService에서 제공하는 OAuth 서비스에서 제공하는 정보를 기반으로
    // 유저 객체를 만들어주는 loadUser() 메소드를 사용해 사용자 객체를 불러옴
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws
            OAuth2AuthenticationException {
        //요청을 바탕으로 유저 정보를 담은 객체 반환
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);
        return user;
    }

    // saveOrUpdate() : 유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build());
        return userRepository.save(user);

    }
}

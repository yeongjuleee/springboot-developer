package study.blog.bootBlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import study.blog.bootBlog.domain.User;
import study.blog.bootBlog.repository.UserRepository;

@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // 사용자 이름(email)으로 사용자의 정보를 가져오는 메소드
    @Override
    public User loadUserByUsername(String email) {
        // 필수로 구현해야 하는 loadUserByUsername() 메소드
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }
}

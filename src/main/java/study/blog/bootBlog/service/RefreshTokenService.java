package study.blog.bootBlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.blog.bootBlog.domain.RefreshToken;
import study.blog.bootBlog.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
// 리프레시 토큰을 전달받아 토큰 제공자를 사용해 새로운 액세스 토큰을 만드는 토큰 서비스 클래스
public class RefreshTokenService {

    // 전달받은 리프레시 토큰으로 리프레시 토큰 객체를 검색해서 전달하는
    // findByRefreshToken()
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalArgumentException("Unexpected token"));
    }
}

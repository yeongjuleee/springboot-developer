package study.blog.bootBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.blog.bootBlog.domain.RefreshToken;

import java.util.Optional;

// Repository : DB에 접근하기 위해 생성한 클래스
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // JpaRepository 클래스를 상속받을 때 엔티티 RefreshToken 과 엔티티의 PK 타입 Long을 인수로 넣음
    // 리포지토리를 사용할 때 JpaReposiroty에서 제공하는 여러 메소드 사용 가능

    Optional<RefreshToken> findByUserId(Long userId);
    // 1. userId로 사용자 정보를 가져옴(사용자 식별 -> 사용자 이름)
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    // 2. refreshToken 으로 데이터 베이스의 저장하는 정보를 가져옴
}

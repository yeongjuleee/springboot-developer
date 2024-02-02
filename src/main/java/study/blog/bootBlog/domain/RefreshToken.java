package study.blog.bootBlog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity // 엔티티로 지정
public class RefreshToken {
    // 리프레시 토큰 : 데이터베이스에 저장하는 정보

    @Id // id 필드를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키를 자동으로 1씩 증가 
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "user_id", updatable = false, unique = true)
    private Long userId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    public RefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }


}

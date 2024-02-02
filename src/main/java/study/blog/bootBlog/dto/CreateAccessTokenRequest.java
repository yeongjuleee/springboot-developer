package study.blog.bootBlog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 토큰 생성 요청 및 응답 담당
public class CreateAccessTokenRequest {
    private String refreshToken;
}

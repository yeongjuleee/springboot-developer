package study.blog.bootBlog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
// 토큰 생성 요청 및 응답 담당
public class CreateAccessTokenResponse {
    private String accessToken;
}

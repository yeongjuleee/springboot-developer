package study.blog.bootBlog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import study.blog.bootBlog.dto.CreateAccessTokenRequest;
import study.blog.bootBlog.dto.CreateAccessTokenResponse;
import study.blog.bootBlog.service.TokenService;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    // 실제 요청을 받고 처리할 컨트롤러
    private final TokenService tokenService;

    // /api/token POST 요청이 오면 토큰 서비스에서 리프레시 토큰을 기반으로
    // 새로운 액세스 토큰을 만들어 주면 됨
    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken
    (@RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }

}

package study.blog.bootBlog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import study.blog.bootBlog.config.jwt.TokenProvider;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    // 필터는 실제로 요청이 전달되기 전과 후에 URL 패턴에 맞는 모든 요청을 처리하는 기능 제공

    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 요청 헤더에서 키가 Authorization 인 필드의 값을 요청
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        // 가져온 값에서 접두사 제거 메소드 getAccessToken()
        String token = getAccessToken(authorizationHeader);

        // 가져온 토큰이 유효한지 확인 후, 유효할 때에 인증 정보 설정
        if (tokenProvider.validToken(token)) {
            // 인증 정보를 관리하는 시큐리티 컨텍스트에 인증 정보 설정
            Authentication authentication = tokenProvider.getAuthentication(token);
            
            // 인증 정보가 설정된 이후 컨텍스트 폴더에서 getAuthentication() 메소드를 사용해
            // 인증 정보를 가져오면, 유저 객체가 반환됨
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    // 접두사 제거
    private String getAccessToken(String authorizationHeader) {
        // 헤더 값이 null 이 아니고, Bearer 로 시작하는 경우, Bearer 값을 빼고 리턴
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        // 아니라면 null 값으로 반환
        return null;
    }
}

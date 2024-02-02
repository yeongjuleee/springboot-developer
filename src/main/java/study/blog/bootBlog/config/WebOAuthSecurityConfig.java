package study.blog.bootBlog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import study.blog.bootBlog.config.jwt.TokenProvider;
import study.blog.bootBlog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import study.blog.bootBlog.config.oauth.OAuth2SuccessHandler;
import study.blog.bootBlog.config.oauth.OAuth2UserCustomService;
import study.blog.bootBlog.repository.RefreshTokenRepository;
import study.blog.bootBlog.service.UserService;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure() {
        //스프링 시큐리티 기능 비활성화
        return (web) -> web.ignoring()
                .requestMatchers("/img/**", "/css/**", "/js/**");
        //.requestMatchers() : 특정 요청과 일치하는 url에 대한 엑세스 설정
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. 토큰 방식으로 인증을 하기 때문에 기존에 사용하던 폼 로그인, 세션 비활성화
        http.csrf(CsrfConfigurer::disable); //csrf 비활성화
        http.cors(CorsConfigurer::disable); //cors
        http
                .httpBasic(AbstractHttpConfigurer::disable) //httpBasic 비활성화
                .formLogin(AbstractHttpConfigurer::disable) //기존 formLogin 비활성화
                .logout(AbstractHttpConfigurer::disable);   //

        //세션 관리 정책을 STATELESS
        //세션이 있으면 쓰지도 않고, 없으면 만들지도 않음
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 2. 헤더값을 확인할 커스텀 필터(TokenAuthenticationFilter()) 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 3. 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요
        http
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/api/token").permitAll()
                                .requestMatchers("/api/**").authenticated()
                                .anyRequest().permitAll()
                );
        http
                .oauth2Login((oauth2Login) ->
                        oauth2Login
                                .loginPage("/login")
                                .authorizationEndpoint(authorization -> authorization
                                        //4. Authorization 요청과 관련된 관련된 상태 저장
                                        //OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸 수 있도록
                                        //인증 요청과 관련된 상태를 저장할 저장소 설정
                                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                );
        http
                .oauth2Login((oauth2Login) ->
                        oauth2Login
                                .successHandler(oAuth2SuccessHandler())    //5. 인증 성공 시 실행할 핸들러 설정
                                .userInfoEndpoint(userInfo ->
                                        userInfo
                                                .userService(oAuth2UserCustomService))
                );
        http
                .logout(httpSecurityLogoutConfigurer ->
                        httpSecurityLogoutConfigurer
                                .logoutSuccessUrl("/login"));


        //6. /api로 시작하는 url인 경우 인증 실패 시 401 상태 코드를 반환하도록 예외 처리
        http
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                        new AntPathRequestMatcher("/api/**")));

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(), userService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

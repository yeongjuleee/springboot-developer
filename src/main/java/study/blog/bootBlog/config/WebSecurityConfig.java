//package study.blog.bootBlog.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import study.blog.bootBlog.service.UserDetailService;
//
//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity
//// 인증을 위한 도메인과 레포지터리, 서비스를 이용하여 실제 인증 처리를 하는 시큐리티 설정 파일
//// OAuth2와 JWT를 함께 사용하기 위해 다른 설정을 해야함! -> 기존 폼 로그인 방식인 내용 모두 주석처리
//public class WebSecurityConfig {
//
//    private final UserDetailService userService;
//    // private final BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    // 1. 스프링 시큐리티 기능 비활성화
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web -> web.ignoring()
//                // requestMatchers() : 특정 요청과 일치하는 url에 대한 액세스를 설정
//                .requestMatchers("/static/**"));
//    }
//
//    // 2. 특정 http 요청에 대한 웹 기반 보안 구성
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//                .authorizeHttpRequests((authorizeRequests) ->
//                        authorizeRequests
//                                .requestMatchers("/login").permitAll()
//                                .requestMatchers("/signup").permitAll()
//                                .requestMatchers("/user").permitAll()
//                                .anyRequest().authenticated()
//                )    // 3. 인증, 인가 설정
//                .formLogin((formLogin) ->
//                        formLogin
//                                .loginPage("/login")
//                                .defaultSuccessUrl("/articles"))    // 4. 폼 기반 로그인 설정
//
//                .logout((logoutConfig) ->
//                        logoutConfig.logoutSuccessUrl("/login")
//                                .invalidateHttpSession(true))   // 5. 로그아웃 설정
//
//                .csrf((csrfConfig) ->
//                        csrfConfig.disable());   // 6. csrf 비활성화
//
//        return http.build();
//    }
//
//    // 7. 인증 관리자 관련 설정
//    /* 책에서 알려준 내용. 하지만 이용할 수 없음
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http,
//                                                       BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService)
//            throws Exception {
//        return http
//                .getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(userService) // 8. 사용자 정보 서비스 설정
//                .passwordEncoder(bCryptPasswordEncoder)
//                .and().build();
//    }
//    */
//
//    /*
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
//        return auth.build();
//    }
//    */
//
//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//
//        daoAuthenticationProvider.setUserDetailsService(userService);
//        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
//
//        return daoAuthenticationProvider;
//    }
//
//    // 9. 패스워드 인코더로 사용할 빈 등록
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}

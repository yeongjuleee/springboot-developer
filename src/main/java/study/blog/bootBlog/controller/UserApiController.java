package study.blog.bootBlog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import study.blog.bootBlog.dto.AddUserRequest;
import study.blog.bootBlog.service.UserService;

@RequiredArgsConstructor
@Controller
public class UserApiController {
    
    // 회원 가입 폼에서 회원 가입 요청을 받으면 서비스 메소드를 사용해 사용자를 저장 -> 로그인 페이지로 이동하는
    // signup() 메소드
    
    private final UserService userService;
    
    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request); // 회원 가입 메소드 호출
        return "redirect:/login"; // 회원 가입이 완료된 이후 로그인 페이지로 이동
    }

    // logout() : 로그아웃 메소드
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 로그아웃을 담당하는 핸들러 : SecurityContextLogoutHandler의 logout() 메소드 호출 -> 로그아웃
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}

package study.blog.bootBlog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {
    // 로그인, 회원 가입 경로로 접근하기 위한 뷰 파일을 연결하는 컨트롤러

    @GetMapping("/login")
    public String login() {
//        return "login";
        // /login 경로로 접근하면 login() 메소드가 login.html 반환

        return "oauthLogin"; //
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
        // /signup 경로로 접근하면 signup() 메소드가 signup.html 반환
    }
}

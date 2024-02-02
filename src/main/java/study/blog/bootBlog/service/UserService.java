package study.blog.bootBlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import study.blog.bootBlog.domain.User;
import study.blog.bootBlog.dto.AddUserRequest;
import study.blog.bootBlog.repository.UserRepository;

@RequiredArgsConstructor
@Service
// AddUserRequest 객체를 인수로 받는 회원 정보 추가 메소드
public class UserService {

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;  
// private final BCryptPasswordEncoder : JWT 토큰 인증 시 필요함. OAuth2 에서는 사용 X 임으로 주석처리    
    
//  JWT 토큰 인증 시 사용하는 내용     
//    public Long save(AddUserRequest dto) {
//        return userRepository.save(User.builder()
//                .email(dto.getEmail())
//                //패스워드 암호화
//                //패스워드를 저장할 때 시큐리티 설정,
//                //패스워드 인코딩용으로 등록한 빈을 사용해서 암호화한 후 저장
//                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
//                .build()).getId();
//    }

    // OAuth2 인증 시 사용하는 내용
    public Long save(AddUserRequest dto) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // BCryptPasswordEncoder를 생성자를 사용해 직접 생성해서 패스워드를 암호화 할 수 있게 코드 수정
        // 이후 findByEmail() 메소드 추가
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                //패스워드 암호화
                //패스워드를 저장할 때 시큐리티 설정,
                //패스워드 인코딩용으로 등록한 빈을 사용해서 암호화한 후 저장
                .password(encoder.encode(dto.getPassword()))
                .build()).getId();
    }

    // userID로 유저를 검색해서 전달하는 findById()
    public  User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("Unexpected user"));
    }

    // findByEmail() : 이메일을 입력받아 users 테이블에서 유저를 찾고, 없으면 예외를 발생
    // OAuth2에서 제공하는 이메일은 유일 값이므로 해당 메소드를 사용해 유저를 찾을 수 있음
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("Unexpected user"));
    }
}

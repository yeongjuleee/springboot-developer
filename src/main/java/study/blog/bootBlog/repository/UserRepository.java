package study.blog.bootBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.blog.bootBlog.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<study.blog.bootBlog.domain.User, Long> {
    Optional<User> findByEmail(String email); // 1. email로 사용자 정보를 가져옴(사용자 식별 -> 사용자 이름)

    // 사용자 정보를 가져오기 위해서 스프링 시큐리티각 이메일을 전달받아야 함.
    // 스프링 데이터 JPA는 메소드 규칙에 맞춰 메소드를 선언하면 이름을 분석해 자동으로 쿼리를 생성
    // ex) findByEmail() 메소드는 실제 DB에 회원 정보를 요청할 때 다음 쿼리를 실행함
    //     FROM users
    //     WHERE email = #{email}
}

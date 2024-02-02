package study.blog.bootBlog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails { // UserDetails를 상속받아 인증 객체로 사용
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
    
    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자가 가지고 있는 권한 목록 반환 (현재 코드에는 사용자 외 권한이 없기 때문에 user 권한만 담아 반환)
        return List.of(new SimpleGrantedAuthority("user"));
    }
    
    // 사용자의 id를 반환(고유한 값)
    @Override
    public String getUsername() {
        // 식별할 수 있는 사용자 이름 반환 메소드 (사용자의 이름은 반드시 고유해야 함!)
        return email;
    }
    
    // 사용자의 패스워드 반환
    @Override
    public String getPassword() {
        // 사용자의 비밀번호 반환 메소드
        return password;
    }
    
    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        // 만료되었는지 확인하는 로직
        return true; // true : 아직 만료되지 않았음
    }
    
    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // true : 잠금되지 않았음
    }

    // 패스워드의 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true : 만료되지 않았음
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true : 사용가능
    }

    // OAuth 서비스 기능 구현 관련 코드
    // 사용자 정보를 조회해 users 테이블에 사용자 정보가 있다면 리소스 서버에서 제공해주는 이름을 업데이트
    // 없다면 users 테이블에 새 사용자를 생성해 DB에 저장하는 서비스를 구현함
    
    // 관련 키를 저장하는 코드 추가
    // 사용자 이름
    @Column(name = "nickname", unique = true)
    private String nickname;

    // 사용자 이름 변경
    public User update(String nickname) {
        this.nickname = nickname;

        return this;
    }
}

package study.blog.bootBlog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity // 엔티티로 지정
@Getter // 엔터티의 값을 가져오는 어노테이션
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 
public class Article {

    @Id // id 필드를 기본키로 지정함
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키를 자동으로 1씩 증가
    @Column(name ="id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false) // 'title'이라는 not null 컬럼과 매핑
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    // 엔티티에 요청 받은 내용으로 값을 수정하는 메소드
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 엔티티에 생성 시간과 수정 시간을 추가해 글이 언제 생성되었는지 확인
    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "author", nullable = false)
    private String author; // 글쓴이

    // 빌더 패턴에서도 author를 추가해 객체를 생성할 때 글쓴이를 입력받을 수 있게 변경

    @Builder // 빌더 패턴으로 객체 생성
    public Article(String author ,String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }
}

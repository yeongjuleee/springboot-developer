package study.blog.bootBlog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.blog.bootBlog.domain.Article;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
// 블로그 글을 추가하는 서비스 계층에서 요청받을 객체 생성
public class AddArticleRequest {
    // DTO : data transfer object, 계층끼리 데이터를 교환하기 위해 사용하는 객체로 데이터를 옮기기 위한 전달자 역할

    private String title;
    private String content;

    public Article toEntity(String author) { // 생성자를 사용해 객체 생성
        // toEntity() : 빌더 패턴을 사용해 DTO를 엔티티로 만들어주는 메소드 
        //              추후 블로그 글을 추가할 때 저장할 엔티티로 변환하는 용도
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}

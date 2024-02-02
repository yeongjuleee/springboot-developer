package study.blog.bootBlog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import study.blog.bootBlog.domain.Article;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ArticleViewResponse {
    // 뷰에서 사용할 DTO 생성

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    private String author;

    public ArticleViewResponse(Article article) {
        this.id = article.getId();;
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
        this.author = article.getAuthor(); // 글 상세 페이지에서 글쓴이 정보가 보여야 함
    }
}

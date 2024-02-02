package study.blog.bootBlog.dto;

import lombok.Getter;
import study.blog.bootBlog.domain.Article;

@Getter
public class ArticleResponse {
    // /api/articles 의 GET 요청이 오면 응답을 위한 DTO 생성
    private final String title;
    private final String content;
    
    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();
    }
    
}

package study.blog.bootBlog.dto;

import lombok.Getter;
import study.blog.bootBlog.domain.Article;

@Getter
public class ArticleListViewResponse {
    // 뷰에게 데이터를 전달하기 위한 객체 생성

    private final Long id;
    private final String title;
    private final String content;

    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}

package study.blog.bootBlog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import study.blog.bootBlog.domain.Article;
import study.blog.bootBlog.dto.ArticleListViewResponse;
import study.blog.bootBlog.dto.ArticleViewResponse;
import study.blog.bootBlog.service.BlogService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {
    // /article GET 요청을 처리할 코드 작성 
    
    private final BlogService blogService; 
    
    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles", articles); // 1. 블로그 글 리스트 저장 
        // addAttribute() 를 이용하여 모델에 값 저장. 
        
        return "articleList"; // 2. articleList.html 라는 뷰 조회
        // 반환값인 "articleList"는 resource/templates/articleKist.html을 찾도록 뷰 이름 명시
    }

    // 블로그 글을 반환할 컨트롤러 메소드 getArticle() 메소드
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        // getArticle() 메소드는 인자에 id에 URL로 넘어온 값을 받아 findById() 메소드로 넘겨 글을 조회 & 화면에서 사용할 모델에 데이터를 저장한 다음
        // 보여줄 화면의 템플릿 이름을 반환

        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    // 수정 화면을 보여주기 위한 메소드 newArticle()
    @GetMapping("/new-article")
    // 1. id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id는 없을 수도 있다.)
    public String newArticle(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            // 2. id가 없으면 생성
            model.addAttribute("article", new ArticleViewResponse());
        } else {
            // 3. id가 있으면 수정, 없으면 기본 생성자를 이용해 빈 ArticleViewResponse 객체를 만들고, id가 있으면 기존 값을 가져오는 findById() 메소드 호출
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle";
    }
}

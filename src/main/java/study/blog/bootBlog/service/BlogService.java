package study.blog.bootBlog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import study.blog.bootBlog.domain.Article;
import study.blog.bootBlog.dto.AddArticleRequest;
import study.blog.bootBlog.dto.UpdateArticleRequest;
import study.blog.bootBlog.repository.BlogRepository;

import java.util.List;

@RequiredArgsConstructor    //final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service    //빈으로 서블릿 컨테이너에 등록
public class BlogService {
    //클라이언트는 DB에 접근할 수 없기 때문에 API 구현하여 접근할 수 있도록 함

    private final BlogRepository blogRepository;

    //블로그 글 추가 메소드
    public Article save(AddArticleRequest request, String userName) {
        // 유저 이름을 추가로 입력받고 toEntity()의 인수로 전달받은 유저 이름을 반환하도록 코드 수정
        return blogRepository.save(request.toEntity(userName));
    }

    //블로그 글 목록 조회를 위한 API 구현
    public List<Article> findAll() {
        return blogRepository.findAll();
        //findAll(): JPA 지원 메소드, article 테이블에 저장된 모든 데이터 조회
    }

    //블로그 글 하나를 조회하는 메소드
    public Article findById(long id) {
        //JPA에서 제공하는 findById() 메소드를 사용해 ID를 받아 엔티티를 조회하고 없으면
        //IllegalArgumentException 예외를 발생시킴
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    //블로그 글 삭제
    public void delete(long id) {
        // blogRepository.deleteById(id); (토큰 X 시 사용)
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    //블로그 글 수정
    @Transactional  //트랜잭션 메소드
    // 트랜잭션 : 데이터 처리 중 오류가 발생하면 원상태로 되돌림(?) 즉, 데이터 변경 처리를 돕는 어노테이션
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    // 게시글 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
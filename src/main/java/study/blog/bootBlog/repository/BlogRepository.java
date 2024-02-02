package study.blog.bootBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.blog.bootBlog.domain.Article;

public interface BlogRepository extends JpaRepository<Article, Long> {
    // JpaRepository 클래스를 상속받을 때 엔티티 Article과 엔티티의 PK 타입 Long을 인수로 넣음
    // 리포지토리를 사용할 때 JpaReposiroty에서 제공하는 여러 메소드 사용 가능
}

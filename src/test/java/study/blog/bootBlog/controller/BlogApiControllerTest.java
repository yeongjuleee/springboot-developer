package study.blog.bootBlog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import study.blog.bootBlog.domain.Article;
import study.blog.bootBlog.domain.User;
import study.blog.bootBlog.dto.AddArticleRequest;
import study.blog.bootBlog.dto.UpdateArticleRequest;
import study.blog.bootBlog.repository.BlogRepository;
import study.blog.bootBlog.repository.UserRepository;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 테스트용 애플리케이션 컨텍스트
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    // MockMvc : 애플리케이션을 배포하지 않고도 서버의 MVC 동작을 테스트 할 수 있는 라이브러리
    // 가짜 객체를 만들어 애플리케이션 서버에 배포하지 않고도 스프링 MVC 동작을 재현할 수 있는 클래스

    @Autowired
    protected ObjectMapper objectMapper; // 직렬화(java -> JSON), 역직렬화(JSON -> Java)를 위한 클래스

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());
    }

    @BeforeEach // 테스트 실행 전 실행하는 메소드
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                user.getAuthorities()));
    }

    // 블로그 글 생성 API 테스트 코드
    @DisplayName("addArticle: 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        // given : 블로그 글 추가에 필요한 요청 객체를 만듦
        //         어떤 상황이 주어졌을 때 (이 데이터를 기반으로 함)
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        // 객체 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        // when : 블로그 글 추가 API를 보냄
        //        이때 요청 타입은 JSON이며, given 절에서 미리 만들어둔 객체를 요청 본문으로 함께 보냄
        //        ~를 실행했을 때 (검증할 것을 실행)
        // 설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        // then : 응답코드가 201 Created인지 확인.
        //        검증한 결과가 ~가 나와야 함
        //        Blog를 전체 조회해 크기가 1인지 확인 -> 실제 저장된 데이터와 요청 값을 비교
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        // assertThat(테스트 타켓.메소드1().메소드2().메소드3()) : 단위 테스트 코드를 사용하기 위해 사용
        // assertThat(actual).isEqualTo(expected) : actual(실제값)이 expected(기대값)과 내용이 같은지 검증
        assertThat(articles.size()).isEqualTo(1); // 크기가 1인지 검증
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    // 블로그 글 목록 조회 API 테스트 코드 생성
    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다")
    @Test
    public void findAllArticles() throws Exception {

        // given
        final String url = "/api/articles";

        Article savedArticle = createDefaultArticle();

        // when
        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()));
    }

    // 블로그 글 한 개 조회 테스트
    @DisplayName("findArticle: 블로그 글 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
        // given
        final String url = "/api/articles/{id}";

        Article savedArticle = createDefaultArticle();

        // when
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()))
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()));
    }
    
    // 블로그 글 삭제 API 테스트
    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {
        //given
        final String url = "/api/articles/{id}";

        Article savedArticle = createDefaultArticle();
        
        // when 
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());
        
        // then 
        List<Article> articles = blogRepository.findAll();

        assertThat(articles.isEmpty());
    }

    //글 수정 테스트
    @DisplayName("deleteArticle: 블로그 글 수정에 성공!")
    @Test
    public void updateArticle() throws Exception {
        //given: 블로그 글을 저장하고, 수정에 필요한 요청 객체를 만든다
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        final String newTitle = "new Title";
        final String newContent = "new Content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        //when: update api로 수정 요청을 보냄
        //요청 타입 = JSON, given절에서 미리 만들어둔 객체를 요청 본문으로 함께 보냄
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsBytes(request)));

        //then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);

    }
}
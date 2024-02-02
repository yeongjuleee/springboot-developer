package study.blog.bootBlog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import study.blog.bootBlog.config.jwt.JwtFactory;
import study.blog.bootBlog.config.jwt.JwtProperties;
import study.blog.bootBlog.domain.RefreshToken;
import study.blog.bootBlog.domain.User;
import study.blog.bootBlog.dto.CreateAccessTokenRequest;
import study.blog.bootBlog.repository.RefreshTokenRepository;
import study.blog.bootBlog.repository.UserRepository;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // @SpringBootTest : 테스트에 필요한 모든 빈(Bean)을 가져와 수행 => 느림
                //                   운영 환경과 가장 유사하게 테스트 가능
@AutoConfigureMockMvc // @AutoConfigureMockMvc : Mock(=실제를 흉내낸 가짜 객체) 테스트시 필요한 의존성 제공
                      //                         MockMvc 객체를 통해 실제 컨테이너가 실행되는 것은 X but 로직상 테스트 가능
                      //                         @WebMvcTest 가 아닌 @SpringBootTest 에서도 Mock 테스트를 가능하게 해주는 역할
public class TokenApiControllerTest {

    @Autowired // @Autowired : 알아서 의존객체(Bean)을 찾아 주입
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    // ObjectMapper : JSON 형식을 사용할 때, 응답을 직렬화 하고 요청을 역직렬화 할 때 사용하는 기술
    @Autowired
    private WebApplicationContext context;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach // @BeforeEach : 테스트 메소드 실행 이전에 먼저 실행되는 어노테이션
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }


    // 토큰을 생성하는 메소드 createNewAccessToken() 에 대한 테스트 코드 
    @DisplayName("createNewAccessToken : 새로운 액세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        // 1. given : 테스트 유저를 생성
        //         jjwt 라이브러리를 이용해 리프레시 토큰을 만들어 DB에 저장
        //         토큰 생성 API의 요청 본문에 리프레시 토큰을 포함하여 요청 객체 생성
        final String url = "/api/token";

        // 1) 테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // 2) jjwt 라이브러리를 이용하여 리프레스 토큰을 만들어
        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        // 2-2) 만든 리프레시 토큰을 리프레시토큰레포지토리를 이용하여 DB에 정보 저장
        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        // 3) 토큰 생성 API의 요청 분문에 리프레시 토큰을 포함하여 요청 객체 생성
        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);

        final String requestBody = objectMapper.writeValueAsString(request);

        // when : 토큰 추가 API에 요청을 보냄
        //        요청 타입은 JSON이며, given절에서 미리 만들어둔 객체를 요청 본문으로 함께 보냄
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then : 응답 코드가 201 Created인지 확인
        //        응답으로 온 액세스 토큰이 비어 있지 않은지 확인
        resultActions
                .andExpect(status().isCreated()) // andExpect : 응답 코드를 확인 하는 메소드
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
                // jsonPath(문자열임) 의 값이 accessToken이 비어있지 않은지 확인
    }

}

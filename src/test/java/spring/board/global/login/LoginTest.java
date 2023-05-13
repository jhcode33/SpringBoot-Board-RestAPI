package spring.board.global.login;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import spring.board.domain.member.Member;
import spring.board.domain.member.Role;
import spring.board.domain.member.repository.MemberRepository;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

	@Autowired 
	MockMvc mockMvc;
	
	@Autowired 
	MemberRepository memberRepository;
	
	@Autowired 
	EntityManager em;
	
	PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "username";
    private static String PASSWORD = "123456789";
    private static String LOGIN_URL = "/login";

    private void clear(){
        em.flush();
        em.clear();
    }
    
    @BeforeEach
    private void init(){
            memberRepository.save(Member.builder()
                    .username(USERNAME)
                    .password(delegatingPasswordEncoder.encode(PASSWORD))
                    .name("Member1")
                    .nickName("NickName1")
                    .role(Role.USER)
                    .age(22)
                    .build());
            clear();
    }
    
    //== Test를 위해 클라이언트에서 넘겨주는 정보를 Map객체로 생성하는 코드 ==>
    private Map getUsernamePasswordMap(String username, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }
	
    //== Test를 위해 POST URL 요청을 생성하는 코드 ==//
    private ResultActions perform(String url, MediaType mediaType, Map usernamePasswordMap) throws Exception{
    	return mockMvc.perform(MockMvcRequestBuilders.post(url)
    												 .contentType(mediaType)
    												 .content(objectMapper.writeValueAsString(usernamePasswordMap)));
    }
    
    @Test
    public void 로그인_성공() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when, then
        MvcResult result = perform(LOGIN_URL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
    
    @Test
    public void 로그인_실패_아이디불일치() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME+"123", PASSWORD);

        //when, then
        MvcResult result = perform(LOGIN_URL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }
    
    @Test
    public void 로그인_실패_비밀번호불일치() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD+"123");


        //when, then
        MvcResult result = perform(LOGIN_URL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }
    
    @Test
    public void 로그인_URL불일치_FORBIDDEN() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when, then
        perform(LOGIN_URL+"123", APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isForbidden());
        
    }
    
    @Test
    public void 로그인_데이터형식_JSON이_아니면_200() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        //when, then
        perform(LOGIN_URL, APPLICATION_FORM_URLENCODED, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
    
    @Test
    public void 로그인_HTTP_METHOD_GET이면_NOTFOUND() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .get(LOGIN_URL)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    @Test
    public void 오류_로그인_HTTP_METHOD_PUT이면_NOTFOUND() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put(LOGIN_URL)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
}

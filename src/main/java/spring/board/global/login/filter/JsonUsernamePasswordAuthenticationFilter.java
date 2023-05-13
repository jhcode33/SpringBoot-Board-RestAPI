package spring.board.global.login.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	
	//기본 로그인 요청 URL 지정 /login
	private static final String DEFAULT_LOGIN_REQUEST_URL = "/login";
	
	//HTTP_Method 요청 방식을 post로 지정
	private static final String HTTP_METHOD = "POST";
	
	//json 타입의 데이터로만 로그인을 진행함
	private static final String CONTENT_TYPE = "application/json";
	
	//ObjectMapper는 JSON 데이터와 Java 객체 간의 변환을 처리하는 Jackson 라이브러리의 핵심 클래스
	private final ObjectMapper objectMapper;
	
	//json 타입의 데이터에서 값을 가져올 때 사용할 key를 지정
	private static final String USERNAME_KEY="username";
	private static final String PASSWORD_KEY="password";
	
	//AntPathRequestMatcher 객체는 로그인 요청이 들어왔을 때 URL과 Method를 검증하는데 사용한다.
	private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER 
					   = new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD);
	
	//생성자
	//super을 통해서 부모클래스의 생성자에게 해당 인자를 전달한다.
	// /login GET 요청에 대해서 처리한다고 되어있는데 이해가 되지 않는다.
	public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
		super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
		this.objectMapper = objectMapper;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		//Content가 json 타입인지 확인, null이거나 json타입이 아니면 오류를 생성
		if(request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
			throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
		}
		
		//json 타입일 경우
		//copyToString() 메서드는 InputStream에서 문자열을 읽어와서 문자열을 반환하는 메서드
		String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
		
		//generic 오류가 발생할 경우 import를 확인
		//readValue() 첫번째 인자로 JSON 데이터, 두번쨰 인자로 Java로 변환될 클래스의 타입이 들어간다.
		//JSON 데이터도 key, value 형태로 되어있기 때문에 key와 value를 가지는 Map 타입으로 쉽게 변환이 가능하다
		Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
		
		String username = usernamePasswordMap.get(USERNAME_KEY);
		String password = usernamePasswordMap.get(PASSWORD_KEY);
		
		//Spring Security에서 제공하는 인증을 위한 토큰 클래스
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		
		return this.getAuthenticationManager().authenticate(authRequest);
	}
}

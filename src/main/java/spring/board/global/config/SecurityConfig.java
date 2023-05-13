package spring.board.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import spring.board.domain.member.service.LoginService;
import spring.board.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import spring.board.global.login.handler.LoginFailureHandler;
import spring.board.global.login.handler.LoginSuccessJWTProvideHandler;

@Configuration
@RequiredArgsConstructor // final , @NonNull 이 붙은 필드를 인자로 받는 생성자
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	//== ObjectMapper, LoginService, 생성자를 통해 주입함 ==//
	private final ObjectMapper objectMapper;
	private final LoginService loginService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().disable()
			.httpBasic().disable()
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			
			.and()
			.authorizeRequests()
			.antMatchers("/login", "/signUp","/").permitAll()
			.anyRequest()
			.authenticated();
		
		//추가한 부분//
		http.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class);
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	//== AuthenticationManager 등록 ==//
	@Bean
	public AuthenticationManager authenticationManager(){
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(loginService);
		return new ProviderManager(provider);
	}
	
	//== LoginSuccessJWTProvideHandler 생성 코드 ==//
	@Bean
	public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler() {
		return new LoginSuccessJWTProvideHandler();
	}
	
	//== LoginFailureHandler 생성 코드 ==//
	@Bean
	public LoginFailureHandler loginFailureHandler() {
		return new LoginFailureHandler();
	}
	
	//== JsonUsernamePasswordAuthenticationFilter 등록 ==//
	@Bean
	public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() {
		JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
		jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
		jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
		jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
		return jsonUsernamePasswordLoginFilter;
	
	}
	
	
	
}

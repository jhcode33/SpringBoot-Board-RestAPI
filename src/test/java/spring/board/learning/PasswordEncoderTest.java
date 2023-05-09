package spring.board.learning;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	private static String PASSWROD = "정희jhcode";
	
	//@Test
	public void 패스워드_암호화() throws Exception {
	    //given
	    String password = "정희jhcode";

	    //when
	    String encodePassword = passwordEncoder.encode(password);

	    //then
	    assertThat(encodePassword).startsWith("{");
	    assertThat(encodePassword).contains("{bcrypt}");
	    assertThat(encodePassword).isNotEqualTo(password);

	}
	
	//@Test
    public void 패스워드_랜덤_암호화() throws Exception {

        //when
        String encodePassword = passwordEncoder.encode(PASSWROD);
        String encodePassword2 = passwordEncoder.encode(PASSWROD);

        //then
        assertThat(encodePassword).isNotEqualTo(encodePassword2);

    }
	
	@Test
    public void 암호화된_비밀번호_매치() throws Exception {

        //when
        String encodePassword = passwordEncoder.encode(PASSWROD);

        //then
        assertThat(passwordEncoder.matches(PASSWROD, encodePassword)).isTrue();

    }

}

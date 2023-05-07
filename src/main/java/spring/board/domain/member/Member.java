package spring.board.domain.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.board.domain.BaseTimeEntity;
 
@Table(name = "MEMBER")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {
	
	//primary key, member을 구별할 식별자
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;
	
	//사용자의 아이디, 비밀번호, NotNull, Unique, 길이 30
	@Column(nullable = false, unique = true, length = 30)
	private String username;
	private String password;
	
	//사용자의 이름
	@Column(nullable = false, length = 30)
	private String name;
	
	//사용자의 별명
	@Column(nullable = false, length = 30)
	private String nickName;
	
	//사용자의 나이
	@Column(nullable = false, length = 30)
	private Integer age;
	
	//사용자의 권한
	//JPA에서 열거형(Enum) 타입의 값을 데이터베이스에서 사용할 때 어떤 형태로 사용할 것인지를 지정해주는 어노테이션
	//열거형이란? Enum 클래스에서 연관된 상수들의 집합을 나타내는 데이터 타입이다.
	@Enumerated(EnumType.STRING)
	private Role role;
	
	//간단한 비즈니스 로직이 있으므로 도메인 객체라고도 할 수 있나?
	//== 비밀번호 암호화 ==//
	//Spring Security가 가지고 있는 passwordEncoder 인터페이스를 사용함.
	//사용자가 입력한 비밀번호를 받아 encode하여 암호화하여 저장함.
	public void encodePassword(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(password);
	}
	
	//== 정보 수정 메서드 == //
	//비밀번호 수정
	public void updatePassword(PasswordEncoder passwordEncoder, String password) {
		this.password = passwordEncoder.encode(password);
	}
	
	//이름 수정
	public void updateName(String name) {
		this.name = name;
	}
	
	//닉네임 수정
	public void updateNickName(String nickName) {
		this.nickName = nickName;
	}
	
	//나이 수정
	public void updateAge(Integer age) {
		this.age = age;
	}
}

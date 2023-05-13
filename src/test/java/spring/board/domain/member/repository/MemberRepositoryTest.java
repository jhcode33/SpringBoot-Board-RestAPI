package spring.board.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import spring.board.domain.member.Member;
import spring.board.domain.member.Role;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    
    //Test 메서드를 하나의 트랜잭션으로 처리할 경우, 여러 개의 트랜잭션이 실행될 경우
    //해당 트랙잭션을 바로 수행 flush(), 해당 트랜잭션 수행 후 트랜잭션 버리기 clear()을 사용해주어야한다.
    //만약 @Transactional을 사용하지 않고 DB에 직접 CRUD 작업을 한다면, 아래 코드는 없어도 된다.
//    private void clear(){
//        em.flush();
//        em.clear();
//    }
    
    @AfterEach
    private void after(){
        em.clear();
    }


    //@Test
    public void 회원저장_성공() throws Exception {
    	//given 조건이 주어졌을 때
	    Member member = Member.builder().username("username")
									    .password("1234567890")
									    .name("Member1")
										.nickName("NickName1")
										.role(Role.USER)
										.age(22)
										.build();
        //when 동작하였을 때
        Member saveMember = memberRepository.save(member);

        //then 조건과 동작 후의 결과 비교
        Member findMember = memberRepository.findById(saveMember.getId()).orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다"));//아직 예외 클래스를 만들지 않았기에 RuntimeException으로 처리하겠습니다.

        //assertThat(findMember).isEqualToComparingFieldByFieldRecursively(saveMember);
        assertThat(findMember).usingRecursiveComparison()
        					  .ignoringFields("createdDate", "lastModifiedDate")
        					  .isEqualTo(saveMember);
        //assertThat(findMember).isEqualTo(member);
    }
    
    //@Test
    public void 오류_회원가입시_아이디가_없음() throws Exception {
        //given
        Member member = Member.builder().password("1234567890").name("Member1").nickName("NickName1").role(Role.USER).age(22).build();
        //when, then
        assertThrows(Exception.class, () -> {memberRepository.save(member);}, "예외가 발생하지 않았습니다.");
    }
    
    //@Test
    public void 오류_회원가입시_이름이_없음() throws Exception {
        //given
        Member member = Member.builder().username("username").password("1234567890").nickName("NickName1").role(Role.USER).age(22).build();
        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member));
    }
    
    //@Test
    public void 오류_회원가입시_닉네임이_없음() throws Exception {
        //given
        Member member = Member.builder().username("username").password("1234567890").name("Member1").role(Role.USER).age(22).build();

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member));
    }
    
    //@Test
    public void 오류_회원가입시_나이가_없음() throws Exception {
        //given
        Member member = Member.builder().username("username").password("1234567890").name("Member1").role(Role.USER).nickName("NickName1").build();

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member));
    }
    
    //@Test
    public void 오류_회원가입시_중복된_아이디가_있음() throws Exception {
        //given
        Member member1 = Member.builder().username("username").password("1234567890").name("Member1").role(Role.USER).nickName("NickName1").age(22).build();
        Member member2 = Member.builder().username("username").password("1111111111").name("Member2").role(Role.USER).nickName("NickName2").age(22).build();
       
        memberRepository.save(member1);

        //when, then
        assertThrows(Exception.class, () -> memberRepository.save(member2));
    }
    
    @Test
    public void 성공_회원수정() throws Exception {
        //given
        Member member1 = Member.builder()
        					   .username("username")
        					   .password("1234567890")
        					   .name("Member1")
        					   .role(Role.USER)
        					   .nickName("NickName1")
        					   .age(22).build();
        
        memberRepository.save(member1);
        
        String updatePassword = "updatePassword";
        String updateName = "updateName";
        String updateNickName = "updateNickName";
        int updateAge = 33;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //when
        Member findMember = memberRepository.findById(member1.getId()).orElseThrow(() -> new Exception());
        findMember.updateAge(updateAge);
        findMember.updateName(updateName);
        findMember.updateNickName(updateNickName);
        findMember.updatePassword(passwordEncoder,updatePassword);
        
        memberRepository.save(findMember);

        //then
        Member findUpdateMember = memberRepository.findById(findMember.getId()).orElseThrow(() -> new Exception());

        assertThat(findUpdateMember).usingRecursiveComparison()
        							.ignoringFields("createdDate", "lastModifiedDate")
        							.isEqualTo(findMember);
        
        assertThat(passwordEncoder.matches(updatePassword, findUpdateMember.getPassword())).isTrue();
        assertThat(findUpdateMember.getName()).isEqualTo(updateName);
        assertThat(findUpdateMember.getName()).isNotEqualTo(member1.getName());
    }
    
    //@Test
    public void 성공_회원삭제() throws Exception {
        //given
        Member member1 = Member.builder().username("username").password("1234567890").name("Member1").role(Role.USER).nickName("NickName1").age(22).build();
        memberRepository.save(member1);

        //when
        memberRepository.delete(member1);

        //then
        assertThrows(Exception.class, () -> memberRepository.findById(member1.getId()).orElseThrow(() -> new Exception()));
    }
    
    //@Test
    public void existByUsername_정상작동() throws Exception {
        //given
        String username = "username";
        Member member1 = Member.builder()
        					   .username(username)
        					   .password("1234567890")
        					   .name("Member1")
        					   .role(Role.USER)
        					   .nickName("NickName1")
        					   .age(22)
        					   .build();
        
        memberRepository.save(member1);
     
        //when, then
        assertThat(memberRepository.existsByUsername(username)).isTrue();
        assertThat(memberRepository.existsByUsername(username+"123")).isFalse();
    }
    
    //@Test
    public void findByUsername_정상작동() throws Exception {
        //given
        String username = "username";
        Member member1 = Member.builder()
        					   .username(username)
        					   .password("1234567890")
        					   .name("Member1")
        					   .role(Role.USER)
        					   .nickName("NickName1")
        					   .age(22)
        					   .build();
        
        memberRepository.save(member1);

        //when, then
        assertThat(memberRepository.findByUsername(username).get().getUsername()).isEqualTo(member1.getUsername());
        assertThat(memberRepository.findByUsername(username).get().getName()).isEqualTo(member1.getName());
        assertThat(memberRepository.findByUsername(username).get().getId()).isEqualTo(member1.getId());
        assertThrows(Exception.class,() -> memberRepository.findByUsername(username+"123")
        			.orElseThrow(() -> new Exception()));

    }
    
    //@Test
    public void 회원가입시_생성수정시간_등록() throws Exception {
        //given
        Member member1 = Member.builder().username("username").password("1234567890").name("Member1").role(Role.USER).nickName("NickName1").age(22).build();
        memberRepository.save(member1);
        
        //when
        Member findMember = memberRepository.findById(member1.getId()).orElseThrow(() -> new Exception());

        //then
        assertThat(findMember.getCreatedDate()).isNotNull();
        assertThat(findMember.getLastModifiedDate()).isNotNull();
    }
    
    
}

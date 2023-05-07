package spring.board.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.board.domain.member.Member;
 
//@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	
	Optional<Member> findByUsername(String username);
	
	boolean existsByUsername(String username);

}
